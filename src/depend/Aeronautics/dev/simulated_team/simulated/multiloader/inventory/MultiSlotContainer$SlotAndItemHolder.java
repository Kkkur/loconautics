/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package dev.simulated_team.simulated.multiloader.inventory;

import net.minecraft.world.item.ItemStack;

public record MultiSlotContainer.SlotAndItemHolder(int currentIndex, int nextIndex, ItemStack stack) {
}
