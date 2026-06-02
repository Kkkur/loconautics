/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.content.processing.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.resources.ResourceLocation;

public static class StandardProcessingRecipe.Builder<R extends StandardProcessingRecipe<?>>
extends ProcessingRecipeBuilder<ProcessingRecipeParams, R, StandardProcessingRecipe.Builder<R>> {
    public StandardProcessingRecipe.Builder(StandardProcessingRecipe.Factory<R> factory, ResourceLocation recipeId) {
        super(factory, recipeId);
    }

    @Override
    protected ProcessingRecipeParams createParams() {
        return new ProcessingRecipeParams();
    }

    @Override
    public StandardProcessingRecipe.Builder<R> self() {
        return this;
    }
}
