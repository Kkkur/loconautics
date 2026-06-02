/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.peripheral.PeripheralCapability
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.signal;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.compat.computercraft.events.SignalStateChangeEvent;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import java.util.List;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class SignalBlockEntity
extends SmartBlockEntity
implements TransformableBlockEntity {
    public TrackTargetingBehaviour<SignalBoundary> edgePoint;
    private SignalState state = SignalState.INVALID;
    private OverlayState overlay = OverlayState.SKIP;
    private int switchToRedAfterTrainEntered;
    private boolean lastReportedPower = false;
    public AbstractComputerBehaviour computerBehaviour;

    public SignalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (Mods.COMPUTERCRAFT.isLoaded()) {
            event.registerBlockEntity(PeripheralCapability.get(), (BlockEntityType)AllBlockEntityTypes.TRACK_SIGNAL.get(), (be, context) -> be.computerBehaviour.getPeripheralCapability());
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        NBTHelper.writeEnum((CompoundTag)tag, (String)"State", (Enum)this.state);
        NBTHelper.writeEnum((CompoundTag)tag, (String)"Overlay", (Enum)this.overlay);
        tag.putBoolean("Power", this.lastReportedPower);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.state = (SignalState)NBTHelper.readEnum((CompoundTag)tag, (String)"State", SignalState.class);
        this.overlay = (OverlayState)NBTHelper.readEnum((CompoundTag)tag, (String)"Overlay", OverlayState.class);
        this.lastReportedPower = tag.getBoolean("Power");
        this.invalidateRenderBoundingBox();
    }

    @Nullable
    public SignalBoundary getSignal() {
        return this.edgePoint.getEdgePoint();
    }

    public boolean isPowered() {
        return this.state == SignalState.RED;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.edgePoint = new TrackTargetingBehaviour<SignalBoundary>(this, EdgePointType.SIGNAL);
        behaviours.add(this.edgePoint);
        this.computerBehaviour = ComputerCraftProxy.behaviour(this);
        behaviours.add(this.computerBehaviour);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            return;
        }
        SignalBoundary boundary = this.getSignal();
        if (boundary == null) {
            this.enterState(SignalState.INVALID);
            this.setOverlay(OverlayState.RENDER);
            return;
        }
        BlockState blockState = this.getBlockState();
        blockState.getOptionalValue((Property)SignalBlock.POWERED).ifPresent(powered -> {
            if (this.lastReportedPower == powered) {
                return;
            }
            this.lastReportedPower = powered;
            boundary.updateBlockEntityPower(this);
            this.notifyUpdate();
        });
        blockState.getOptionalValue(SignalBlock.TYPE).ifPresent(stateType -> {
            SignalBlock.SignalType targetType = boundary.getTypeFor(this.worldPosition);
            if (stateType != targetType) {
                this.level.setBlock(this.worldPosition, (BlockState)blockState.setValue(SignalBlock.TYPE, (Comparable)((Object)targetType)), 3);
                this.refreshBlockState();
            }
        });
        this.enterState(boundary.getStateFor(this.worldPosition));
        this.setOverlay(boundary.getOverlayFor(this.worldPosition));
    }

    public boolean getReportedPower() {
        return this.lastReportedPower;
    }

    public SignalState getState() {
        return this.state;
    }

    public OverlayState getOverlay() {
        return this.overlay;
    }

    public void setOverlay(OverlayState state) {
        if (this.overlay == state) {
            return;
        }
        this.overlay = state;
        this.notifyUpdate();
    }

    public void enterState(SignalState state) {
        if (this.switchToRedAfterTrainEntered > 0) {
            --this.switchToRedAfterTrainEntered;
        }
        if (this.state == state) {
            return;
        }
        if (state == SignalState.RED && this.switchToRedAfterTrainEntered > 0) {
            return;
        }
        this.state = state;
        int n = this.switchToRedAfterTrainEntered = state == SignalState.GREEN || state == SignalState.YELLOW ? 15 : 0;
        if (this.computerBehaviour.hasAttachedComputer()) {
            this.computerBehaviour.prepareComputerEvent(new SignalStateChangeEvent(state));
        }
        this.notifyUpdate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.computerBehaviour.removePeripheral();
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(Vec3.atLowerCornerOf((Vec3i)this.worldPosition), Vec3.atLowerCornerOf((Vec3i)this.edgePoint.getGlobalPosition())).inflate(2.0);
    }

    @Override
    public void transform(BlockEntity be, StructureTransform transform) {
        this.edgePoint.transform(be, transform);
    }

    public static enum SignalState {
        RED,
        YELLOW,
        GREEN,
        INVALID;


        public boolean isRedLight(float renderTime) {
            return this == RED || this == INVALID && renderTime % 40.0f < 3.0f;
        }

        public boolean isYellowLight(float renderTime) {
            return this == YELLOW;
        }

        public boolean isGreenLight(float renderTime) {
            return this == GREEN;
        }
    }

    public static enum OverlayState {
        RENDER,
        SKIP,
        DUAL;

    }
}
