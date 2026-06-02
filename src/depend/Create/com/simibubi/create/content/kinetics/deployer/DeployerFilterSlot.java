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
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.deployer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.deployer.DeployerBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class DeployerFilterSlot
extends ValueBoxTransform.Sided {
    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction facing = (Direction)state.getValue((Property)DeployerBlock.FACING);
        Vec3 vec = VecHelper.voxelSpace((double)8.0, (double)8.0, (double)15.5);
        vec = VecHelper.rotateCentered((Vec3)vec, (double)AngleHelper.horizontalAngle((Direction)this.getSide()), (Direction.Axis)Direction.Axis.Y);
        vec = VecHelper.rotateCentered((Vec3)vec, (double)AngleHelper.verticalAngle((Direction)this.getSide()), (Direction.Axis)Direction.Axis.X);
        vec = vec.subtract(Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale(0.125));
        return vec;
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        Direction facing = (Direction)state.getValue((Property)DeployerBlock.FACING);
        if (direction.getAxis() == facing.getAxis()) {
            return false;
        }
        return ((DeployerBlock)state.getBlock()).getRotationAxis(state) != direction.getAxis();
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        Direction facing = this.getSide();
        float xRot = facing == Direction.UP ? 90.0f : (facing == Direction.DOWN ? 270.0f : 0.0f);
        float yRot = AngleHelper.horizontalAngle((Direction)facing) + 180.0f;
        if (facing.getAxis() == Direction.Axis.Y) {
            TransformStack.of((PoseStack)ms).rotateYDegrees(180.0f + AngleHelper.horizontalAngle((Direction)((Direction)state.getValue((Property)DeployerBlock.FACING))));
        }
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(yRot)).rotateXDegrees(xRot);
    }

    @Override
    protected Vec3 getSouthLocation() {
        return Vec3.ZERO;
    }
}
