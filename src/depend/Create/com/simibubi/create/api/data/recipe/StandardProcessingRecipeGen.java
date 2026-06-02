/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.api.data.recipe;

import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public abstract class StandardProcessingRecipeGen<R extends StandardProcessingRecipe<?>>
extends ProcessingRecipeGen<ProcessingRecipeParams, R, StandardProcessingRecipe.Builder<R>> {
    public StandardProcessingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    protected StandardProcessingRecipe.Serializer<R> getSerializer() {
        return (StandardProcessingRecipe.Serializer)this.getRecipeType().getSerializer();
    }

    @Override
    protected StandardProcessingRecipe.Builder<R> getBuilder(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<R>(this.getSerializer().factory(), id);
    }
}
