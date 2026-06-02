/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids.pump;

import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

public class PumpBlockEntity
extends KineticBlockEntity {
    Couple<MutableBoolean> sidesToUpdate = Couple.create(MutableBoolean::new);
    boolean pressureUpdate;
    boolean scheduleFlip;

    public PumpBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(new PumpFluidTransferBehaviour(this));
        this.registerAwardables(behaviours, FluidPropagator.getSharedTriggers());
        this.registerAwardables(behaviours, AllAdvancements.PUMP);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide && !this.isVirtual()) {
            return;
        }
        if (this.scheduleFlip) {
            this.level.setBlockAndUpdate(this.worldPosition, (BlockState)this.getBlockState().setValue((Property)PumpBlock.FACING, (Comparable)((Direction)this.getBlockState().getValue((Property)PumpBlock.FACING)).getOpposite()));
            this.scheduleFlip = false;
        }
        this.sidesToUpdate.forEachWithContext((update, isFront) -> {
            if (update.isFalse()) {
                return;
            }
            update.setFalse();
            this.distributePressureTo(isFront != false ? this.getFront() : this.getFront().getOpposite());
        });
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        if (Math.abs(previousSpeed) == Math.abs(this.getSpeed())) {
            return;
        }
        if (this.speed != 0.0f) {
            this.award(AllAdvancements.PUMP);
        }
        if (this.level.isClientSide && !this.isVirtual()) {
            return;
        }
        this.updatePressureChange();
    }

    public void updatePressureChange() {
        this.pressureUpdate = false;
        BlockPos frontPos = this.worldPosition.relative(this.getFront());
        BlockPos backPos = this.worldPosition.relative(this.getFront().getOpposite());
        FluidPropagator.propagateChangedPipe((LevelAccessor)this.level, frontPos, this.level.getBlockState(frontPos));
        FluidPropagator.propagateChangedPipe((LevelAccessor)this.level, backPos, this.level.getBlockState(backPos));
        FluidTransportBehaviour behaviour = this.getBehaviour(FluidTransportBehaviour.TYPE);
        if (behaviour != null) {
            behaviour.wipePressure();
        }
        this.sidesToUpdate.forEach(MutableBoolean::setTrue);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (compound.getBoolean("Reversed")) {
            this.scheduleFlip = true;
        }
    }

    protected void distributePressureTo(Direction side) {
        if (this.getSpeed() == 0.0f) {
            return;
        }
        BlockFace start = new BlockFace(this.worldPosition, side);
        boolean pull = this.isPullingOnSide(this.isFront(side));
        HashSet<BlockFace> targets = new HashSet<BlockFace>();
        HashMap<BlockPos, Pair<Integer, Map<Direction, Boolean>>> pipeGraph = new HashMap<BlockPos, Pair<Integer, Map<Direction, Boolean>>>();
        if (!pull) {
            FluidPropagator.resetAffectedFluidNetworks(this.level, this.worldPosition, side.getOpposite());
        }
        if (!this.hasReachedValidEndpoint((LevelAccessor)this.level, start, pull)) {
            ((Map)pipeGraph.computeIfAbsent(this.worldPosition, $ -> Pair.of((Object)0, new IdentityHashMap())).getSecond()).put(side, pull);
            ((Map)pipeGraph.computeIfAbsent(start.getConnectedPos(), $ -> Pair.of((Object)1, new IdentityHashMap())).getSecond()).put(side.getOpposite(), !pull);
            ArrayList<Pair> frontier = new ArrayList<Pair>();
            HashSet<BlockPos> visited = new HashSet<BlockPos>();
            int maxDistance = FluidPropagator.getPumpRange();
            frontier.add(Pair.of((Object)1, (Object)start.getConnectedPos()));
            while (!frontier.isEmpty()) {
                Pair entry = (Pair)frontier.remove(0);
                int distance = (Integer)entry.getFirst();
                BlockPos currentPos = (BlockPos)entry.getSecond();
                if (!this.level.isLoaded(currentPos) || visited.contains(currentPos)) continue;
                visited.add(currentPos);
                BlockState currentState = this.level.getBlockState(currentPos);
                FluidTransportBehaviour pipe = FluidPropagator.getPipe((BlockGetter)this.level, currentPos);
                if (pipe == null) continue;
                for (Direction face : FluidPropagator.getPipeConnections(currentState, pipe)) {
                    BlockFace blockFace = new BlockFace(currentPos, face);
                    BlockPos connectedPos = blockFace.getConnectedPos();
                    if (!this.level.isLoaded(connectedPos) || blockFace.isEquivalent(start)) continue;
                    if (this.hasReachedValidEndpoint((LevelAccessor)this.level, blockFace, pull)) {
                        ((Map)pipeGraph.computeIfAbsent(currentPos, $ -> Pair.of((Object)distance, new IdentityHashMap())).getSecond()).put(face, pull);
                        targets.add(blockFace);
                        continue;
                    }
                    FluidTransportBehaviour pipeBehaviour = FluidPropagator.getPipe((BlockGetter)this.level, connectedPos);
                    if (pipeBehaviour == null || pipeBehaviour instanceof PumpFluidTransferBehaviour || visited.contains(connectedPos)) continue;
                    if (distance + 1 >= maxDistance) {
                        ((Map)pipeGraph.computeIfAbsent(currentPos, $ -> Pair.of((Object)distance, new IdentityHashMap())).getSecond()).put(face, pull);
                        targets.add(blockFace);
                        continue;
                    }
                    ((Map)pipeGraph.computeIfAbsent(currentPos, $ -> Pair.of((Object)distance, new IdentityHashMap())).getSecond()).put(face, pull);
                    ((Map)pipeGraph.computeIfAbsent(connectedPos, $ -> Pair.of((Object)(distance + 1), new IdentityHashMap())).getSecond()).put(face.getOpposite(), !pull);
                    frontier.add(Pair.of((Object)(distance + 1), (Object)connectedPos));
                }
            }
        }
        HashMap<Integer, Set<BlockFace>> validFaces = new HashMap<Integer, Set<BlockFace>>();
        this.searchForEndpointRecursively(pipeGraph, targets, validFaces, new BlockFace(start.getPos(), start.getOppositeFace()), pull);
        float pressure = Math.abs(this.getSpeed());
        for (Set set : validFaces.values()) {
            int parallelBranches = Math.max(1, set.size() - 1);
            for (BlockFace face : set) {
                BlockPos pipePos = face.getPos();
                Direction pipeSide = face.getFace();
                if (pipePos.equals((Object)this.worldPosition)) continue;
                boolean inbound = (Boolean)((Map)((Pair)pipeGraph.get(pipePos)).getSecond()).get(pipeSide);
                FluidTransportBehaviour pipeBehaviour = FluidPropagator.getPipe((BlockGetter)this.level, pipePos);
                if (pipeBehaviour == null) continue;
                pipeBehaviour.addPressure(pipeSide, inbound, pressure / (float)parallelBranches);
            }
        }
    }

    protected boolean searchForEndpointRecursively(Map<BlockPos, Pair<Integer, Map<Direction, Boolean>>> pipeGraph, Set<BlockFace> targets, Map<Integer, Set<BlockFace>> validFaces, BlockFace currentFace, boolean pull) {
        BlockPos currentPos = currentFace.getPos();
        if (!pipeGraph.containsKey(currentPos)) {
            return false;
        }
        Pair<Integer, Map<Direction, Boolean>> pair = pipeGraph.get(currentPos);
        int distance = (Integer)pair.getFirst();
        boolean atLeastOneBranchSuccessful = false;
        for (Direction nextFacing : Iterate.directions) {
            Map map;
            if (nextFacing == currentFace.getFace() || !(map = (Map)pair.getSecond()).containsKey(nextFacing)) continue;
            BlockFace localTarget = new BlockFace(currentPos, nextFacing);
            if (targets.contains(localTarget)) {
                validFaces.computeIfAbsent(distance, $ -> new HashSet()).add(localTarget);
                atLeastOneBranchSuccessful = true;
                continue;
            }
            if ((Boolean)map.get(nextFacing) != pull || !this.searchForEndpointRecursively(pipeGraph, targets, validFaces, new BlockFace(currentPos.relative(nextFacing), nextFacing.getOpposite()), pull)) continue;
            validFaces.computeIfAbsent(distance, $ -> new HashSet()).add(localTarget);
            atLeastOneBranchSuccessful = true;
        }
        if (atLeastOneBranchSuccessful) {
            validFaces.computeIfAbsent(distance, $ -> new HashSet()).add(currentFace);
        }
        return atLeastOneBranchSuccessful;
    }

    private boolean hasReachedValidEndpoint(LevelAccessor world, BlockFace blockFace, boolean pull) {
        IFluidHandler capability;
        BlockPos connectedPos = blockFace.getConnectedPos();
        BlockState connectedState = world.getBlockState(connectedPos);
        BlockEntity blockEntity = world.getBlockEntity(connectedPos);
        Direction face = blockFace.getFace();
        if (PumpBlock.isPump(connectedState) && ((Direction)connectedState.getValue((Property)PumpBlock.FACING)).getAxis() == face.getAxis() && blockEntity instanceof PumpBlockEntity) {
            PumpBlockEntity pumpBE = (PumpBlockEntity)blockEntity;
            return pumpBE.isPullingOnSide(pumpBE.isFront(blockFace.getOppositeFace())) != pull;
        }
        FluidTransportBehaviour pipe = FluidPropagator.getPipe((BlockGetter)world, connectedPos);
        if (pipe != null && pipe.canHaveFlowToward(connectedState, blockFace.getOppositeFace())) {
            return false;
        }
        if (blockEntity != null && (capability = (IFluidHandler)blockEntity.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), (Object)face.getOpposite())) != null) {
            return true;
        }
        return FluidPropagator.isOpenEnd((BlockGetter)world, blockFace.getPos(), face);
    }

    public void updatePipesOnSide(Direction side) {
        if (!this.isSideAccessible(side)) {
            return;
        }
        this.updatePipeNetwork(this.isFront(side));
        this.getBehaviour(FluidTransportBehaviour.TYPE).wipePressure();
    }

    protected boolean isFront(Direction side) {
        BlockState blockState = this.getBlockState();
        if (!(blockState.getBlock() instanceof PumpBlock)) {
            return false;
        }
        Direction front = (Direction)blockState.getValue((Property)PumpBlock.FACING);
        boolean isFront = side == front;
        return isFront;
    }

    @Nullable
    protected Direction getFront() {
        BlockState blockState = this.getBlockState();
        if (!(blockState.getBlock() instanceof PumpBlock)) {
            return null;
        }
        return (Direction)blockState.getValue((Property)PumpBlock.FACING);
    }

    protected void updatePipeNetwork(boolean front) {
        ((MutableBoolean)this.sidesToUpdate.get(front)).setTrue();
    }

    public boolean isSideAccessible(Direction side) {
        BlockState blockState = this.getBlockState();
        if (!(blockState.getBlock() instanceof PumpBlock)) {
            return false;
        }
        return ((Direction)blockState.getValue((Property)PumpBlock.FACING)).getAxis() == side.getAxis();
    }

    public boolean isPullingOnSide(boolean front) {
        return !front;
    }

    class PumpFluidTransferBehaviour
    extends FluidTransportBehaviour {
        public PumpFluidTransferBehaviour(SmartBlockEntity be) {
            super(be);
        }

        @Override
        public void tick() {
            super.tick();
            for (Map.Entry entry : this.interfaces.entrySet()) {
                boolean pull = PumpBlockEntity.this.isPullingOnSide(PumpBlockEntity.this.isFront((Direction)entry.getKey()));
                Couple<Float> pressure = ((PipeConnection)entry.getValue()).getPressure();
                pressure.set(pull, (Object)Float.valueOf(Math.abs(PumpBlockEntity.this.getSpeed())));
                pressure.set(!pull, (Object)Float.valueOf(0.0f));
            }
        }

        @Override
        public boolean canHaveFlowToward(BlockState state, Direction direction) {
            return PumpBlockEntity.this.isSideAccessible(direction);
        }

        @Override
        public FluidTransportBehaviour.AttachmentTypes getRenderedRimAttachment(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
            FluidTransportBehaviour.AttachmentTypes attachment = super.getRenderedRimAttachment(world, pos, state, direction);
            if (attachment == FluidTransportBehaviour.AttachmentTypes.RIM) {
                return FluidTransportBehaviour.AttachmentTypes.NONE;
            }
            return attachment;
        }
    }
}
