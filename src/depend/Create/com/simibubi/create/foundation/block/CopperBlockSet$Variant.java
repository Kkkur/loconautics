/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.providers.RegistrateRecipeProvider
 *  com.tterrag.registrate.providers.loot.RegistrateBlockLootTables
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  com.tterrag.registrate.util.nullness.NonNullFunction
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.WeatheringCopper$WeatherState
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 */
package com.simibubi.create.foundation.block;

import com.simibubi.create.foundation.block.CopperBlockSet;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;

public static interface CopperBlockSet.Variant<T extends Block> {
    public String getSuffix();

    public NonNullFunction<BlockBehaviour.Properties, T> getFactory(CopperBlockSet var1, WeatheringCopper.WeatherState var2, boolean var3);

    default public void generateLootTable(RegistrateBlockLootTables lootTable, T block, CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
        lootTable.dropSelf(block);
    }

    public void generateRecipes(BlockEntry<?> var1, DataGenContext<Block, T> var2, RegistrateRecipeProvider var3);

    public void generateBlockState(DataGenContext<Block, T> var1, RegistrateBlockstateProvider var2, CopperBlockSet var3, WeatheringCopper.WeatherState var4, boolean var5);
}
