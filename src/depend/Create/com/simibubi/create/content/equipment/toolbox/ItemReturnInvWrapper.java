/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper
 */
package com.simibubi.create.content.equipment.toolbox;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

public class ItemReturnInvWrapper
extends PlayerMainInvWrapper {
    public ItemReturnInvWrapper(Inventory inv) {
        super(inv);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slot >= 0 && slot < 9) {
            return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }
}
