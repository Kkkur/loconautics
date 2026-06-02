/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeType
 */
package com.simibubi.create.foundation.recipe;

import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import java.util.function.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

public class RecipeConditions {
    public static Predicate<RecipeHolder<? extends Recipe<?>>> isOfType(RecipeType<?> ... otherTypes) {
        return recipe -> {
            RecipeType recipeType = recipe.value().getType();
            for (RecipeType other : otherTypes) {
                if (recipeType != other) continue;
                return true;
            }
            return false;
        };
    }

    public static Predicate<RecipeHolder<? extends Recipe<?>>> firstIngredientMatches(ItemStack stack) {
        return r -> !r.value().getIngredients().isEmpty() && ((Ingredient)r.value().getIngredients().get(0)).test(stack);
    }

    public static Predicate<RecipeHolder<? extends Recipe<?>>> outputMatchesFilter(FilteringBehaviour filtering) {
        return r -> filtering.test(r.value().getResultItem((HolderLookup.Provider)filtering.getWorld().registryAccess()));
    }
}
