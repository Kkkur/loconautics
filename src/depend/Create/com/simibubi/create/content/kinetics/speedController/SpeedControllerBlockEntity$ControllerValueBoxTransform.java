/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.speedController;

import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlock;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

private class SpeedControllerBlockEntity.ControllerValueBoxTransform
extends ValueBoxTransform.Sided {
    private SpeedControllerBlockEntity.ControllerValueBoxTransform(SpeedControllerBlockEntity speedControllerBlockEntity) {
    }

    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)11.0, (double)15.5);
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        if (direction.getAxis().isVertical()) {
            return false;
        }
        return state.getValue(SpeedControllerBlock.HORIZONTAL_AXIS) != direction.getAxis();
    }

    @Override
    public float getScale() {
        return 0.5f;
    }
}
