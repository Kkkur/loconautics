/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.saw;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class SawFilterSlot
extends ValueBoxTransform {
    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        if (state.getValue((Property)SawBlock.FACING) != Direction.UP) {
            return null;
        }
        int offset = (Boolean)state.getValue((Property)SawBlock.FLIPPED) != false ? -3 : 3;
        Vec3 x = VecHelper.voxelSpace((double)8.0, (double)12.5, (double)(8 + offset));
        Vec3 z = VecHelper.voxelSpace((double)(8 + offset), (double)12.5, (double)8.0);
        return (Boolean)state.getValue((Property)SawBlock.AXIS_ALONG_FIRST_COORDINATE) != false ? z : x;
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        int yRot = ((Boolean)state.getValue((Property)SawBlock.AXIS_ALONG_FIRST_COORDINATE) != false ? 90 : 0) + ((Boolean)state.getValue((Property)SawBlock.FLIPPED) != false ? 0 : 180);
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees((float)yRot)).rotateXDegrees(90.0f);
    }
}
