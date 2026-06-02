/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.DyedItemColor
 */
package com.simibubi.create.content.redstone.link;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public static class RedstoneLinkNetworkHandler.Frequency {
    public static final RedstoneLinkNetworkHandler.Frequency EMPTY = new RedstoneLinkNetworkHandler.Frequency(ItemStack.EMPTY);
    private static final Map<Item, RedstoneLinkNetworkHandler.Frequency> simpleFrequencies = new IdentityHashMap<Item, RedstoneLinkNetworkHandler.Frequency>();
    private ItemStack stack;
    private Item item;
    private int color;

    public static RedstoneLinkNetworkHandler.Frequency of(ItemStack stack) {
        if (stack.isEmpty()) {
            return EMPTY;
        }
        if (stack.getComponents().isEmpty()) {
            return simpleFrequencies.computeIfAbsent(stack.getItem(), $ -> new RedstoneLinkNetworkHandler.Frequency(stack));
        }
        return new RedstoneLinkNetworkHandler.Frequency(stack);
    }

    private RedstoneLinkNetworkHandler.Frequency(ItemStack stack) {
        this.stack = stack;
        this.item = stack.getItem();
        this.color = stack.has(DataComponents.DYED_COLOR) ? ((DyedItemColor)stack.get(DataComponents.DYED_COLOR)).rgb() : -1;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public int hashCode() {
        return this.item.hashCode() * 31 ^ this.color;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof RedstoneLinkNetworkHandler.Frequency ? ((RedstoneLinkNetworkHandler.Frequency)obj).item == this.item && ((RedstoneLinkNetworkHandler.Frequency)obj).color == this.color : false;
    }
}
