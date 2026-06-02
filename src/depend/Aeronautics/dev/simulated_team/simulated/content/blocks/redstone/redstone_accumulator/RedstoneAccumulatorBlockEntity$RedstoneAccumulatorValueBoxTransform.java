/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.redstone.redstone_accumulator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.redstone.redstone_accumulator.RedstoneAccumulatorBlock;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

private static class RedstoneAccumulatorBlockEntity.RedstoneAccumulatorValueBoxTransform
extends ValueBoxTransform {
    private RedstoneAccumulatorBlockEntity.RedstoneAccumulatorValueBoxTransform() {
    }

    public Vec3 getLocalOffset(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        return new Vec3(0.5, (double)0.4125f, 0.5);
    }

    public void rotate(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, PoseStack poseStack) {
        float yRot = AngleHelper.horizontalAngle((Direction)((Direction)blockState.getValue((Property)RedstoneAccumulatorBlock.FACING))) + 180.0f;
        ((PoseTransformStack)TransformStack.of((PoseStack)poseStack).rotateYDegrees(yRot)).rotateXDegrees(90.0f);
    }
}
