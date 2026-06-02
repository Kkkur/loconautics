/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package com.simibubi.create.content.trains.schedule;

import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

class ScheduleMenu.InactiveItemHandlerSlot
extends SlotItemHandler {
    private int targetIndex;

    public ScheduleMenu.InactiveItemHandlerSlot(IItemHandler itemHandler, int targetIndex, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.targetIndex = targetIndex;
    }

    public boolean isActive() {
        return ScheduleMenu.this.slotsActive && this.targetIndex < ScheduleMenu.this.targetSlotsActive;
    }
}
