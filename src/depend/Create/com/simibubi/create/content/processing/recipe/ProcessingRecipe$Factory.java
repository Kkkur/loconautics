/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.processing.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

@FunctionalInterface
public static interface ProcessingRecipe.Factory<P extends ProcessingRecipeParams, R extends ProcessingRecipe<?, P>> {
    public R create(P var1);
}
