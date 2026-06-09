package com.lycoris.loconautics.foundation.menu;

import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * Reusable two-row frequency-binding menu (forward + backward rows, 2 ghost slots each).
 *
 * <p>Any block that needs a Create-style "bind two frequencies" GUI can extend this class
 * and provide:
 * <ul>
 *   <li>{@link #createOnClient} — resolve the {@code BlockEntity} from the buf on the client
 *   <li>{@link #createGhostInventory} — populate the 4-slot ghost inventory from the BE
 *   <li>{@link #saveData} — write ghost slots back to the BE on close
 * </ul>
 *
 * <p>The row labels ("Forward" / "Backward" in the analog controller) are supplied as
 * {@link Component}s so each block can localise or rename them without touching this class.
 * They are stored publicly for the screen to read.
 *
 * <p>Slot layout (indices relative to the menu):
 * <pre>
 *   0–35   player inventory (gated by {@link #slotsActive})
 *   36     first row,  first  ghost slot  (red)
 *   37     first row,  second ghost slot  (blue)
 *   38     second row, first  ghost slot  (red)
 *   39     second row, second ghost slot  (blue)
 * </pre>
 *
 * <p>Ghost-slot pixel positions (panel-relative, matches {@code frecuency_bind.png}):
 * <pre>
 *   Row 1: red x=94 y=32,  blue x=112 y=32
 *   Row 2: red x=94 y=61,  blue x=112 y=61
 * </pre>
 *
 * @param <T> the concrete {@link BlockEntity} type that owns the frequency data
 */
public abstract class BindFrequencyMenu<T extends BlockEntity> extends GhostItemMenu<T> {

    /** Set {@code true} by the screen on open to enable player slot interaction. */
    public boolean slotsActive = false;

    /**
     * Label shown for the first (top) frequency row.
     * Read by {@link com.lycoris.loconautics.foundation.screen.BindFrequencyScreen}.
     */
    public final Component firstRowLabel;

    /**
     * Label shown for the second (bottom) frequency row.
     * Read by {@link com.lycoris.loconautics.foundation.screen.BindFrequencyScreen}.
     */
    public final Component secondRowLabel;

    // ------------------------------------------------------------------ constructors

    /**
     * Client-side constructor — called by the {@link MenuType} factory when the server
     * sends an open-menu packet.
     *
     * @param type           the registered {@link MenuType}
     * @param id             container id
     * @param inv            player inventory
     * @param extraData      byte buf written by the server; must begin with the two
     *                       label {@link Component}s (written via
     *                       {@link net.minecraft.network.chat.ComponentSerialization})
     * @param firstRowLabel  decoded first-row label (callers decode from {@code extraData}
     *                       before delegating to this constructor)
     * @param secondRowLabel decoded second-row label
     */
    protected BindFrequencyMenu(MenuType<?> type, int id, Inventory inv,
                                net.minecraft.network.RegistryFriendlyByteBuf extraData,
                                Component firstRowLabel, Component secondRowLabel) {
        super(type, id, inv, extraData);
        this.firstRowLabel  = firstRowLabel;
        this.secondRowLabel = secondRowLabel;
    }

    /**
     * Server-side constructor — called directly when the server opens the menu for a player.
     *
     * @param type           the registered {@link MenuType}
     * @param id             container id
     * @param inv            player inventory
     * @param be             the owning block entity
     * @param firstRowLabel  label for the first frequency row
     * @param secondRowLabel label for the second frequency row
     */
    protected BindFrequencyMenu(MenuType<?> type, int id, Inventory inv, T be,
                                Component firstRowLabel, Component secondRowLabel) {
        super(type, id, inv, be);
        this.firstRowLabel  = firstRowLabel;
        this.secondRowLabel = secondRowLabel;
    }

    // ------------------------------------------------------------------ GhostItemMenu impl

    @Override
    protected void addSlots() {
        addPlayerSlots(27, 118);

        // First-row ghost slots
        addSlot(new GhostSlot((IItemHandler) ghostInventory, 0, 94,  32));
        addSlot(new GhostSlot((IItemHandler) ghostInventory, 1, 112, 32));

        // Second-row ghost slots
        addSlot(new GhostSlot((IItemHandler) ghostInventory, 2, 94,  61));
        addSlot(new GhostSlot((IItemHandler) ghostInventory, 3, 112, 61));
    }

    @Override
    protected void addPlayerSlots(int x, int y) {
        // Hotbar first (y+58), then main inventory rows — gated by slotsActive
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            addSlot(new PlayerSlot(playerInventory, hotbarSlot, x + hotbarSlot * 18, y + 58));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new PlayerSlot(playerInventory, col + row * 9 + 9,
                        x + col * 18, y + row * 18));
            }
        }
    }

    @Override
    protected boolean allowRepeats() {
        return true;
    }

    // ------------------------------------------------------------------ inner slot types

    private class PlayerSlot extends Slot {
        public PlayerSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean isActive() {
            return slotsActive;
        }
    }

    private static class GhostSlot extends SlotItemHandler {
        public GhostSlot(IItemHandler itemHandler, int index, int x, int y) {
            super(itemHandler, index, x, y);
        }

        @Override
        public boolean isFake() {
            return true;
        }

        @Override
        public boolean isActive() {
            return true;
        }
    }
}