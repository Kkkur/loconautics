/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.WorldAttached
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.fluids.FluidStack
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidReactions;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public abstract class FluidTransportBehaviour
extends BlockEntityBehaviour {
    public static final BehaviourType<FluidTransportBehaviour> TYPE = new BehaviourType();
    public Map<Direction, PipeConnection> interfaces;
    public UpdatePhase phase = UpdatePhase.WAIT_FOR_PUMPS;
    public static final WorldAttached<Map<BlockPos, Map<Direction, PipeConnection>>> interfaceTransfer = new WorldAttached($ -> new HashMap());

    public FluidTransportBehaviour(SmartBlockEntity be) {
        super(be);
    }

    public boolean canPullFluidFrom(FluidStack fluid, BlockState state, Direction direction) {
        return true;
    }

    public abstract boolean canHaveFlowToward(BlockState var1, Direction var2);

    @Override
    public void initialize() {
        super.initialize();
        this.createConnectionData();
    }

    @Override
    public void tick() {
        boolean onServer;
        super.tick();
        Level world = this.getWorld();
        BlockPos pos = this.getPos();
        boolean bl = onServer = !world.isClientSide || this.blockEntity.isVirtual();
        if (this.interfaces == null) {
            return;
        }
        Collection<PipeConnection> connections = this.interfaces.values();
        PipeConnection singleSource = null;
        if (this.phase == UpdatePhase.WAIT_FOR_PUMPS) {
            this.phase = UpdatePhase.FLIP_FLOWS;
            return;
        }
        if (onServer) {
            boolean sendUpdate = false;
            for (PipeConnection pipeConnection : connections) {
                sendUpdate |= pipeConnection.flipFlowsIfPressureReversed();
                pipeConnection.manageSource(world, pos, this.blockEntity);
            }
            if (sendUpdate) {
                this.blockEntity.notifyUpdate();
            }
        }
        if (this.phase == UpdatePhase.FLIP_FLOWS) {
            this.phase = UpdatePhase.IDLE;
            return;
        }
        if (onServer) {
            int n;
            FluidStack availableFlow = FluidStack.EMPTY;
            FluidStack collidingFlow = FluidStack.EMPTY;
            for (PipeConnection connection : connections) {
                FluidStack fluidInFlow = connection.getProvidedFluid();
                if (fluidInFlow.isEmpty()) continue;
                if (availableFlow.isEmpty()) {
                    singleSource = connection;
                    availableFlow = fluidInFlow;
                    continue;
                }
                if (FluidStack.isSameFluidSameComponents((FluidStack)availableFlow, (FluidStack)fluidInFlow)) {
                    singleSource = null;
                    availableFlow = fluidInFlow;
                    continue;
                }
                collidingFlow = fluidInFlow;
                break;
            }
            if (!collidingFlow.isEmpty()) {
                FluidReactions.handlePipeFlowCollision(world, pos, availableFlow, collidingFlow);
                return;
            }
            boolean bl2 = false;
            for (PipeConnection connection : connections) {
                FluidStack internalFluid = singleSource != connection ? availableFlow : FluidStack.EMPTY;
                Predicate<FluidStack> extractionPredicate = extracted -> this.canPullFluidFrom((FluidStack)extracted, this.blockEntity.getBlockState(), connection.side);
                n |= connection.manageFlows(world, pos, internalFluid, extractionPredicate);
            }
            if (n != 0) {
                this.blockEntity.notifyUpdate();
            }
        }
        for (PipeConnection connection : connections) {
            connection.tickFlowProgress(world, pos);
        }
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        if (this.interfaces == null) {
            this.interfaces = new IdentityHashMap<Direction, PipeConnection>();
        }
        for (Direction face : Iterate.directions) {
            if (!nbt.contains(face.getName())) continue;
            this.interfaces.computeIfAbsent(face, d -> new PipeConnection((Direction)d));
        }
        if (this.interfaces.isEmpty()) {
            this.interfaces = null;
            return;
        }
        this.interfaces.values().forEach(connection -> connection.deserializeNBT(nbt, registries, this.blockEntity.getBlockPos(), clientPacket));
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        if (clientPacket) {
            this.createConnectionData();
        }
        if (this.interfaces == null) {
            return;
        }
        this.interfaces.values().forEach(connection -> connection.serializeNBT(nbt, registries, clientPacket));
    }

    public FluidStack getProvidedOutwardFluid(Direction side) {
        this.createConnectionData();
        if (!this.interfaces.containsKey(side)) {
            return FluidStack.EMPTY;
        }
        return this.interfaces.get(side).provideOutboundFlow();
    }

    @Nullable
    public PipeConnection getConnection(Direction side) {
        this.createConnectionData();
        return this.interfaces.get(side);
    }

    public boolean hasAnyPressure() {
        this.createConnectionData();
        for (PipeConnection pipeConnection : this.interfaces.values()) {
            if (!pipeConnection.hasPressure()) continue;
            return true;
        }
        return false;
    }

    @Nullable
    public PipeConnection.Flow getFlow(Direction side) {
        this.createConnectionData();
        if (!this.interfaces.containsKey(side)) {
            return null;
        }
        return this.interfaces.get((Object)side).flow.orElse(null);
    }

    public void addPressure(Direction side, boolean inbound, float pressure) {
        this.createConnectionData();
        if (!this.interfaces.containsKey(side)) {
            return;
        }
        this.interfaces.get(side).addPressure(inbound, pressure);
        this.blockEntity.sendData();
    }

    public void wipePressure() {
        if (this.interfaces != null) {
            for (Direction d : Iterate.directions) {
                if (!this.canHaveFlowToward(this.blockEntity.getBlockState(), d)) {
                    this.interfaces.remove(d);
                    continue;
                }
                this.interfaces.computeIfAbsent(d, PipeConnection::new);
            }
        }
        this.phase = UpdatePhase.WAIT_FOR_PUMPS;
        this.createConnectionData();
        this.interfaces.values().forEach(PipeConnection::wipePressure);
        this.blockEntity.sendData();
    }

    private void createConnectionData() {
        if (this.interfaces != null) {
            return;
        }
        this.interfaces = new IdentityHashMap<Direction, PipeConnection>();
        for (Direction d : Iterate.directions) {
            if (!this.canHaveFlowToward(this.blockEntity.getBlockState(), d)) continue;
            this.interfaces.put(d, new PipeConnection(d));
        }
    }

    public AttachmentTypes getRenderedRimAttachment(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        if (!this.canHaveFlowToward(state, direction)) {
            return AttachmentTypes.NONE;
        }
        BlockPos offsetPos = pos.relative(direction);
        BlockState facingState = world.getBlockState(offsetPos);
        if (facingState.getBlock() instanceof PumpBlock && facingState.getValue((Property)PumpBlock.FACING) == direction.getOpposite()) {
            return AttachmentTypes.NONE;
        }
        if (AllBlocks.ENCASED_FLUID_PIPE.has(facingState) && ((Boolean)facingState.getValue((Property)EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(direction.getOpposite()))).booleanValue()) {
            return AttachmentTypes.RIM;
        }
        if (FluidPropagator.hasFluidCapability((BlockGetter)world, offsetPos, direction.getOpposite()) && !AllBlocks.HOSE_PULLEY.has(facingState)) {
            return AttachmentTypes.DRAIN;
        }
        return AttachmentTypes.RIM;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public static void cacheFlows(LevelAccessor world, BlockPos pos) {
        FluidTransportBehaviour pipe = BlockEntityBehaviour.get((BlockGetter)world, pos, TYPE);
        if (pipe != null) {
            ((Map)interfaceTransfer.get(world)).put(pos, pipe.interfaces);
        }
    }

    public static void loadFlows(LevelAccessor world, BlockPos pos) {
        FluidTransportBehaviour newPipe = BlockEntityBehaviour.get((BlockGetter)world, pos, TYPE);
        if (newPipe != null) {
            newPipe.interfaces = (Map)((Map)interfaceTransfer.get(world)).remove(pos);
        }
    }

    public static enum UpdatePhase {
        WAIT_FOR_PUMPS,
        FLIP_FLOWS,
        IDLE;

    }

    public static enum AttachmentTypes {
        NONE(new ComponentPartials[0]),
        CONNECTION(ComponentPartials.CONNECTION),
        DETAILED_CONNECTION(ComponentPartials.RIM_CONNECTOR),
        RIM(ComponentPartials.RIM_CONNECTOR, ComponentPartials.RIM),
        PARTIAL_RIM(ComponentPartials.RIM),
        DRAIN(ComponentPartials.RIM_CONNECTOR, ComponentPartials.DRAIN),
        PARTIAL_DRAIN(ComponentPartials.DRAIN);

        public final ComponentPartials[] partials;

        private AttachmentTypes(ComponentPartials ... partials) {
            this.partials = partials;
        }

        public AttachmentTypes withoutConnector() {
            if (this == RIM) {
                return PARTIAL_RIM;
            }
            if (this == DRAIN) {
                return PARTIAL_DRAIN;
            }
            return this;
        }

        public static enum ComponentPartials {
            CONNECTION,
            RIM_CONNECTOR,
            RIM,
            DRAIN;

        }
    }
}
