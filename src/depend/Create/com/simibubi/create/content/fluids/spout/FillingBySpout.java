/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
 */
package com.simibubi.create.content.fluids.spout;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class FillingBySpout {
    public static boolean canItemBeFilled(Level world, ItemStack stack) {
        SingleRecipeInput input = new SingleRecipeInput(stack);
        Optional<RecipeHolder<FillingRecipe>> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(world, input, AllRecipeTypes.FILLING.getType(), FillingRecipe.class);
        if (assemblyRecipe.isPresent()) {
            return true;
        }
        if (AllRecipeTypes.FILLING.find(input, world).isPresent()) {
            return true;
        }
        return GenericItemFilling.canItemBeFilled(world, stack);
    }

    public static int getRequiredAmountForItem(Level world, ItemStack stack, FluidStack availableFluid) {
        SizedFluidIngredient requiredFluid;
        SingleRecipeInput input = new SingleRecipeInput(stack);
        Optional<RecipeHolder<FillingRecipe>> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(world, input, AllRecipeTypes.FILLING.getType(), FillingRecipe.class, FillingBySpout.matchItemAndFluid(world, availableFluid, input));
        if (assemblyRecipe.isPresent() && (requiredFluid = ((FillingRecipe)assemblyRecipe.get().value()).getRequiredFluid()).ingredient().test(availableFluid)) {
            return requiredFluid.amount();
        }
        for (RecipeHolder recipe : world.getRecipeManager().getRecipesFor(AllRecipeTypes.FILLING.getType(), (RecipeInput)input, world)) {
            FillingRecipe fillingRecipe = (FillingRecipe)recipe.value();
            SizedFluidIngredient requiredFluid2 = fillingRecipe.getRequiredFluid();
            if (!requiredFluid2.ingredient().test(availableFluid)) continue;
            return requiredFluid2.amount();
        }
        return GenericItemFilling.getRequiredAmountForItem(world, stack, availableFluid);
    }

    public static ItemStack fillItem(Level level, int requiredAmount, ItemStack stack, FluidStack availableFluid) {
        FluidStack toFill = availableFluid.copy();
        toFill.setAmount(requiredAmount);
        SingleRecipeInput input = new SingleRecipeInput(stack);
        RecipeHolder fillingRecipe = SequencedAssemblyRecipe.getRecipe(level, input, AllRecipeTypes.FILLING.getType(), FillingRecipe.class, FillingBySpout.matchItemAndFluid(level, availableFluid, input)).filter(fr -> ((FillingRecipe)fr.value()).getRequiredFluid().test(toFill)).orElseGet(() -> {
            for (RecipeHolder recipe : level.getRecipeManager().getRecipesFor(AllRecipeTypes.FILLING.getType(), (RecipeInput)input, level)) {
                FillingRecipe fr = (FillingRecipe)recipe.value();
                SizedFluidIngredient requiredFluid = fr.getRequiredFluid();
                if (!requiredFluid.test(toFill)) continue;
                return new RecipeHolder(recipe.id(), (Recipe)fr);
            }
            return null;
        });
        if (fillingRecipe != null) {
            List<ItemStack> results = ((FillingRecipe)fillingRecipe.value()).rollResults(level.random);
            availableFluid.shrink(requiredAmount);
            stack.shrink(1);
            return results.isEmpty() ? ItemStack.EMPTY : results.get(0);
        }
        return GenericItemFilling.fillItem(level, requiredAmount, stack, availableFluid);
    }

    private static Predicate<RecipeHolder<FillingRecipe>> matchItemAndFluid(Level world, FluidStack availableFluid, SingleRecipeInput input) {
        return r -> ((FillingRecipe)r.value()).matches(input, world) && ((FillingRecipe)r.value()).getRequiredFluid().test(availableFluid);
    }
}
