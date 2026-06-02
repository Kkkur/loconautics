/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.kinetics.crusher;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

@ParametersAreNonnullByDefault
public class CrushingRecipe
extends AbstractCrushingRecipe {
    public CrushingRecipe(ProcessingRecipeParams params) {
        super((IRecipeTypeInfo)AllRecipeTypes.CRUSHING, params);
    }

    public boolean matches(RecipeInput inv, Level worldIn) {
        if (inv.isEmpty()) {
            return false;
        }
        return ((Ingredient)this.ingredients.get(0)).test(inv.getItem(0));
    }

    @Override
    protected int getMaxOutputCount() {
        return 7;
    }
}
