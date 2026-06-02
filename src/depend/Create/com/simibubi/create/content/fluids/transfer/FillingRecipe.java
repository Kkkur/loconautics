/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
 */
package com.simibubi.create.content.fluids.transfer;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class FillingRecipe
extends StandardProcessingRecipe<SingleRecipeInput>
implements IAssemblyRecipe {
    public FillingRecipe(ProcessingRecipeParams params) {
        super((IRecipeTypeInfo)AllRecipeTypes.FILLING, params);
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
    protected int getMaxFluidInputCount() {
        return 1;
    }

    public SizedFluidIngredient getRequiredFluid() {
        if (this.fluidIngredients.isEmpty()) {
            throw new IllegalStateException("Filling Recipe has no fluid ingredient!");
        }
        return (SizedFluidIngredient)this.fluidIngredients.get(0);
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {
    }

    @Override
    public void addAssemblyFluidIngredients(List<SizedFluidIngredient> list) {
        list.add(this.getRequiredFluid());
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public Component getDescriptionForAssembly() {
        List<FluidStack> matchingFluidStacks = Arrays.asList(((SizedFluidIngredient)this.fluidIngredients.get(0)).getFluids());
        if (matchingFluidStacks.size() == 0) {
            return Component.literal((String)"Invalid");
        }
        return CreateLang.translateDirect("recipe.assembly.spout_filling_fluid", matchingFluidStacks.get(0).getHoverName().getString());
    }

    @Override
    public void addRequiredMachines(Set<ItemLike> list) {
        list.add((ItemLike)AllBlocks.SPOUT.get());
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> SequencedAssemblySubCategory.AssemblySpouting::new;
    }
}
