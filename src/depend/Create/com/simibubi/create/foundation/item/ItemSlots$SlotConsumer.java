/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.foundation.item;

import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public static interface ItemSlots.SlotConsumer {
    public void accept(int var1, ItemStack var2);
}
