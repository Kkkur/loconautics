/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.NonNullList
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingInput
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
 *  net.neoforged.neoforge.items.IItemHandler
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.processing.basin;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class BasinRecipe
extends StandardProcessingRecipe<RecipeInput> {
    public static boolean match(BasinBlockEntity basin, Recipe<?> recipe) {
        BasinRecipe basinRecipe;
        FilteringBehaviour filter = basin.getFilter();
        if (filter == null) {
            return false;
        }
        boolean filterTest = filter.test(recipe.getResultItem((HolderLookup.Provider)basin.getLevel().registryAccess()));
        if (recipe instanceof BasinRecipe && (basinRecipe = (BasinRecipe)recipe).getRollableResults().isEmpty() && !basinRecipe.getFluidResults().isEmpty()) {
            filterTest = filter.test((FluidStack)basinRecipe.getFluidResults().get(0));
        }
        if (!filterTest) {
            return false;
        }
        return BasinRecipe.apply(basin, recipe, true);
    }

    public static boolean apply(BasinBlockEntity basin, Recipe<?> recipe) {
        return BasinRecipe.apply(basin, recipe, false);
    }

    private static boolean apply(BasinBlockEntity basin, Recipe<?> recipe, boolean test) {
        boolean isBasinRecipe = recipe instanceof BasinRecipe;
        IItemHandler availableItems = (IItemHandler)basin.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, basin.getBlockPos(), null);
        IFluidHandler availableFluids = (IFluidHandler)basin.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, basin.getBlockPos(), null);
        if (availableItems == null || availableFluids == null) {
            return false;
        }
        BlazeBurnerBlock.HeatLevel heat = basin.getHeatLevel();
        if (isBasinRecipe && !((BasinRecipe)recipe).getRequiredHeat().testBlazeBurner(heat)) {
            return false;
        }
        ArrayList<ItemStack> recipeOutputItems = new ArrayList<ItemStack>();
        ArrayList<FluidStack> recipeOutputFluids = new ArrayList<FluidStack>();
        LinkedList ingredients = new LinkedList(recipe.getIngredients());
        List fluidIngredients = isBasinRecipe ? ((BasinRecipe)recipe).getFluidIngredients() : Collections.emptyList();
        for (boolean simulate : Iterate.trueAndFalse) {
            Object ingredient2;
            if (!simulate && test) {
                return true;
            }
            int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
            int[] extractedFluidsFromTank = new int[availableFluids.getTanks()];
            block1: for (Object ingredient2 : ingredients) {
                for (int slot = 0; slot < availableItems.getSlots(); ++slot) {
                    ItemStack extracted;
                    if (simulate && availableItems.getStackInSlot(slot).getCount() <= extractedItemsFromSlot[slot] || !ingredient2.test(extracted = availableItems.extractItem(slot, 1, true))) continue;
                    if (!simulate) {
                        availableItems.extractItem(slot, 1, false);
                    }
                    int n = slot;
                    extractedItemsFromSlot[n] = extractedItemsFromSlot[n] + 1;
                    continue block1;
                }
                return false;
            }
            boolean fluidsAffected = false;
            ingredient2 = fluidIngredients.iterator();
            block3: while (ingredient2.hasNext()) {
                SizedFluidIngredient fluidIngredient = (SizedFluidIngredient)ingredient2.next();
                int amountRequired = fluidIngredient.amount();
                for (int tank = 0; tank < availableFluids.getTanks(); ++tank) {
                    FluidStack fluidStack = availableFluids.getFluidInTank(tank);
                    if (simulate && fluidStack.getAmount() <= extractedFluidsFromTank[tank] || !fluidIngredient.test(fluidStack)) continue;
                    int drainedAmount = Math.min(amountRequired, fluidStack.getAmount());
                    if (!simulate) {
                        fluidStack.shrink(drainedAmount);
                        fluidsAffected = true;
                    }
                    if ((amountRequired -= drainedAmount) != 0) continue;
                    int n = tank;
                    extractedFluidsFromTank[n] = extractedFluidsFromTank[n] + drainedAmount;
                    continue block3;
                }
                return false;
            }
            if (fluidsAffected) {
                basin.getBehaviour(SmartFluidTankBehaviour.INPUT).forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
                basin.getBehaviour(SmartFluidTankBehaviour.OUTPUT).forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
            }
            if (simulate) {
                CraftingInput remainderInput = new DummyCraftingContainer(availableItems, extractedItemsFromSlot).asCraftInput();
                if (recipe instanceof BasinRecipe) {
                    BasinRecipe basinRecipe = (BasinRecipe)recipe;
                    recipeOutputItems.addAll(basinRecipe.rollResults(basin.getLevel().random));
                    for (FluidStack fluidStack : basinRecipe.getFluidResults()) {
                        if (fluidStack.isEmpty()) continue;
                        recipeOutputFluids.add(fluidStack);
                    }
                    for (ItemStack stack : basinRecipe.getRemainingItems((RecipeInput)remainderInput)) {
                        if (stack.isEmpty()) continue;
                        recipeOutputItems.add(stack);
                    }
                } else {
                    recipeOutputItems.add(recipe.getResultItem((HolderLookup.Provider)basin.getLevel().registryAccess()));
                    if (recipe instanceof CraftingRecipe) {
                        CraftingRecipe craftingRecipe = (CraftingRecipe)recipe;
                        for (ItemStack stack : craftingRecipe.getRemainingItems((RecipeInput)remainderInput)) {
                            if (stack.isEmpty()) continue;
                            recipeOutputItems.add(stack);
                        }
                    }
                }
            }
            if (basin.acceptOutputs(recipeOutputItems, recipeOutputFluids, simulate)) continue;
            return false;
        }
        return true;
    }

    public static RecipeHolder<BasinRecipe> convertShapeless(RecipeHolder<?> recipe) {
        BasinRecipe basinRecipe = (BasinRecipe)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)new StandardProcessingRecipe.Builder<BasinRecipe>(BasinRecipe::new, recipe.id()).withItemIngredients((NonNullList<Ingredient>)recipe.value().getIngredients())).withSingleItemOutput(recipe.value().getResultItem((HolderLookup.Provider)Minecraft.getInstance().level.registryAccess()))).build();
        return new RecipeHolder(recipe.id(), (Recipe)basinRecipe);
    }

    protected BasinRecipe(IRecipeTypeInfo type, ProcessingRecipeParams params) {
        super(type, params);
    }

    public BasinRecipe(ProcessingRecipeParams params) {
        this((IRecipeTypeInfo)AllRecipeTypes.BASIN, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 64;
    }

    @Override
    protected int getMaxOutputCount() {
        return 4;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 2;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 2;
    }

    @Override
    protected boolean canRequireHeat() {
        return true;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    public boolean matches(RecipeInput input, @NotNull Level worldIn) {
        return false;
    }
}
