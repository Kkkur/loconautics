/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.IntAttached
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.foundation.item;

import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class CountedItemStackList {
    Map<Item, Set<ItemStackEntry>> items = new HashMap<Item, Set<ItemStackEntry>>();

    public CountedItemStackList(IItemHandler inventory, FilteringBehaviour filteringBehaviour) {
        for (int slot = 0; slot < inventory.getSlots(); ++slot) {
            ItemStack extractItem = inventory.getStackInSlot(slot);
            if (!filteringBehaviour.test(extractItem)) continue;
            this.add(extractItem);
        }
    }

    public Stream<IntAttached<MutableComponent>> getTopNames(int limit) {
        return this.items.values().stream().flatMap(Collection::stream).sorted(IntAttached.comparator()).limit(limit).map(entry -> IntAttached.with((int)entry.count(), (Object)entry.stack().getHoverName().copy()));
    }

    public void add(ItemStack stack) {
        this.add(stack, stack.getCount());
    }

    public void add(ItemStack stack, int amount) {
        if (stack.isEmpty()) {
            return;
        }
        Set<ItemStackEntry> stackSet = this.getOrCreateItemSet(stack);
        for (ItemStackEntry entry : stackSet) {
            if (!entry.matches(stack)) continue;
            entry.grow(amount);
            return;
        }
        stackSet.add(new ItemStackEntry(stack, amount));
    }

    private Set<ItemStackEntry> getOrCreateItemSet(ItemStack stack) {
        if (!this.items.containsKey(stack.getItem())) {
            this.items.put(stack.getItem(), new HashSet());
        }
        return this.getItemSet(stack);
    }

    private Set<ItemStackEntry> getItemSet(ItemStack stack) {
        return this.items.get(stack.getItem());
    }

    public static class ItemStackEntry
    extends IntAttached<ItemStack> {
        public ItemStackEntry(ItemStack stack) {
            this(stack, stack.getCount());
        }

        public ItemStackEntry(ItemStack stack, int amount) {
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
}
