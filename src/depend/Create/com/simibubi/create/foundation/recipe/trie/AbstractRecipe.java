/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.crafting.Recipe
 */
package com.simibubi.create.foundation.recipe.trie;

import com.simibubi.create.foundation.recipe.trie.AbstractIngredient;
import java.util.Set;
import net.minecraft.world.item.crafting.Recipe;

public class AbstractRecipe<R extends Recipe<?>> {
    final R recipe;
    final Set<AbstractIngredient> ingredients;

    public AbstractRecipe(R recipe, Set<AbstractIngredient> ingredients) {
        this.recipe = recipe;
        this.ingredients = ingredients;
    }
}
