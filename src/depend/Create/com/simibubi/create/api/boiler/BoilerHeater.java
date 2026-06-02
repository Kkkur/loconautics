/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateHolder
 */
package com.simibubi.create.api.boiler;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;

@FunctionalInterface
public interface BoilerHeater {
    public static final int PASSIVE_HEAT = 0;
    public static final int NO_HEAT = -1;
    public static final BoilerHeater PASSIVE = BoilerHeaters::passive;
    public static final BoilerHeater BLAZE_BURNER = BoilerHeaters::blazeBurner;
    public static final SimpleRegistry<Block, BoilerHeater> REGISTRY = SimpleRegistry.create();

    public static float findHeat(Level level, BlockPos pos, BlockState state) {
        BoilerHeater heater = REGISTRY.get((StateHolder<Block, ?>)state);
        return heater != null ? heater.getHeat(level, pos, state) : -1.0f;
    }

    public float getHeat(Level var1, BlockPos var2, BlockState var3);
}
