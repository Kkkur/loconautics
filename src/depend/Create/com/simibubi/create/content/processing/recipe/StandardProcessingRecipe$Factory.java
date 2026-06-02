/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.processing.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;

@FunctionalInterface
public static interface StandardProcessingRecipe.Factory<R extends StandardProcessingRecipe<?>>
extends ProcessingRecipe.Factory<ProcessingRecipeParams, R> {
    @Override
    public R create(ProcessingRecipeParams var1);
}
