/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package dev.simulated_team.simulated.content.linked_typewriter;

import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

private class LinkedTypewriterMenuImpl.GhostSlotHandler
extends SlotItemHandler {
    public LinkedTypewriterMenuImpl.GhostSlotHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public boolean isFake() {
        return true;
    }

    public boolean isActive() {
        return LinkedTypewriterMenuImpl.this.slotsActive;
    }
}
