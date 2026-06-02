/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.content.kinetics.deployer;

import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipeParams;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;

public static class ItemApplicationRecipe.Builder<R extends ItemApplicationRecipe>
extends ProcessingRecipeBuilder<ItemApplicationRecipeParams, R, ItemApplicationRecipe.Builder<R>> {
    public ItemApplicationRecipe.Builder(ItemApplicationRecipe.Factory<R> factory, ResourceLocation recipeId) {
        super(factory, recipeId);
    }

    @Override
    protected ItemApplicationRecipeParams createParams() {
        return new ItemApplicationRecipeParams();
    }

    @Override
    public ItemApplicationRecipe.Builder<R> self() {
        return this;
    }

    public ItemApplicationRecipe.Builder<R> toolNotConsumed() {
        ((ItemApplicationRecipeParams)this.params).keepHeldItem = true;
        return this;
    }
}
