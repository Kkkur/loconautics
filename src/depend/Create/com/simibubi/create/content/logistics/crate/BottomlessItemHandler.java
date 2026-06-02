/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.logistics.crate;

import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BottomlessItemHandler
extends ItemStackHandler {
    private Supplier<ItemStack> suppliedItemStack;

    public BottomlessItemHandler(Supplier<ItemStack> suppliedItemStack) {
        this.suppliedItemStack = suppliedItemStack;
    }

    public int getSlots() {
        return 2;
    }

    public ItemStack getStackInSlot(int slot) {
        ItemStack stack = this.suppliedItemStack.get();
        if (slot == 1) {
            return ItemStack.EMPTY;
        }
        if (stack == null) {
            return ItemStack.EMPTY;
        }
        if (!stack.isEmpty()) {
            return stack.copyWithCount(stack.getMaxStackSize());
        }
        return stack;
    }

    public void setStackInSlot(int slot, ItemStack stack) {
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return ItemStack.EMPTY;
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack stack = this.suppliedItemStack.get();
        if (slot == 1) {
            return ItemStack.EMPTY;
        }
        if (stack == null) {
            return ItemStack.EMPTY;
        }
        if (!stack.isEmpty()) {
            return stack.copyWithCount(Math.min(stack.getMaxStackSize(), amount));
        }
        return ItemStack.EMPTY;
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }
}
