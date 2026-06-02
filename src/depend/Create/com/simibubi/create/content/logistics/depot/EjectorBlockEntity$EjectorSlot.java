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
package com.simibubi.create.content.logistics.depot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.depot.EjectorBlock;
import com.simibubi.create.content.logistics.depot.EjectorBlockEntity;
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

private class EjectorBlockEntity.EjectorSlot
extends ValueBoxTransform.Sided {
    private EjectorBlockEntity.EjectorSlot() {
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        if (this.direction != Direction.UP) {
            return super.getLocalOffset(level, pos, state);
        }
        return new Vec3(0.5, 0.65625, 0.5).add(VecHelper.rotate((Vec3)VecHelper.voxelSpace((double)0.0, (double)0.0, (double)-5.0), (double)this.angle(state), (Direction.Axis)Direction.Axis.Y));
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        if (this.direction != Direction.UP) {
            super.rotate(level, pos, state, ms);
            return;
        }
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(this.angle(state))).rotateXDegrees(90.0f);
    }

    protected float angle(BlockState state) {
        float horizontalAngle = AllBlocks.WEIGHTED_EJECTOR.has(state) ? AngleHelper.horizontalAngle((Direction)((Direction)state.getValue(EjectorBlock.HORIZONTAL_FACING))) : 0.0f;
        return horizontalAngle;
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        return direction.getAxis() == ((Direction)state.getValue(EjectorBlock.HORIZONTAL_FACING)).getAxis() || direction == Direction.UP && EjectorBlockEntity.this.state != EjectorBlockEntity.State.CHARGED;
    }

    @Override
    protected Vec3 getSouthLocation() {
        return this.direction == Direction.UP ? Vec3.ZERO : VecHelper.voxelSpace((double)8.0, (double)6.0, (double)15.5);
    }
}
