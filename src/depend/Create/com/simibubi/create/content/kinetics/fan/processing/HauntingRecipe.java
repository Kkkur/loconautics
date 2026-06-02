/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.kinetics.fan.processing;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

@ParametersAreNonnullByDefault
public class HauntingRecipe
extends StandardProcessingRecipe<SingleRecipeInput> {
    public HauntingRecipe(ProcessingRecipeParams params) {
        super((IRecipeTypeInfo)AllRecipeTypes.HAUNTING, params);
    }

    public boolean matches(SingleRecipeInput inv, Level worldIn) {
        if (inv.isEmpty()) {
            return false;
        }
        return ((Ingredient)this.ingredients.get(0)).test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 12;
    }
}
