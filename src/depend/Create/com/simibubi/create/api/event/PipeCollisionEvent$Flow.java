/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.Fluid
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.event;

import com.simibubi.create.api.event.PipeCollisionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public static class PipeCollisionEvent.Flow
extends PipeCollisionEvent {
    public PipeCollisionEvent.Flow(Level level, BlockPos pos, Fluid firstFluid, Fluid secondFluid, @Nullable BlockState defaultState) {
        super(level, pos, firstFluid, secondFluid, defaultState);
    }

    public Fluid getFirstFluid() {
        return this.firstFluid;
    }

    public Fluid getSecondFluid() {
        return this.secondFluid;
    }
}
