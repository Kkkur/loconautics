/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.crank;

import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public static class ValveHandleBlockEntity.ValveHandleValueBox
extends ValueBoxTransform.Sided {
    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        return direction == state.getValue((Property)ValveHandleBlock.FACING);
    }

    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)4.5);
    }

    @Override
    public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
        Vec3 offset = this.getLocalOffset(level, pos, state);
        if (offset == null) {
            return false;
        }
        return localHit.distanceTo(offset) < (double)(this.scale / 1.5f);
    }
}
