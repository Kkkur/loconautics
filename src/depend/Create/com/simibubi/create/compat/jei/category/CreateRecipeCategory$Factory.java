/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.crafting.Recipe
 */
package com.simibubi.create.compat.jei.category;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import net.minecraft.world.item.crafting.Recipe;

public static interface CreateRecipeCategory.Factory<T extends Recipe<?>> {
    public CreateRecipeCategory<T> create(CreateRecipeCategory.Info<T> var1);
}
