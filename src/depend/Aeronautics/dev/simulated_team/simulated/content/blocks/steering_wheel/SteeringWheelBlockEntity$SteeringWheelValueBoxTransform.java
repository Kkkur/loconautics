/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.steering_wheel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlock;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

private static class SteeringWheelBlockEntity.SteeringWheelValueBoxTransform
extends ValueBoxTransform.Sided {
    private SteeringWheelBlockEntity.SteeringWheelValueBoxTransform() {
    }

    protected boolean isSideActive(BlockState state, Direction direction) {
        return direction == ((Boolean)state.getValue((Property)SteeringWheelBlock.ON_FLOOR) != false ? Direction.UP : Direction.DOWN);
    }

    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)15.5);
    }

    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        super.rotate(level, pos, state, ms);
        Direction facing = (Direction)state.getValue((Property)HorizontalDirectionalBlock.FACING);
        TransformStack.of((PoseStack)ms).rotateZDegrees(-AngleHelper.horizontalAngle((Direction)facing) + 180.0f);
    }
}
