/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.processing.basin;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

static class BasinBlockEntity.BasinValueBox
extends ValueBoxTransform.Sided {
    BasinBlockEntity.BasinValueBox() {
    }

    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)12.0, (double)16.05);
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        return direction.getAxis().isHorizontal();
    }
}
