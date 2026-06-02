/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.levelgen.placement.PlacementContext
 *  net.minecraft.world.level.levelgen.placement.PlacementFilter
 *  net.minecraft.world.level.levelgen.placement.PlacementModifierType
 */
package com.simibubi.create.infrastructure.worldgen;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.worldgen.AllPlacementModifiers;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class ConfigPlacementFilter
extends PlacementFilter {
    public static final ConfigPlacementFilter INSTANCE = new ConfigPlacementFilter();
    public static final MapCodec<ConfigPlacementFilter> CODEC = MapCodec.unit(() -> INSTANCE);

    protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
        return (Boolean)AllConfigs.common().worldGen.disable.get() == false;
    }

    public PlacementModifierType<?> type() {
        return (PlacementModifierType)AllPlacementModifiers.CONFIG_FILTER.get();
    }
}
