/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.StonecutterRecipe
 */
package com.simibubi.create.compat.jei.category;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public static class BlockCuttingCategory.CondensedBlockCuttingRecipe
extends StonecutterRecipe {
    List<ItemStack> outputs = new ArrayList<ItemStack>();

    public BlockCuttingCategory.CondensedBlockCuttingRecipe(Ingredient ingredient) {
        super("", ingredient, ItemStack.EMPTY);
    }

    public void addOutput(ItemStack stack) {
        this.outputs.add(stack);
    }

    public List<ItemStack> getOutputs() {
        return this.outputs;
    }

    public List<List<ItemStack>> getCondensedOutputs() {
        ArrayList<List<ItemStack>> result = new ArrayList<List<ItemStack>>();
        int index = 0;
        boolean firstPass = true;
        for (ItemStack itemStack : this.outputs) {
            if (firstPass) {
                result.add(new ArrayList());
            }
            ((List)result.get(index)).add(itemStack);
            if (++index < 15) continue;
            index = 0;
            firstPass = false;
        }
        return result;
    }

    public boolean isSpecial() {
        return true;
    }
}
