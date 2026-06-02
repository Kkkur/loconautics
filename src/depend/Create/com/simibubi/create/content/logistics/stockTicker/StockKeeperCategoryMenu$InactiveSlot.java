/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.inventory.Slot
 */
package com.simibubi.create.content.logistics.stockTicker;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

class StockKeeperCategoryMenu.InactiveSlot
extends Slot {
    public StockKeeperCategoryMenu.InactiveSlot(Container pContainer, int pIndex, int pX, int pY) {
        super(pContainer, pIndex, pX, pY);
    }

    public boolean isActive() {
        return StockKeeperCategoryMenu.this.slotsActive;
    }
}
