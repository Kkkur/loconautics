/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.material.FlowingFluid
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.neoforge.common.Tags$Fluids
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.CompactingRecipeGen;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;

public final class CreateCompactingRecipeGen
extends CompactingRecipeGen {
    BaseRecipeProvider.GeneratedRecipe GRANITE = this.create("granite_from_flint", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((ItemLike)Items.FLINT)).require((ItemLike)Items.FLINT)).require(Fluids.LAVA, 100)).require((ItemLike)Items.RED_SAND)).output((ItemLike)Blocks.GRANITE, 1));
    BaseRecipeProvider.GeneratedRecipe DIORITE = this.create("diorite_from_flint", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((ItemLike)Items.FLINT)).require((ItemLike)Items.FLINT)).require(Fluids.LAVA, 100)).require((ItemLike)Items.CALCITE)).output((ItemLike)Blocks.DIORITE, 1));
    BaseRecipeProvider.GeneratedRecipe ANDESITE = this.create("andesite_from_flint", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((ItemLike)Items.FLINT)).require((ItemLike)Items.FLINT)).require(Fluids.LAVA, 100)).require((ItemLike)Items.GRAVEL)).output((ItemLike)Blocks.ANDESITE, 1));
    BaseRecipeProvider.GeneratedRecipe CHOCOLATE = this.create("chocolate", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((FlowingFluid)AllFluids.CHOCOLATE.get(), 250)).output((ItemLike)AllItems.BAR_OF_CHOCOLATE.get(), 1));
    BaseRecipeProvider.GeneratedRecipe BLAZE_CAKE = this.create("blaze_cake", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((TagKey<Item>)Tags.Items.EGGS)).require((ItemLike)Items.SUGAR)).require((ItemLike)AllItems.CINDER_FLOUR.get())).output((ItemLike)AllItems.BLAZE_CAKE_BASE.get(), 1));
    BaseRecipeProvider.GeneratedRecipe HONEY = this.create("honey", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((TagKey<Fluid>)Tags.Fluids.HONEY, 1000)).output((ItemLike)Items.HONEY_BLOCK, 1));
    BaseRecipeProvider.GeneratedRecipe ICE = this.create("ice", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((ItemLike)Blocks.SNOW_BLOCK)).require((ItemLike)Blocks.SNOW_BLOCK)).require((ItemLike)Blocks.SNOW_BLOCK)).require((ItemLike)Blocks.SNOW_BLOCK)).require((ItemLike)Blocks.SNOW_BLOCK)).require((ItemLike)Blocks.SNOW_BLOCK)).require((ItemLike)Blocks.SNOW_BLOCK)).require((ItemLike)Blocks.SNOW_BLOCK)).require((ItemLike)Blocks.SNOW_BLOCK)).output((ItemLike)Blocks.ICE));

    public CreateCompactingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "create");
    }
}
