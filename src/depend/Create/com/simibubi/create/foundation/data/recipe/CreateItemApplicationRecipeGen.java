/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.ItemApplicationRecipeGen;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

public final class CreateItemApplicationRecipeGen
extends ItemApplicationRecipeGen {
    BaseRecipeProvider.GeneratedRecipe BOUND_CARDBOARD_BLOCK = this.create("bound_cardboard_inworld", b -> (ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)b.require((ItemLike)AllBlocks.CARDBOARD_BLOCK.asItem())).require((TagKey<Item>)Tags.Items.STRINGS)).output(AllBlocks.BOUND_CARDBOARD_BLOCK.asStack()));
    BaseRecipeProvider.GeneratedRecipe ANDESITE = this.woodCasing("andesite", CreateRecipeProvider.I::andesiteAlloy, CreateRecipeProvider.I::andesiteCasing);
    BaseRecipeProvider.GeneratedRecipe COPPER = this.woodCasingTag("copper", CreateRecipeProvider.I::copper, CreateRecipeProvider.I::copperCasing);
    BaseRecipeProvider.GeneratedRecipe BRASS = this.woodCasingTag("brass", CreateRecipeProvider.I::brass, CreateRecipeProvider.I::brassCasing);
    BaseRecipeProvider.GeneratedRecipe RAILWAY = this.create("railway_casing", b -> (ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)b.require(CreateRecipeProvider.I.brassCasing())).require(CreateRecipeProvider.I.sturdySheet())).output(CreateRecipeProvider.I.railwayCasing()));

    public CreateItemApplicationRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "create");
    }
}
