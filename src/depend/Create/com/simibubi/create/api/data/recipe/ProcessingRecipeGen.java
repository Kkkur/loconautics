/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.api.data.recipe;

import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public abstract class ProcessingRecipeGen<P extends ProcessingRecipeParams, R extends ProcessingRecipe<?, P>, B extends ProcessingRecipeBuilder<P, R, B>>
extends BaseRecipeProvider {
    public ProcessingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    protected BaseRecipeProvider.GeneratedRecipe create(String namespace, Supplier<ItemLike> singleIngredient, UnaryOperator<B> transform) {
        BaseRecipeProvider.GeneratedRecipe generatedRecipe = c -> {
            ItemLike itemLike = (ItemLike)singleIngredient.get();
            ((ProcessingRecipeBuilder)transform.apply(((ProcessingRecipeBuilder)this.getBuilder(ResourceLocation.fromNamespaceAndPath((String)namespace, (String)RegisteredObjectsHelper.getKeyOrThrow((Item)itemLike.asItem()).getPath()))).withItemIngredients(Ingredient.of((ItemLike[])new ItemLike[]{itemLike})))).build(c);
        };
        this.all.add(generatedRecipe);
        return generatedRecipe;
    }

    protected BaseRecipeProvider.GeneratedRecipe create(Supplier<ItemLike> singleIngredient, UnaryOperator<B> transform) {
        return this.create("create", singleIngredient, transform);
    }

    protected BaseRecipeProvider.GeneratedRecipe createWithDeferredId(Supplier<ResourceLocation> name, UnaryOperator<B> transform) {
        BaseRecipeProvider.GeneratedRecipe generatedRecipe = c -> ((ProcessingRecipeBuilder)transform.apply(this.getBuilder((ResourceLocation)name.get()))).build(c);
        this.all.add(generatedRecipe);
        return generatedRecipe;
    }

    protected BaseRecipeProvider.GeneratedRecipe create(ResourceLocation name, UnaryOperator<B> transform) {
        return this.createWithDeferredId(() -> name, transform);
    }

    protected BaseRecipeProvider.GeneratedRecipe create(String name, UnaryOperator<B> transform) {
        return this.create(this.asResource(name), transform);
    }

    protected abstract IRecipeTypeInfo getRecipeType();

    protected abstract B getBuilder(ResourceLocation var1);

    protected Supplier<ResourceLocation> idWithSuffix(Supplier<ItemLike> item, String suffix) {
        return () -> {
            ResourceLocation registryName = RegisteredObjectsHelper.getKeyOrThrow((Item)((ItemLike)item.get()).asItem());
            return this.asResource(registryName.getPath() + suffix);
        };
    }

    @NotNull
    public String getName() {
        return this.modid + "'s processing recipes: " + this.getRecipeType().getId().getPath();
    }
}
