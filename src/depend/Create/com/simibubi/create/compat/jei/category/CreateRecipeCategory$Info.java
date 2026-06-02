/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.gui.drawable.IDrawable
 *  mezz.jei.api.recipe.RecipeType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 */
package com.simibubi.create.compat.jei.category;

import java.util.List;
import java.util.function.Supplier;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public record CreateRecipeCategory.Info<T extends Recipe<?>>(RecipeType<RecipeHolder<T>> recipeType, Component title, IDrawable background, IDrawable icon, Supplier<List<RecipeHolder<T>>> recipes, List<Supplier<? extends ItemStack>> catalysts) {
}
