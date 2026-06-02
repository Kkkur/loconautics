/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.IntAttached
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.foundation.item;

import net.createmod.catnip.data.IntAttached;
import net.minecraft.world.item.ItemStack;

public static class CountedItemStackList.ItemStackEntry
extends IntAttached<ItemStack> {
    public CountedItemStackList.ItemStackEntry(ItemStack stack) {
        this(stack, stack.getCount());
    }

    public CountedItemStackList.ItemStackEntry(ItemStack stack, int amount) {
        super(Integer.valueOf(amount), (Object)stack);
    }

    public boolean matches(ItemStack other) {
        return ItemStack.isSameItemSameComponents((ItemStack)other, (ItemStack)this.stack());
    }

    public ItemStack stack() {
        return (ItemStack)this.getSecond();
    }

    public void grow(int amount) {
        this.setFirst((Integer)this.getFirst() + amount);
    }

    public int count() {
        return (Integer)this.getFirst();
    }
}
