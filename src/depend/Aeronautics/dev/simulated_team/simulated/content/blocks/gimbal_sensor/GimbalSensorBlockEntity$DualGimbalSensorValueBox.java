/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Dual
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
package dev.simulated_team.simulated.content.blocks.gimbal_sensor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.gimbal_sensor.GimbalSensorBlock;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public static class GimbalSensorBlockEntity.DualGimbalSensorValueBox
extends ValueBoxTransform.Dual {
    protected Direction direction = Direction.UP;

    public GimbalSensorBlockEntity.DualGimbalSensorValueBox(boolean first) {
        super(first);
    }

    public Vec3 getLocalOffset(LevelAccessor levelAccessor, BlockPos blockPos, BlockState state) {
        Vec3 location = this.getSouthLocation();
        location = VecHelper.rotateCentered((Vec3)location, (double)AngleHelper.horizontalAngle((Direction)this.getSide()), (Direction.Axis)Direction.Axis.Y);
        location = VecHelper.rotateCentered((Vec3)location, (double)AngleHelper.verticalAngle((Direction)this.getSide()), (Direction.Axis)Direction.Axis.X);
        return location;
    }

    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)16.0);
    }

    public void rotate(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, PoseStack poseStack) {
        float yRot = AngleHelper.horizontalAngle((Direction)this.getSide()) + 180.0f;
        float xRot = this.getSide() == Direction.UP ? 90.0f : (this.getSide() == Direction.DOWN ? 270.0f : 0.0f);
        ((PoseTransformStack)TransformStack.of((PoseStack)poseStack).rotateYDegrees(yRot)).rotateXDegrees(xRot);
    }

    public boolean shouldRender(LevelAccessor level, BlockPos pos, BlockState state) {
        return super.shouldRender(level, pos, state) && this.isSideActive(state, this.getSide());
    }

    public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
        Vec3 offset = this.getLocalOffset(level, pos, state);
        if (offset == null) {
            return false;
        }
        return localHit.distanceTo(offset) < (double)(this.scale / 1.5f);
    }

    protected boolean isSideActive(BlockState state, Direction direction) {
        boolean a = direction.getAxis() == state.getValue(GimbalSensorBlock.HORIZONTAL_AXIS);
        return direction.getAxis().isHorizontal() && a == this.first;
    }

    public Direction getSide() {
        return Direction.NORTH;
    }
}
