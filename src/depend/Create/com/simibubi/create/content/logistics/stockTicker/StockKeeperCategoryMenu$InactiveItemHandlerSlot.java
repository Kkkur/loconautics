/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.content.logistics.filter.FilterItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

class StockKeeperCategoryMenu.InactiveItemHandlerSlot
extends SlotItemHandler {
    public StockKeeperCategoryMenu.InactiveItemHandlerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public boolean mayPlace(@NotNull ItemStack stack) {
        return super.mayPlace(stack) && (stack.isEmpty() || stack.getItem() instanceof FilterItem);
    }

    public boolean isActive() {
        return StockKeeperCategoryMenu.this.slotsActive;
    }
}
