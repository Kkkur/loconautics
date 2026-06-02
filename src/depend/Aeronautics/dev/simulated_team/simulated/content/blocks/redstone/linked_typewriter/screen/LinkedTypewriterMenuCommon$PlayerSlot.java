/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.inventory.Slot
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

private class LinkedTypewriterMenuCommon.PlayerSlot
extends Slot {
    public LinkedTypewriterMenuCommon.PlayerSlot(Container pContainer, int pIndex, int pX, int pY) {
        super(pContainer, pIndex, pX, pY);
    }

    public boolean isActive() {
        return LinkedTypewriterMenuCommon.this.slotsActive;
    }
}
