/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics.chute;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SmartChuteFilterSlotPositioning
extends ValueBoxTransform.Sided {
    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction side = this.getSide();
        float horizontalAngle = AngleHelper.horizontalAngle((Direction)side);
        Vec3 southLocation = VecHelper.voxelSpace((double)8.0, (double)11.0, (double)15.5);
        return VecHelper.rotateCentered((Vec3)southLocation, (double)horizontalAngle, (Direction.Axis)Direction.Axis.Y);
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        return direction.getAxis().isHorizontal();
    }

    @Override
    protected Vec3 getSouthLocation() {
        return Vec3.ZERO;
    }
}
