/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.Recipe
 */
package com.simibubi.create.compat.jei;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

private class CreateJEI.CategoryBuilder<T extends Recipe<?>>
extends CreateRecipeCategory.Builder<T> {
    public CreateJEI.CategoryBuilder(Class<? extends T> recipeClass) {
        super(recipeClass);
    }

    @Override
    public CreateRecipeCategory<T> build(ResourceLocation id, CreateRecipeCategory.Factory<T> factory) {
        CreateRecipeCategory<T> category = super.build(id, factory);
        CreateJEI.this.allCategories.add(category);
        return category;
    }
}
