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
 * Mirrors LinkedTypewriterMenuImpl exactly:
 *   addPlayerSlots(38, 59) → in our standalone screen this becomes (27, 90)
 *   Ghost slots at (94, 32) and (112, 32)
 *
 * Coordinate derivation (from Aeronautics source):
 *   Their panel renders at getCenterWidth()=leftPos+11, getCenterHeight()=topPos-31.
 *   Their addPlayerSlots(38, 59) and renderPlayerInventory at leftPos+30, topPos+41.
 *   Our panel renders at (leftPos+0, topPos+0) — our window IS the panel (214×80).
 *   Our renderPlayerInventory at (leftPos+19, topPos+72).
 *   Our player slots must offset identically: slotX=invX+8=27, slotY=invY+18=90.
 *   Ghost slots: their absolute (105, 1) relative to their panel (leftPos+11, topPos-31)
 *     → panel-relative (94, 32); same for slot 1 at (112, 32).
 *
 * Slot indices:
 *   0–35  : player inventory (PlayerSlot, gated by slotsActive)
 *   36    : frequency first  (ghost)
 *   37    : frequency second (ghost)
 */
public class AnalogControllerMenu extends GhostItemMenu<AnalogControllerBlockEntity> {

    /** Set true by the screen on open to enable player slot interaction. */
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
        ItemStackHandler handler = new ItemStackHandler(4);
        if (contentHolder != null) {
            handler.setStackInSlot(0, contentHolder.getFrequencyFirst().copy());
            handler.setStackInSlot(1, contentHolder.getFrequencySecond().copy());
            handler.setStackInSlot(2, contentHolder.getFrequencyBackFirst().copy());
            handler.setStackInSlot(3, contentHolder.getFrequencyBackSecond().copy());
        }
        return handler;
    }

    /**
     * Slot indices:
     *   0–35  : player inventory (PlayerSlot, gated by slotsActive)
     *   36    : forward frequency first  (ghost, red slot  — forward row)
     *   37    : forward frequency second (ghost, blue slot — forward row)
     *   38    : backward frequency first  (ghost, red slot  — backward row)
     *   39    : backward frequency second (ghost, blue slot — backward row)
     *
     * Ghost slot positions are panel-relative pixel coords from texture analysis:
     *   Forward row:  red x=94 y=32, blue x=112 y=32
     *   Backward row: red x=94 y=61, blue x=112 y=61
     *
     * Player inventory sits below the panel (panel is 95px tall).
     * INV_Y=100 → addPlayerSlots base: x=19+8=27, y=100+18=118.
     */
    @Override
    protected void addSlots() {
        addPlayerSlots(27, 118);

        // Forward frequency slots (top row)
        addSlot(new GhostSlot((IItemHandler) ghostInventory, 0, 94, 32));
        addSlot(new GhostSlot((IItemHandler) ghostInventory, 1, 112, 32));

        // Backward frequency slots (bottom row)
        addSlot(new GhostSlot((IItemHandler) ghostInventory, 2, 94, 61));
        addSlot(new GhostSlot((IItemHandler) ghostInventory, 3, 112, 61));
    }

    /**
     * Mirrors LinkedTypewriterMenuCommon.addPlayerSlots exactly —
     * hotbar first (y+58), then main inventory rows (y, y+18, y+36), gated by slotsActive.
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
        be.setBackwardFrequency(
                ghostInventory.getStackInSlot(2),
                ghostInventory.getStackInSlot(3)
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

        @Override
        public boolean isActive() {
            return true;
        }
    }
}