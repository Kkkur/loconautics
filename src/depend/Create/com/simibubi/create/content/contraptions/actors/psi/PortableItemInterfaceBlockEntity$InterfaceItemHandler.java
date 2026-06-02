/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 */
package com.simibubi.create.content.contraptions.actors.psi;

import com.simibubi.create.foundation.item.ItemHandlerWrapper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

class PortableItemInterfaceBlockEntity.InterfaceItemHandler
extends ItemHandlerWrapper {
    public PortableItemInterfaceBlockEntity.InterfaceItemHandler(IItemHandlerModifiable wrapped) {
        super(wrapped);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!PortableItemInterfaceBlockEntity.this.canTransfer()) {
            return ItemStack.EMPTY;
        }
        ItemStack extractItem = super.extractItem(slot, amount, simulate);
        if (!simulate && !extractItem.isEmpty()) {
            PortableItemInterfaceBlockEntity.this.onContentTransferred();
        }
        return extractItem;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!PortableItemInterfaceBlockEntity.this.canTransfer()) {
            return stack;
        }
        ItemStack insertItem = super.insertItem(slot, stack, simulate);
        if (!simulate && !ItemStack.matches((ItemStack)insertItem, (ItemStack)stack)) {
            PortableItemInterfaceBlockEntity.this.onContentTransferred();
        }
        return insertItem;
    }
}
