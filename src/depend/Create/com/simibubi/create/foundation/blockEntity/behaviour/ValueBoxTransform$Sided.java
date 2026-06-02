/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public static abstract class ValueBoxTransform.Sided
extends ValueBoxTransform {
    protected Direction direction = Direction.UP;

    public ValueBoxTransform.Sided fromSide(Direction direction) {
        this.direction = direction;
        return this;
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Vec3 location = this.getSouthLocation();
        location = VecHelper.rotateCentered((Vec3)location, (double)AngleHelper.horizontalAngle((Direction)this.getSide()), (Direction.Axis)Direction.Axis.Y);
        location = VecHelper.rotateCentered((Vec3)location, (double)AngleHelper.verticalAngle((Direction)this.getSide()), (Direction.Axis)Direction.Axis.X);
        return location;
    }

    protected abstract Vec3 getSouthLocation();

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        float yRot = AngleHelper.horizontalAngle((Direction)this.getSide()) + 180.0f;
        float xRot = this.getSide() == Direction.UP ? 90.0f : (this.getSide() == Direction.DOWN ? 270.0f : 0.0f);
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(yRot)).rotateXDegrees(xRot);
    }

    @Override
    public boolean shouldRender(LevelAccessor level, BlockPos pos, BlockState state) {
        return super.shouldRender(level, pos, state) && this.isSideActive(state, this.getSide());
    }

    @Override
    public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
        return this.isSideActive(state, this.getSide()) && super.testHit(level, pos, state, localHit);
    }

    protected boolean isSideActive(BlockState state, Direction direction) {
        return true;
    }

    public Direction getSide() {
        return this.direction;
    }
}
