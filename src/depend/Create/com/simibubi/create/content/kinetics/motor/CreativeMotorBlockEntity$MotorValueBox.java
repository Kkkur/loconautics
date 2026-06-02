/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
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
package com.simibubi.create.content.kinetics.motor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
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

static class CreativeMotorBlockEntity.MotorValueBox
extends ValueBoxTransform.Sided {
    CreativeMotorBlockEntity.MotorValueBox() {
    }

    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)12.5);
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction facing = (Direction)state.getValue((Property)CreativeMotorBlock.FACING);
        return super.getLocalOffset(level, pos, state).add(Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale(-0.0625));
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        super.rotate(level, pos, state, ms);
        Direction facing = (Direction)state.getValue((Property)CreativeMotorBlock.FACING);
        if (facing.getAxis() == Direction.Axis.Y) {
            return;
        }
        if (this.getSide() != Direction.UP) {
            return;
        }
        TransformStack.of((PoseStack)ms).rotateZDegrees(-AngleHelper.horizontalAngle((Direction)facing) + 180.0f);
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        Direction facing = (Direction)state.getValue((Property)CreativeMotorBlock.FACING);
        if (facing.getAxis() != Direction.Axis.Y && direction == Direction.DOWN) {
            return false;
        }
        return direction.getAxis() != facing.getAxis();
    }
}
