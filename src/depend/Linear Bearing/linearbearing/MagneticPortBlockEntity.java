/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bearing.linearbearing;

import com.bearing.linearbearing.MagneticPortBlock;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MagneticPortBlockEntity
extends GeneratingKineticBlockEntity {
    public float receivedSpeed = 0.0f;
    private boolean receivedThisTick = false;
    private boolean transmitting = false;
    private BlockPos visualTargetPos = null;
    private int particleCooldown = 0;

    public MagneticPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public float getGeneratedSpeed() {
        return this.receivedSpeed;
    }

    public boolean isSource() {
        return true;
    }

    public boolean isTransmitting() {
        return this.transmitting;
    }

    public @Nullable BlockPos getVisualTargetPos() {
        return this.visualTargetPos;
    }

    public void receiveWirelessSpeed(float newSpeed) {
        this.receiveWirelessSpeed(newSpeed, null);
    }

    public void receiveWirelessSpeed(float newSpeed, BlockPos visualFrom) {
        this.receivedThisTick = true;
        if (this.receivedSpeed != newSpeed) {
            this.receivedSpeed = newSpeed;
            this.updateGeneratedRotation();
        }
        boolean bl = this.transmitting = newSpeed != 0.0f;
        if (visualFrom != null) {
            this.visualTargetPos = visualFrom;
        }
        this.setChanged();
        this.sendData();
    }

    public void tick() {
        BlockState state;
        super.tick();
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        if (this.getSpeed() != 0.0f && this.receivedSpeed == 0.0f && (state = this.getBlockState()).getBlock() instanceof MagneticPortBlock) {
            Vec3 endVec;
            Direction facing = (Direction)state.getValue((Property)MagneticPortBlock.FACING);
            Vec3 directionVec = Vec3.atLowerCornerOf((Vec3i)facing.getNormal());
            Vec3 startVec = new Vec3((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5).add(directionVec.scale(0.51));
            ClipContext context = new ClipContext(startVec, endVec = startVec.add(directionVec.scale(3.5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)null);
            BlockHitResult hitResult = this.level.clip(context);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos hitPos = hitResult.getBlockPos();
                BlockEntity targetBE = null;
                Level level = this.level;
                if (level instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)level;
                    targetBE = serverLevel.getBlockEntity(hitPos);
                    if (!(targetBE instanceof MagneticPortBlockEntity)) {
                        for (ServerLevel otherLevel : serverLevel.getServer().getAllLevels()) {
                            BlockEntity tempBE;
                            if (otherLevel == serverLevel || !((tempBE = otherLevel.getBlockEntity(hitPos)) instanceof MagneticPortBlockEntity)) continue;
                            targetBE = tempBE;
                            break;
                        }
                    }
                } else {
                    targetBE = this.level.getBlockEntity(hitPos);
                }
                if (targetBE instanceof MagneticPortBlockEntity) {
                    Direction hitFace;
                    Direction targetFacing;
                    MagneticPortBlockEntity targetPort = (MagneticPortBlockEntity)targetBE;
                    BlockState targetState = targetBE.getLevel().getBlockState(hitPos);
                    targetPort.receiveWirelessSpeed(this.getSpeed(), this.worldPosition);
                    this.transmitting = this.getSpeed() != 0.0f;
                    this.visualTargetPos = hitPos;
                    this.setChanged();
                    this.sendData();
                    if (targetState.hasProperty((Property)MagneticPortBlock.FACING) && (targetFacing = (Direction)targetState.getValue((Property)MagneticPortBlock.FACING)) == (hitFace = hitResult.getDirection())) {
                        targetPort.receiveWirelessSpeed(this.getSpeed());
                    }
                }
            }
        }
        if (!this.receivedThisTick && this.receivedSpeed != 0.0f) {
            this.receivedSpeed = 0.0f;
            this.updateGeneratedRotation();
            this.setChanged();
            this.sendData();
        }
        if (!this.receivedThisTick && this.transmitting) {
            this.transmitting = false;
            this.visualTargetPos = null;
            this.setChanged();
            this.sendData();
        }
        this.receivedThisTick = false;
    }

    public float calculateAddedStressCapacity() {
        if (this.receivedSpeed != 0.0f) {
            return Math.abs(this.receivedSpeed) * 16.0f;
        }
        return 0.0f;
    }

    public float calculateStressApplied() {
        if (this.getSpeed() != 0.0f && this.receivedSpeed == 0.0f) {
            return Math.abs(this.getSpeed()) * 16.0f;
        }
        return super.calculateStressApplied();
    }

    public int getRotationAngleOffset(Direction.Axis axis) {
        if (this.level == null) {
            return super.getRotationAngleOffset(axis);
        }
        BlockState state = this.getBlockState();
        if (state.hasProperty((Property)MagneticPortBlock.FACING)) {
            boolean hasCogwheel;
            Direction facing = (Direction)state.getValue((Property)MagneticPortBlock.FACING);
            BlockPos backPos = this.worldPosition.relative(facing.getOpposite());
            BlockState backState = this.level.getBlockState(backPos);
            boolean bl = hasCogwheel = backState != null && backState.getBlock().getClass().getSimpleName().contains("Cog");
            if (this.receivedSpeed == 0.0f && this.getSpeed() == 0.0f) {
                return 0;
            }
            if (this.receivedSpeed != 0.0f) {
                return 0;
            }
        }
        return super.getRotationAngleOffset(axis);
    }

    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putFloat("ReceivedWirelessSpeed", this.receivedSpeed);
        compound.putBoolean("Transmitting", this.transmitting);
        if (this.visualTargetPos != null) {
            compound.putInt("VisualTargetX", this.visualTargetPos.getX());
            compound.putInt("VisualTargetY", this.visualTargetPos.getY());
            compound.putInt("VisualTargetZ", this.visualTargetPos.getZ());
        } else {
            compound.putInt("VisualTargetX", Integer.MIN_VALUE);
        }
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.receivedSpeed = compound.getFloat("ReceivedWirelessSpeed");
        this.transmitting = compound.getBoolean("Transmitting");
        if (compound.contains("VisualTargetX")) {
            int vx = compound.getInt("VisualTargetX");
            if (vx != Integer.MIN_VALUE) {
                int vy = compound.getInt("VisualTargetY");
                int vz = compound.getInt("VisualTargetZ");
                this.visualTargetPos = new BlockPos(vx, vy, vz);
            } else {
                this.visualTargetPos = null;
            }
        } else {
            this.visualTargetPos = null;
        }
    }
}
