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
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.redstone;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class FilteredDetectorFilterSlot
extends ValueBoxTransform.Sided {
    private boolean hasSlotAtBottom;

    public FilteredDetectorFilterSlot(boolean hasSlotAtBottom) {
        this.hasSlotAtBottom = hasSlotAtBottom;
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        Direction targetDirection = DirectedDirectionalBlock.getTargetDirection(state);
        if (direction == targetDirection) {
            return false;
        }
        if (targetDirection.getOpposite() == direction) {
            return true;
        }
        if (targetDirection.getAxis() != Direction.Axis.Y) {
            return direction == Direction.UP || direction == Direction.DOWN && this.hasSlotAtBottom;
        }
        if (targetDirection == Direction.UP) {
            direction = direction.getOpposite();
        }
        if (!this.hasSlotAtBottom) {
            return direction == state.getValue((Property)DirectedDirectionalBlock.FACING);
        }
        return direction.getAxis() == ((Direction)state.getValue((Property)DirectedDirectionalBlock.FACING)).getClockWise().getAxis();
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        super.rotate(level, pos, state, ms);
        Direction facing = (Direction)state.getValue((Property)DirectedDirectionalBlock.FACING);
        if (facing.getAxis() == Direction.Axis.Y) {
            return;
        }
        if (this.getSide() != Direction.UP) {
            return;
        }
        TransformStack.of((PoseStack)ms).rotateZDegrees(-AngleHelper.horizontalAngle((Direction)facing) + 180.0f);
    }

    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)15.5);
    }
}
