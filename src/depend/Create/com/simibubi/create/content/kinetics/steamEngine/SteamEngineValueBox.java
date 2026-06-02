/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.Pointing
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.steamEngine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class SteamEngineValueBox
extends ValueBoxTransform.Sided {
    @Override
    protected boolean isSideActive(BlockState state, Direction side) {
        boolean recessed;
        Direction engineFacing = SteamEngineBlock.getFacing(state);
        if (engineFacing.getAxis() == side.getAxis()) {
            return false;
        }
        float roll = 0.0f;
        for (Pointing p : Pointing.values()) {
            if (p.getCombinedDirection(engineFacing) != side) continue;
            roll = p.getXRotation();
        }
        if (engineFacing == Direction.UP) {
            roll += 180.0f;
        }
        boolean bl = recessed = roll % 180.0f == 0.0f;
        if (engineFacing.getAxis() == Direction.Axis.Y) {
            recessed ^= ((Direction)state.getValue((Property)SteamEngineBlock.FACING)).getAxis() == Direction.Axis.X;
        }
        return !recessed;
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction side = this.getSide();
        Direction engineFacing = SteamEngineBlock.getFacing(state);
        float roll = 0.0f;
        for (Pointing p : Pointing.values()) {
            if (p.getCombinedDirection(engineFacing) != side) continue;
            roll = p.getXRotation();
        }
        if (engineFacing == Direction.UP) {
            roll += 180.0f;
        }
        float horizontalAngle = AngleHelper.horizontalAngle((Direction)engineFacing);
        float verticalAngle = AngleHelper.verticalAngle((Direction)engineFacing);
        Vec3 local = VecHelper.voxelSpace((double)8.0, (double)14.5, (double)9.0);
        local = VecHelper.rotateCentered((Vec3)local, (double)roll, (Direction.Axis)Direction.Axis.Z);
        local = VecHelper.rotateCentered((Vec3)local, (double)horizontalAngle, (Direction.Axis)Direction.Axis.Y);
        local = VecHelper.rotateCentered((Vec3)local, (double)verticalAngle, (Direction.Axis)Direction.Axis.X);
        return local;
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        Direction facing = SteamEngineBlock.getFacing(state);
        if (facing.getAxis() == Direction.Axis.Y) {
            super.rotate(level, pos, state, ms);
            return;
        }
        float roll = 0.0f;
        for (Pointing p : Pointing.values()) {
            if (p.getCombinedDirection(facing) != this.getSide()) continue;
            roll = p.getXRotation();
        }
        float yRot = AngleHelper.horizontalAngle((Direction)facing) + (float)(facing == Direction.DOWN ? 180 : 0);
        ((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(yRot)).rotateXDegrees(facing == Direction.DOWN ? -90.0f : 90.0f)).rotateYDegrees(roll);
    }

    @Override
    protected Vec3 getSouthLocation() {
        return Vec3.ZERO;
    }
}
