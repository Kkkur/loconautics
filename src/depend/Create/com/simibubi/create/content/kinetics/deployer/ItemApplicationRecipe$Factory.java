/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.kinetics.deployer;

import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipeParams;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;

@FunctionalInterface
public static interface ItemApplicationRecipe.Factory<R extends ItemApplicationRecipe>
extends ProcessingRecipe.Factory<ItemApplicationRecipeParams, R> {
    @Override
    public R create(ItemApplicationRecipeParams var1);
}
