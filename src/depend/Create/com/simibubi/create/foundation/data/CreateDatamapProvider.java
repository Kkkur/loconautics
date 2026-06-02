/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.level.block.Block
 *  net.neoforged.neoforge.common.conditions.ICondition
 *  net.neoforged.neoforge.common.data.DataMapProvider
 *  net.neoforged.neoforge.common.data.DataMapProvider$Builder
 *  net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps
 *  net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable
 *  net.neoforged.neoforge.registries.datamaps.builtin.Waxable
 */
package com.simibubi.create.foundation.data;

import com.simibubi.create.foundation.block.CopperRegistries;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

public class CreateDatamapProvider
extends DataMapProvider {
    public CreateDatamapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    protected void gather(HolderLookup.Provider provider) {
        DataMapProvider.Builder oxidizables = this.builder(NeoForgeDataMaps.OXIDIZABLES);
        CopperRegistries.getWeatheringView().forEach((now, after) -> CreateDatamapProvider.add(oxidizables, (Holder<Block>)now, new Oxidizable((Block)after.value())));
        DataMapProvider.Builder waxables = this.builder(NeoForgeDataMaps.WAXABLES);
        CopperRegistries.getWaxableView().forEach((now, after) -> CreateDatamapProvider.add(waxables, (Holder<Block>)now, new Waxable((Block)after.value())));
    }

    public static <T> void add(DataMapProvider.Builder<T, Block> b, Holder<Block> now, T after) {
        b.add(now, after, false, new ICondition[0]);
    }
}
