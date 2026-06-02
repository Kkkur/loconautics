/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.processing.basin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.world.item.ItemStack;

public class BasinInventory
extends SmartInventory {
    private BasinBlockEntity blockEntity;
    public boolean packagerMode;

    public BasinInventory(int slots, BasinBlockEntity be) {
        super(slots, be, 64, true);
        this.blockEntity = be;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (this.packagerMode) {
            return this.inv.insertItem(slot, stack, simulate);
        }
        int firstFreeSlot = -1;
        for (int i = 0; i < this.getSlots(); ++i) {
            if (i != slot && ItemStack.isSameItemSameComponents((ItemStack)stack, (ItemStack)this.inv.getStackInSlot(i))) {
                return stack;
            }
            if (!this.inv.getStackInSlot(i).isEmpty() || firstFreeSlot != -1) continue;
            firstFreeSlot = i;
        }
        if (this.inv.getStackInSlot(slot).isEmpty() && firstFreeSlot != slot) {
            return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack extractItem = super.extractItem(slot, amount, simulate);
        if (!simulate && !extractItem.isEmpty()) {
            this.blockEntity.notifyChangeOfContents();
        }
        return extractItem;
    }
}
