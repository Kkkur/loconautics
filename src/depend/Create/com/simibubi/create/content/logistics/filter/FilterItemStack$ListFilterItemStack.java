/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.logistics.filter;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.filter.ListFilterItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public static class FilterItemStack.ListFilterItemStack
extends FilterItemStack {
    public List<FilterItemStack> containedItems;
    public boolean shouldRespectNBT;
    public boolean isBlacklist;

    public FilterItemStack.ListFilterItemStack(ItemStack filter) {
        super(filter);
        boolean hasFilterItems = filter.has(AllDataComponents.FILTER_ITEMS);
        this.containedItems = new ArrayList<FilterItemStack>();
        ItemStackHandler items = ((ListFilterItem)filter.getItem()).getFilterItemHandler(filter);
        for (int i = 0; i < items.getSlots(); ++i) {
            ItemStack stackInSlot = items.getStackInSlot(i);
            if (stackInSlot.isEmpty()) continue;
            this.containedItems.add(FilterItemStack.of(stackInSlot));
        }
        this.shouldRespectNBT = hasFilterItems && (Boolean)filter.getOrDefault(AllDataComponents.FILTER_ITEMS_RESPECT_NBT, (Object)false) != false;
        this.isBlacklist = hasFilterItems && (Boolean)filter.getOrDefault(AllDataComponents.FILTER_ITEMS_BLACKLIST, (Object)false) != false;
    }

    @Override
    public boolean test(Level world, ItemStack stack, boolean matchNBT) {
        for (FilterItemStack filterItemStack : this.containedItems) {
            if (!filterItemStack.test(world, stack, this.shouldRespectNBT)) continue;
            return !this.isBlacklist;
        }
        return this.isBlacklist;
    }

    @Override
    public boolean test(Level world, FluidStack stack, boolean matchNBT) {
        for (FilterItemStack filterItemStack : this.containedItems) {
            if (!filterItemStack.test(world, stack, this.shouldRespectNBT)) continue;
            return !this.isBlacklist;
        }
        return this.isBlacklist;
    }
}
