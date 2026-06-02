/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 */
package com.simibubi.create.api.data.recipe;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.DatagenMod;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public abstract class HauntingRecipeGen
extends StandardProcessingRecipeGen<HauntingRecipe> {
    public BaseRecipeProvider.GeneratedRecipe convert(ItemLike input, ItemLike result) {
        return this.convert(() -> Ingredient.of((ItemLike[])new ItemLike[]{input}), () -> result);
    }

    public BaseRecipeProvider.GeneratedRecipe convert(Supplier<Ingredient> input, Supplier<ItemLike> result) {
        return this.create(this.asResource(RegisteredObjectsHelper.getKeyOrThrow((Item)result.get().asItem()).getPath()), p -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)p.withItemIngredients((Ingredient)input.get())).output((ItemLike)result.get()));
    }

    protected BaseRecipeProvider.GeneratedRecipe moddedConversion(DatagenMod mod, String input, String output) {
        return this.create("compat/" + mod.getId() + "/" + output, p -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)p.require(mod, input)).output(mod, output)).whenModLoaded(mod.getId()));
    }

    public HauntingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.HAUNTING;
    }
}
