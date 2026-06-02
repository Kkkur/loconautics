/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.providers.RegistrateRecipeProvider
 *  com.tterrag.registrate.util.DataIngredient
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  com.tterrag.registrate.util.nullness.NonNullFunction
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.WeatheringCopper$WeatherState
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 */
package com.simibubi.create.foundation.block;

import com.simibubi.create.foundation.block.CopperBlockSet;
import com.simibubi.create.foundation.block.CreateCopperStairBlock;
import com.simibubi.create.foundation.block.CreateWeatheringCopperStairBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;

public static class CopperBlockSet.StairVariant
implements CopperBlockSet.Variant<StairBlock> {
    public static final CopperBlockSet.StairVariant INSTANCE = new CopperBlockSet.StairVariant(CopperBlockSet.BlockVariant.INSTANCE);
    protected final CopperBlockSet.Variant<?> parent;

    protected CopperBlockSet.StairVariant(CopperBlockSet.Variant<?> parent) {
        this.parent = parent;
    }

    @Override
    public String getSuffix() {
        return "_stairs";
    }

    @Override
    public NonNullFunction<BlockBehaviour.Properties, StairBlock> getFactory(CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
        if (!blocks.hasVariant(this.parent)) {
            throw new IllegalStateException("Cannot add StairVariant '" + String.valueOf(this) + "' without parent Variant '" + this.parent.toString() + "'!");
        }
        if (waxed) {
            return CreateCopperStairBlock::new;
        }
        return p -> new CreateWeatheringCopperStairBlock(state, (BlockBehaviour.Properties)p);
    }

    @Override
    public void generateBlockState(DataGenContext<Block, StairBlock> ctx, RegistrateBlockstateProvider prov, CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
        String baseLoc = "block/" + blocks.generalDirectory + CopperBlockSet.getWeatherStatePrefix(state);
        ResourceLocation texture = prov.modLoc(baseLoc + blocks.getName());
        ResourceLocation endTexture = prov.modLoc(baseLoc + blocks.getEndTextureName());
        prov.stairsBlock((StairBlock)ctx.get(), texture, endTexture, endTexture);
    }

    @Override
    public void generateRecipes(BlockEntry<?> blockVariant, DataGenContext<Block, StairBlock> ctx, RegistrateRecipeProvider prov) {
        prov.stairs(DataIngredient.items((ItemLike)((Block)blockVariant.get()), (ItemLike[])new Block[0]), RecipeCategory.BUILDING_BLOCKS, () -> ctx.get(), null, true);
    }
}
