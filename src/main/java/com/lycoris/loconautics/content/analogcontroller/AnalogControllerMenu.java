package com.lycoris.loconautics.content.analogcontroller;

import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * Frequency-selection menu for the Analog Controller.
 *
 * Extends {@link GhostItemMenu} so we get Create's ghost-slot click handling for free.
 * Two ghost slots (frequency first + second), no player inventory displayed.
 *
 * Slot layout inside the menu (GhostItemMenu.clicked uses slotId - 36):
 *   Slots 0-35  : player inventory (added but not shown — needed for GhostItemMenu arithmetic)
 *   Slot 36     : frequency first  (ghost)
 *   Slot 37     : frequency second (ghost)
 */
public class AnalogControllerMenu extends GhostItemMenu<AnalogControllerBlockEntity> {

    // ------------------------------------------------------------------ constructors

    /** Network / client-side constructor (called by IMenuTypeExtension). */
    public AnalogControllerMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    /** Server-side constructor. */
    public AnalogControllerMenu(MenuType<?> type, int id, Inventory inv, AnalogControllerBlockEntity be) {
        super(type, id, inv, be);
    }

    public static AnalogControllerMenu create(int id, Inventory inv, AnalogControllerBlockEntity be) {
        return new AnalogControllerMenu(LoconauticsRegistries.ANALOG_CONTROLLER_MENU.get(), id, inv, be);
    }

    // ------------------------------------------------------------------ GhostItemMenu impl

    /**
     * Reconstruct the BE reference on the client from what the server wrote in
     * {@link AnalogControllerBlock#useWithoutItem} via {@code sp.openMenu(ace, buf -> ...)}.
     *
     * Server writes: blockPos, then a CompoundTag produced by
     * {@link AnalogControllerBlockEntity#sendToMenu}.
     */
    @Override
    protected AnalogControllerBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos());
        if (be instanceof AnalogControllerBlockEntity ace) {
            ace.readClient(extraData.readNbt(), extraData.registryAccess());
            return ace;
        }
        return null;
    }

    /**
     * Create the 2-slot ghost inventory, pre-populated from the BE's current frequency.
     * {@code contentHolder} is non-null on the server; may be null on the client before
     * the BE is resolved (handled gracefully by ItemStack.EMPTY defaults).
     */
    @Override
    protected ItemStackHandler createGhostInventory() {
        ItemStackHandler handler = new ItemStackHandler(2);
        if (contentHolder != null) {
            handler.setStackInSlot(0, contentHolder.getFrequencyFirst().copy());
            handler.setStackInSlot(1, contentHolder.getFrequencySecond().copy());
        }
        return handler;
    }

    /**
     * Add slots.  GhostItemMenu.clicked() subtracts 36 to get the ghost slot index,
     * so we MUST add the 36 player inventory slots first even though they're never shown.
     *
     * Ghost slots follow at indices 36 + 0 and 36 + 1.
     * Their panel-relative positions (x=4,y=6 and x=22,y=6) match the texture atlas.
     */
    @Override
    protected void addSlots() {
        // Add the 36 player slots (off-screen at a position that won't interfere).
        // The -1000 offset means they are never rendered by the vanilla slot renderer.
        addPlayerSlots(-1000, -1000);

        // Ghost slots — positions are panel-relative (leftPos/topPos added by the screen).
        addSlot(new GhostSlot((IItemHandler) ghostInventory, 0, 4, 6));
        addSlot(new GhostSlot((IItemHandler) ghostInventory, 1, 22, 6));
    }

    /**
     * Called by {@link com.simibubi.create.foundation.gui.menu.MenuBase#removed} on the
     * server when the player closes the screen.  Push frequency back to the BE.
     */
    @Override
    protected void saveData(AnalogControllerBlockEntity be) {
        if (be == null) return;
        be.setFrequency(
                ghostInventory.getStackInSlot(0),
                ghostInventory.getStackInSlot(1)
        );
    }

    @Override
    protected boolean allowRepeats() {
        // Allow the same item in both frequency slots (matches Create's Redstone Link behaviour).
        return true;
    }

    // ------------------------------------------------------------------ ghost slot

    /**
     * Slim SlotItemHandler that marks itself as a ghost slot so Create's renderer
     * draws the translucent overlay instead of a real item.
     */
    private static class GhostSlot extends SlotItemHandler {
        public GhostSlot(IItemHandler itemHandler, int index, int x, int y) {
            super(itemHandler, index, x, y);
        }

        @Override
        public boolean isFake() {
            return true;
        }
    }
}