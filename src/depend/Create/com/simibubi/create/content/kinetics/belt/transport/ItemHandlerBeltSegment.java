/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.content.kinetics.belt.transport;

import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemHandlerBeltSegment
implements IItemHandler {
    private final BeltInventory beltInventory;
    int offset;

    public ItemHandlerBeltSegment(BeltInventory beltInventory, int offset) {
        this.beltInventory = beltInventory;
        this.offset = offset;
    }

    public int getSlots() {
        return 1;
    }

    public ItemStack getStackInSlot(int slot) {
        TransportedItemStack stackAtOffset = this.beltInventory.getStackAtOffset(this.offset);
        if (stackAtOffset == null) {
            return ItemStack.EMPTY;
        }
        return stackAtOffset.stack;
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (this.beltInventory.canInsertAt(this.offset)) {
            ItemStack remainder = ItemHelper.limitCountToMaxStackSize(stack, simulate);
            if (!simulate) {
                TransportedItemStack newStack = new TransportedItemStack(stack);
                newStack.insertedAt = this.offset;
                newStack.prevBeltPosition = newStack.beltPosition = (float)this.offset + 0.5f + (float)(this.beltInventory.beltMovementPositive ? -1 : 1) / 16.0f;
                this.beltInventory.addItem(newStack);
                this.beltInventory.belt.setChanged();
                this.beltInventory.belt.sendData();
            }
            return remainder;
        }
        return stack;
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack extracted;
        TransportedItemStack transported = this.beltInventory.getStackAtOffset(this.offset);
        if (transported == null) {
            return ItemStack.EMPTY;
        }
        amount = Math.min(amount, transported.stack.getCount());
        ItemStack itemStack = extracted = simulate ? transported.stack.copy().split(amount) : transported.stack.split(amount);
        if (!simulate) {
            if (transported.stack.isEmpty()) {
                this.beltInventory.toRemove.add(transported);
            } else {
                this.beltInventory.belt.notifyUpdate();
            }
        }
        return extracted;
    }

    public int getSlotLimit(int slot) {
        return (Integer)this.getStackInSlot(slot).getOrDefault(DataComponents.MAX_STACK_SIZE, (Object)64);
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }
}
