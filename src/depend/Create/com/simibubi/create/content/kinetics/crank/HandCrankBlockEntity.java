/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.kinetics.crank;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class HandCrankBlockEntity
extends GeneratingKineticBlockEntity {
    public int inUse;
    public boolean backwards;
    public float independentAngle;
    public float chasingAngularVelocity;

    public HandCrankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void turn(boolean back) {
        boolean update = false;
        if (this.getGeneratedSpeed() == 0.0f || back != this.backwards) {
            update = true;
        }
        this.inUse = 10;
        this.backwards = back;
        if (update && !this.level.isClientSide) {
            this.updateGeneratedRotation();
        }
    }

    public float getIndependentAngle(float partialTicks) {
        return this.independentAngle + partialTicks * this.chasingAngularVelocity;
    }

    @Override
    public float getGeneratedSpeed() {
        Block block = this.getBlockState().getBlock();
        if (!(block instanceof HandCrankBlock)) {
            return 0.0f;
        }
        HandCrankBlock crank = (HandCrankBlock)block;
        int speed = (this.inUse == 0 ? 0 : (this.clockwise() ? -1 : 1)) * crank.getRotationSpeed();
        return HandCrankBlockEntity.convertToDirection(speed, (Direction)this.getBlockState().getValue((Property)HandCrankBlock.FACING));
    }

    protected boolean clockwise() {
        return this.backwards;
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("InUse", this.inUse);
        compound.putBoolean("Backwards", this.backwards);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.inUse = compound.getInt("InUse");
        this.backwards = compound.getBoolean("Backwards");
        super.read(compound, registries, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();
        float actualAngularSpeed = KineticBlockEntity.convertToAngular(this.getSpeed());
        this.chasingAngularVelocity += (actualAngularSpeed - this.chasingAngularVelocity) / 4.0f;
        this.independentAngle += this.chasingAngularVelocity;
        if (this.inUse > 0) {
            --this.inUse;
            if (this.inUse == 0 && !this.level.isClientSide) {
                this.sequenceContext = null;
                this.updateGeneratedRotation();
            }
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public SuperByteBuffer getRenderedHandle() {
        BlockState blockState = this.getBlockState();
        Direction facing = blockState.getOptionalValue((Property)HandCrankBlock.FACING).orElse(Direction.UP);
        return CachedBuffers.partialFacing((PartialModel)AllPartialModels.HAND_CRANK_HANDLE, (BlockState)blockState, (Direction)facing.getOpposite());
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean shouldRenderShaft() {
        return true;
    }

    @Override
    protected Block getStressConfigKey() {
        return AllBlocks.HAND_CRANK.has(this.getBlockState()) ? (Block)AllBlocks.HAND_CRANK.get() : (Block)AllBlocks.COPPER_VALVE_HANDLE.get();
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();
        if (this.inUse > 0 && AnimationTickHolder.getTicks() % 10 == 0) {
            if (!AllBlocks.HAND_CRANK.has(this.getBlockState())) {
                return;
            }
            AllSoundEvents.CRANKING.playAt(this.level, (Vec3i)this.worldPosition, (float)this.inUse / 2.5f, 0.65f + (float)(10 - this.inUse) / 10.0f, true);
        }
    }
}
