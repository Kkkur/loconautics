/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package dev.simulated_team.simulated.content.linked_typewriter;

import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.LinkedTypewriterMenuCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class LinkedTypewriterMenuImpl
extends LinkedTypewriterMenuCommon {
    public LinkedTypewriterMenuImpl(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public LinkedTypewriterMenuImpl(MenuType<?> type, int id, Inventory inv, LinkedTypewriterBlockEntity be) {
        super(type, id, inv, be);
    }

    protected ItemStackHandler createGhostInventory() {
        return new ItemStackHandler(2);
    }

    protected void addSlots() {
        this.addPlayerSlots(38, 59);
        for (int i = 0; i < 2; ++i) {
            this.addSlot((Slot)new GhostSlotHandler((IItemHandler)this.ghostInventory, i, 105 + i * 18, 1));
        }
    }

    private class GhostSlotHandler
    extends SlotItemHandler {
        public GhostSlotHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        public boolean isFake() {
            return true;
        }

        public boolean isActive() {
            return LinkedTypewriterMenuImpl.this.slotsActive;
        }
    }
}
