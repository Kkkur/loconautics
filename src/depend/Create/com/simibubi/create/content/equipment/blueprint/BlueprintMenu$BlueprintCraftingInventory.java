/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.TransientCraftingContainer
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.equipment.blueprint;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

static class BlueprintMenu.BlueprintCraftingInventory
extends TransientCraftingContainer {
    public BlueprintMenu.BlueprintCraftingInventory(AbstractContainerMenu menu, ItemStackHandler items) {
        super(menu, 3, 3);
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                ItemStack stack = items.getStackInSlot(y * 3 + x);
                this.setItem(y * 3 + x, stack == null ? ItemStack.EMPTY : stack.copy());
            }
        }
    }
}
