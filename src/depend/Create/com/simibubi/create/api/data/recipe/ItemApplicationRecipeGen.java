/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.simibubi.create.api.data.recipe;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipeParams;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

public abstract class ItemApplicationRecipeGen
extends ProcessingRecipeGen<ItemApplicationRecipeParams, ManualApplicationRecipe, ItemApplicationRecipe.Builder<ManualApplicationRecipe>> {
    protected BaseRecipeProvider.GeneratedRecipe woodCasing(String type, Supplier<ItemLike> ingredient, Supplier<ItemLike> output) {
        return this.woodCasingIngredient(type, () -> Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)ingredient.get()}), output);
    }

    protected BaseRecipeProvider.GeneratedRecipe woodCasingTag(String type, Supplier<TagKey<Item>> ingredient, Supplier<ItemLike> output) {
        return this.woodCasingIngredient(type, () -> Ingredient.of((TagKey)((TagKey)ingredient.get())), output);
    }

    protected BaseRecipeProvider.GeneratedRecipe woodCasingIngredient(String type, Supplier<Ingredient> ingredient, Supplier<ItemLike> output) {
        this.create(type + "_casing_from_log", b -> (ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)b.require((TagKey<Item>)Tags.Items.STRIPPED_LOGS)).require((Ingredient)ingredient.get())).output((ItemLike)output.get()));
        return this.create(type + "_casing_from_wood", b -> (ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)b.require((TagKey<Item>)Tags.Items.STRIPPED_WOODS)).require((Ingredient)ingredient.get())).output((ItemLike)output.get()));
    }

    public ItemApplicationRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.ITEM_APPLICATION;
    }

    @Override
    protected ItemApplicationRecipe.Builder<ManualApplicationRecipe> getBuilder(ResourceLocation id) {
        return new ItemApplicationRecipe.Builder<ManualApplicationRecipe>(ManualApplicationRecipe::new, id);
    }
}
