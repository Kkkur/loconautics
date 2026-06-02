/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.portable_engine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlock;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

private static class PortableEngineBlockEntity.PortableEngineValueBoxTransform
extends ValueBoxTransform {
    private PortableEngineBlockEntity.PortableEngineValueBoxTransform() {
    }

    public Vec3 getLocalOffset(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        Direction facing = (Direction)blockState.getValue(PortableEngineBlock.HORIZONTAL_FACING);
        float yRot = AngleHelper.horizontalAngle((Direction)facing);
        return VecHelper.rotateCentered((Vec3)VecHelper.voxelSpace((double)8.0, (double)13.5, (double)7.4f), (double)yRot, (Direction.Axis)Direction.Axis.Y);
    }

    public void rotate(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, PoseStack poseStack) {
        float yRot = AngleHelper.horizontalAngle((Direction)((Direction)blockState.getValue((Property)BlockStateProperties.HORIZONTAL_FACING)));
        ((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)poseStack).rotateYDegrees(yRot)).rotateXDegrees(90.0f)).translate(0.0, 0.1, 0.0);
    }
}
