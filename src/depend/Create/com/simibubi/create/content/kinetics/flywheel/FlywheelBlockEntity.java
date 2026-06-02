/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 */
package com.simibubi.create.content.kinetics.flywheel;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class FlywheelBlockEntity
extends KineticBlockEntity {
    LerpedFloat visualSpeed = LerpedFloat.linear();
    float angle;

    public FlywheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(2.0);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (clientPacket) {
            this.visualSpeed.chase((double)this.getGeneratedSpeed(), 0.015625, LerpedFloat.Chaser.EXP);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            return;
        }
        float targetSpeed = this.getSpeed();
        this.visualSpeed.updateChaseTarget(targetSpeed);
        this.visualSpeed.tickChaser();
        this.angle += this.visualSpeed.getValue() * 3.0f / 10.0f;
        this.angle %= 360.0f;
    }
}
