/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.TransientCraftingContainer
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.equipment.blueprint;

import java.util.Map;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;

static class BlueprintEntity.BlueprintCraftingInventory
extends TransientCraftingContainer {
    private static final AbstractContainerMenu dummyContainer = new AbstractContainerMenu(null, -1){

        public boolean stillValid(Player playerIn) {
            return false;
        }

        public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
            return ItemStack.EMPTY;
        }
    };

    public BlueprintEntity.BlueprintCraftingInventory(Map<Integer, ItemStack> items) {
        super(dummyContainer, 3, 3);
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                ItemStack stack = items.get(y * 3 + x);
                this.setItem(y * 3 + x, stack == null ? ItemStack.EMPTY : stack.copy());
            }
        }
    }
}
