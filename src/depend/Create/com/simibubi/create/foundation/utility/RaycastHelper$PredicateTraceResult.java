/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 */
package com.simibubi.create.foundation.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public static class RaycastHelper.PredicateTraceResult {
    private BlockPos pos;
    private Direction facing;

    public RaycastHelper.PredicateTraceResult(BlockPos pos, Direction facing) {
        this.pos = pos;
        this.facing = facing;
    }

    public RaycastHelper.PredicateTraceResult() {
    }

    public Direction getFacing() {
        return this.facing;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean missed() {
        return this.pos == null;
    }
}
