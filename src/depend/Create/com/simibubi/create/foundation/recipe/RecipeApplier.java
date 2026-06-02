/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.foundation.recipe;

import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public class RecipeApplier {
    public static void applyRecipeOn(ItemEntity entity, Recipe<?> recipe, boolean returnProcessingRemainder) {
        List<ItemStack> stacks = RecipeApplier.applyRecipeOn(entity.level(), entity.getItem(), recipe, returnProcessingRemainder);
        if (stacks.isEmpty()) {
            entity.discard();
            return;
        }
        entity.setItem(stacks.removeFirst());
        for (ItemStack additional : stacks) {
            ItemEntity entityIn = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), additional);
            entityIn.setDeltaMovement(entity.getDeltaMovement());
            entity.level().addFreshEntity((Entity)entityIn);
        }
    }

    public static List<ItemStack> applyRecipeOn(Level level, ItemStack stackIn, Recipe<?> recipe, boolean returnProcessingRemainder) {
        ArrayList<ItemStack> stacks;
        if (recipe instanceof ProcessingRecipe) {
            ProcessingRecipe pr = (ProcessingRecipe)recipe;
            stacks = new ArrayList();
            for (int i = 0; i < stackIn.getCount(); ++i) {
                List<ProcessingOutput> list;
                if (pr instanceof ManualApplicationRecipe) {
                    ManualApplicationRecipe mar = (ManualApplicationRecipe)pr;
                    list = mar.getRollableResults();
                } else {
                    list = pr.getRollableResults();
                }
                List<ProcessingOutput> outputs = list;
                for (ItemStack stack : pr.rollResults(outputs, level.random)) {
                    for (ItemStack previouslyRolled : stacks) {
                        if (stack.isEmpty() || !ItemStack.isSameItemSameComponents((ItemStack)stack, (ItemStack)previouslyRolled)) continue;
                        int amount = Math.min(previouslyRolled.getMaxStackSize() - previouslyRolled.getCount(), stack.getCount());
                        previouslyRolled.grow(amount);
                        stack.shrink(amount);
                    }
                    if (stack.isEmpty()) continue;
                    stacks.add(stack);
                }
                if (!returnProcessingRemainder || !stackIn.hasCraftingRemainingItem()) continue;
                ItemHelper.addToList(stackIn.getCraftingRemainingItem(), stacks);
            }
        } else {
            ItemStack out = recipe.getResultItem((HolderLookup.Provider)level.registryAccess()).copy();
            stacks = ItemHelper.multipliedOutput(stackIn, out);
        }
        return stacks;
    }
}
