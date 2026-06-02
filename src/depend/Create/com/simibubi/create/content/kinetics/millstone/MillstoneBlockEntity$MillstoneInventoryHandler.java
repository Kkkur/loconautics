/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.wrapper.CombinedInvWrapper
 */
package com.simibubi.create.content.kinetics.millstone;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

private class MillstoneBlockEntity.MillstoneInventoryHandler
extends CombinedInvWrapper {
    public MillstoneBlockEntity.MillstoneInventoryHandler() {
        super(new IItemHandlerModifiable[]{MillstoneBlockEntity.this.inputInv, MillstoneBlockEntity.this.outputInv});
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        if (MillstoneBlockEntity.this.outputInv == this.getHandlerFromIndex(this.getIndexForSlot(slot))) {
            return false;
        }
        return MillstoneBlockEntity.this.canProcess(stack) && super.isItemValid(slot, stack);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (MillstoneBlockEntity.this.outputInv == this.getHandlerFromIndex(this.getIndexForSlot(slot))) {
            return stack;
        }
        if (!this.isItemValid(slot, stack)) {
            return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (MillstoneBlockEntity.this.inputInv == this.getHandlerFromIndex(this.getIndexForSlot(slot))) {
            return ItemStack.EMPTY;
        }
        return super.extractItem(slot, amount, simulate);
    }
}
