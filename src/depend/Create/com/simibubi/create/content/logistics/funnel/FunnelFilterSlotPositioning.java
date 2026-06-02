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
package com.simibubi.create.content.logistics.funnel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
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

public class FunnelFilterSlotPositioning
extends ValueBoxTransform.Sided {
    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction side = this.getSide();
        float horizontalAngle = AngleHelper.horizontalAngle((Direction)side);
        Direction funnelFacing = FunnelBlock.getFunnelFacing(state);
        float stateAngle = AngleHelper.horizontalAngle((Direction)funnelFacing);
        if (state.getBlock() instanceof BeltFunnelBlock) {
            switch ((BeltFunnelBlock.Shape)((Object)state.getValue(BeltFunnelBlock.SHAPE))) {
                case EXTENDED: {
                    return VecHelper.rotateCentered((Vec3)VecHelper.voxelSpace((double)8.0, (double)15.5, (double)13.0), (double)stateAngle, (Direction.Axis)Direction.Axis.Y);
                }
                case PULLING: 
                case PUSHING: {
                    return VecHelper.rotateCentered((Vec3)VecHelper.voxelSpace((double)8.0, (double)12.0, (double)8.675f), (double)horizontalAngle, (Direction.Axis)Direction.Axis.Y);
                }
            }
            return VecHelper.rotateCentered((Vec3)VecHelper.voxelSpace((double)8.0, (double)13.0, (double)7.5), (double)horizontalAngle, (Direction.Axis)Direction.Axis.Y);
        }
        if (!funnelFacing.getAxis().isHorizontal()) {
            Vec3 southLocation = VecHelper.voxelSpace((double)8.0, (double)(funnelFacing == Direction.DOWN ? 14.0 : 2.0), (double)15.5);
            return VecHelper.rotateCentered((Vec3)southLocation, (double)horizontalAngle, (Direction.Axis)Direction.Axis.Y);
        }
        return VecHelper.rotateCentered((Vec3)VecHelper.voxelSpace((double)8.0, (double)12.2, (double)8.55f), (double)horizontalAngle, (Direction.Axis)Direction.Axis.Y);
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        Direction facing = FunnelBlock.getFunnelFacing(state);
        if (facing.getAxis().isVertical()) {
            super.rotate(level, pos, state, ms);
            return;
        }
        boolean isBeltFunnel = state.getBlock() instanceof BeltFunnelBlock;
        if (isBeltFunnel && state.getValue(BeltFunnelBlock.SHAPE) != BeltFunnelBlock.Shape.EXTENDED) {
            BeltFunnelBlock.Shape shape = (BeltFunnelBlock.Shape)((Object)state.getValue(BeltFunnelBlock.SHAPE));
            super.rotate(level, pos, state, ms);
            if (shape == BeltFunnelBlock.Shape.PULLING || shape == BeltFunnelBlock.Shape.PUSHING) {
                TransformStack.of((PoseStack)ms).rotateXDegrees(-22.5f);
            }
            return;
        }
        if (state.getBlock() instanceof FunnelBlock) {
            super.rotate(level, pos, state, ms);
            TransformStack.of((PoseStack)ms).rotateXDegrees(-22.5f);
            return;
        }
        float yRot = AngleHelper.horizontalAngle((Direction)AbstractFunnelBlock.getFunnelFacing(state)) + (float)(facing == Direction.DOWN ? 180 : 0);
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(yRot)).rotateXDegrees(facing == Direction.DOWN ? -90.0f : 90.0f);
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        Direction facing = FunnelBlock.getFunnelFacing(state);
        if (facing == null) {
            return false;
        }
        if (facing.getAxis().isVertical()) {
            return direction.getAxis().isHorizontal();
        }
        if (state.getBlock() instanceof BeltFunnelBlock && state.getValue(BeltFunnelBlock.SHAPE) == BeltFunnelBlock.Shape.EXTENDED) {
            return direction == Direction.UP;
        }
        return direction == facing;
    }

    @Override
    protected Vec3 getSouthLocation() {
        return Vec3.ZERO;
    }
}
