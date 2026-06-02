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
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.actors.contraptionControls;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public static class ContraptionControlsBlockEntity.ControlsSlot
extends ValueBoxTransform.Sided {
    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction facing = (Direction)state.getValue((Property)ControlsBlock.FACING);
        float yRot = AngleHelper.horizontalAngle((Direction)facing);
        return VecHelper.rotateCentered((Vec3)VecHelper.voxelSpace((double)8.0, (double)14.0, (double)5.5), (double)yRot, (Direction.Axis)Direction.Axis.Y);
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        Direction facing = (Direction)state.getValue((Property)ControlsBlock.FACING);
        float yRot = AngleHelper.horizontalAngle((Direction)facing);
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(yRot + 180.0f)).rotateXDegrees(67.5f);
    }

    @Override
    public float getScale() {
        return 0.508f;
    }

    @Override
    protected Vec3 getSouthLocation() {
        return Vec3.ZERO;
    }
}
