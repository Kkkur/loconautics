/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.providers.RegistrateRecipeProvider
 *  com.tterrag.registrate.providers.loot.RegistrateBlockLootTables
 *  com.tterrag.registrate.util.DataIngredient
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  com.tterrag.registrate.util.nullness.NonNullFunction
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.WeatheringCopper$WeatherState
 *  net.minecraft.world.level.block.WeatheringCopperSlabBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 */
package com.simibubi.create.foundation.block;

import com.simibubi.create.foundation.block.CopperBlockSet;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperSlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public static class CopperBlockSet.SlabVariant
implements CopperBlockSet.Variant<SlabBlock> {
    public static final CopperBlockSet.SlabVariant INSTANCE = new CopperBlockSet.SlabVariant();

    protected CopperBlockSet.SlabVariant() {
    }

    @Override
    public String getSuffix() {
        return "_slab";
    }

    @Override
    public NonNullFunction<BlockBehaviour.Properties, SlabBlock> getFactory(CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
        if (waxed) {
            return SlabBlock::new;
        }
        return p -> new WeatheringCopperSlabBlock(state, p);
    }

    @Override
    public void generateLootTable(RegistrateBlockLootTables lootTable, SlabBlock block, CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
        lootTable.add((Block)block, lootTable.createSlabItemTable((Block)block));
    }

    @Override
    public void generateBlockState(DataGenContext<Block, SlabBlock> ctx, RegistrateBlockstateProvider prov, CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
        ResourceLocation fullModel = prov.modLoc("block/" + CopperBlockSet.getWeatherStatePrefix(state) + blocks.getName());
        String baseLoc = "block/" + blocks.generalDirectory + CopperBlockSet.getWeatherStatePrefix(state);
        ResourceLocation texture = prov.modLoc(baseLoc + blocks.getName());
        ResourceLocation endTexture = prov.modLoc(baseLoc + blocks.getEndTextureName());
        prov.slabBlock((SlabBlock)ctx.get(), fullModel, texture, endTexture, endTexture);
    }

    @Override
    public void generateRecipes(BlockEntry<?> blockVariant, DataGenContext<Block, SlabBlock> ctx, RegistrateRecipeProvider prov) {
        prov.slab(DataIngredient.items((ItemLike)((Block)blockVariant.get()), (ItemLike[])new Block[0]), RecipeCategory.BUILDING_BLOCKS, () -> ctx.get(), null, true);
    }
}
