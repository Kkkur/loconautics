/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import java.util.function.BiPredicate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CenteredSideValueBoxTransform
extends ValueBoxTransform.Sided {
    private BiPredicate<BlockState, Direction> allowedDirections;

    public CenteredSideValueBoxTransform() {
        this((b, d) -> true);
    }

    public CenteredSideValueBoxTransform(BiPredicate<BlockState, Direction> allowedDirections) {
        this.allowedDirections = allowedDirections;
    }

    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)15.5);
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        return this.allowedDirections.test(state, direction);
    }
}
