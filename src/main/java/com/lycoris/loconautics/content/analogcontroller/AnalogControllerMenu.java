package com.lycoris.loconautics.content.analogcontroller;

import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
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
 * Mirrors LinkedTypewriterMenuCommon exactly:
 *   - addPlayerSlots() adds real-coordinate slots gated by slotsActive
 *   - Two ghost frequency slots follow at indices 36 and 37
 *
 * Slot layout:
 *   Slots 0-35 : player inventory (PlayerSlot, visibility gated by slotsActive)
 *   Slot 36    : frequency first  (ghost)
 *   Slot 37    : frequency second (ghost)
 */
public class AnalogControllerMenu extends GhostItemMenu<AnalogControllerBlockEntity> {

    /** Controls whether player slots respond to interaction. Set true by the screen on open. */
    public boolean slotsActive = false;

    // ------------------------------------------------------------------ constructors

    public AnalogControllerMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public AnalogControllerMenu(MenuType<?> type, int id, Inventory inv, AnalogControllerBlockEntity be) {
        super(type, id, inv, be);
    }

    public static AnalogControllerMenu create(int id, Inventory inv, AnalogControllerBlockEntity be) {
        return new AnalogControllerMenu(LoconauticsRegistries.ANALOG_CONTROLLER_MENU.get(), id, inv, be);
    }

    // ------------------------------------------------------------------ GhostItemMenu impl

    @Override
    protected AnalogControllerBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos());
        if (be instanceof AnalogControllerBlockEntity ace) {
            ace.readClient(extraData.readNbt(), extraData.registryAccess());
            return ace;
        }
        return null;
    }

    @Override
    protected ItemStackHandler createGhostInventory() {
        ItemStackHandler handler = new ItemStackHandler(2);
        if (contentHolder != null) {
            handler.setStackInSlot(0, contentHolder.getFrequencyFirst().copy());
            handler.setStackInSlot(1, contentHolder.getFrequencySecond().copy());
        }
        return handler;
    }

    @Override
    protected void addSlots() {
        // Player slots at real coordinates — screen passes the correct origin from renderPlayerInventory.
        // Mirroring EntryModifierScreen: inventory rendered at centerX+19, centerY+72.
        // We use 0,0 here; AbstractSimiContainerScreen offsets by leftPos/topPos automatically.
        addPlayerSlots(0, 0);

        // Ghost frequency slots — panel-relative positions inside the LINKED_TYPEWRITER_BIND panel.
        // The two colored squares in the bind panel sit at roughly x=88,y=33 and x=106,y=33
        // relative to the panel origin (empirically from the 212×89 layout in the screenshot).
        addSlot(new GhostSlot((IItemHandler) ghostInventory, 0, 88, 33));
        addSlot(new GhostSlot((IItemHandler) ghostInventory, 1, 106, 33));
    }

    /**
     * Mirrors LinkedTypewriterMenuCommon.addPlayerSlots exactly —
     * hotbar first, then main inventory rows, with PlayerSlot gating isActive().
     */
    @Override
    protected void addPlayerSlots(int x, int y) {
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            addSlot(new PlayerSlot(playerInventory, hotbarSlot, x + hotbarSlot * 18, y + 58));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new PlayerSlot(playerInventory, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }
    }

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
        return true;
    }

    // ------------------------------------------------------------------ inner slots

    /** Mirrors LinkedTypewriterMenuCommon.PlayerSlot — isActive() gated by slotsActive. */
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
    }
}