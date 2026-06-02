/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids.transfer;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class EmptyingRecipe
extends StandardProcessingRecipe<SingleRecipeInput> {
    public EmptyingRecipe(ProcessingRecipeParams params) {
        super((IRecipeTypeInfo)AllRecipeTypes.EMPTYING, params);
    }

    public boolean matches(SingleRecipeInput inv, Level p_77569_2_) {
        return ((Ingredient)this.ingredients.get(0)).test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 1;
    }

    public FluidStack getResultingFluid() {
        if (this.fluidResults.isEmpty()) {
            throw new IllegalStateException("Emptying Recipe has no fluid output!");
        }
        return (FluidStack)this.fluidResults.get(0);
    }
}
