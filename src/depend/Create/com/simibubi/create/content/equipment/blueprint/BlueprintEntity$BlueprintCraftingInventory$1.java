/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.equipment.blueprint;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

class BlueprintEntity.BlueprintCraftingInventory.1
extends AbstractContainerMenu {
    BlueprintEntity.BlueprintCraftingInventory.1(MenuType arg0, int arg1) {
        super(arg0, arg1);
    }

    public boolean stillValid(Player playerIn) {
        return false;
    }

    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return ItemStack.EMPTY;
    }
}
