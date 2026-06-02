/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids.pipes;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IAxisPipe {
    @Nullable
    public static Direction.Axis getAxisOf(BlockState state) {
        if (state.getBlock() instanceof IAxisPipe) {
            return ((IAxisPipe)state.getBlock()).getAxis(state);
        }
        return null;
    }

    public Direction.Axis getAxis(BlockState var1);
}
