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
package com.simibubi.create.content.contraptions.actors.roller;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.actors.roller.RollerBlock;
import com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity;
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

private final class RollerBlockEntity.RollerValueBox
extends ValueBoxTransform {
    private int hOffset;

    public RollerBlockEntity.RollerValueBox(RollerBlockEntity rollerBlockEntity, int hOffset) {
        this.hOffset = hOffset;
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        Direction facing = (Direction)state.getValue((Property)RollerBlock.FACING);
        float yRot = AngleHelper.horizontalAngle((Direction)facing) + 180.0f;
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(yRot)).rotateXDegrees(90.0f);
    }

    @Override
    public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
        Vec3 offset = this.getLocalOffset(level, pos, state);
        if (offset == null) {
            return false;
        }
        return localHit.distanceTo(offset) < (double)(this.scale / 3.0f);
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction facing = (Direction)state.getValue((Property)RollerBlock.FACING);
        float stateAngle = AngleHelper.horizontalAngle((Direction)facing) + 180.0f;
        return VecHelper.rotateCentered((Vec3)VecHelper.voxelSpace((double)(8 + this.hOffset), (double)15.5, (double)11.0), (double)stateAngle, (Direction.Axis)Direction.Axis.Y);
    }
}
