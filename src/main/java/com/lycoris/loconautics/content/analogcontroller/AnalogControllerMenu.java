package com.lycoris.loconautics.content.analogcontroller;

import com.lycoris.loconautics.registry.LoconauticsRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Container/menu for the Analog Controller frequency screen.
 *
 * Layout: 2 ghost slots (frequency first, frequency second) + no player inventory
 * displayed (we open this via shift-right-click, player inventory is not needed).
 *
 * The frequency items are stored in the block entity, not in the container's inventory.
 * On close, changes are sent to the server via a packet.
 */
public class AnalogControllerMenu extends AbstractContainerMenu {

    // Slot indices
    public static final int SLOT_FREQ_FIRST = 0;
    public static final int SLOT_FREQ_SECOND = 1;

    // The underlying 2-slot inventory (ghost items — never consumed)
    private final SimpleContainer frequencyInv;

    // Server side: direct reference to the block entity
    private final AnalogControllerBlockEntity blockEntity;

    // Position, used by the screen for the "display block" render
    public final BlockPos blockPos;

    // ------------------------------------------------------------------ server constructor

    /** Called on the server when the player opens the menu. */
    public AnalogControllerMenu(int containerId, Inventory playerInventory,
                                AnalogControllerBlockEntity be) {
        super(LoconauticsRegistries.ANALOG_CONTROLLER_MENU.get(), containerId);
        this.blockEntity = be;
        this.blockPos = be.getBlockPos();

        frequencyInv = new SimpleContainer(2);
        frequencyInv.setItem(0, be.getFrequencyFirst().copy());
        frequencyInv.setItem(1, be.getFrequencySecond().copy());

        addFrequencySlots();
    }

    // ------------------------------------------------------------------ client constructor (network)

    /** Called on the client side when the server opens the screen (via network). */
    public AnalogControllerMenu(int containerId, Inventory playerInventory,
                                FriendlyByteBuf buf) {
        super(LoconauticsRegistries.ANALOG_CONTROLLER_MENU.get(), containerId);
        this.blockEntity = null;
        this.blockPos = buf.readBlockPos();

        frequencyInv = new SimpleContainer(2);
        frequencyInv.setItem(0, ItemStack.OPTIONAL_STREAM_CODEC
                .decode((net.minecraft.network.RegistryFriendlyByteBuf) buf));
        frequencyInv.setItem(1, ItemStack.OPTIONAL_STREAM_CODEC
                .decode((net.minecraft.network.RegistryFriendlyByteBuf) buf));

        addFrequencySlots();
    }

    private void addFrequencySlots() {
        // Two frequency slots — ghost items (don't consume from inventory)
        addSlot(new GhostSlot(frequencyInv, SLOT_FREQ_FIRST, 44, 35));
        addSlot(new GhostSlot(frequencyInv, SLOT_FREQ_SECOND, 80, 35));
    }

    // ------------------------------------------------------------------ slot getters

    public ItemStack getFrequencyFirst() {
        return frequencyInv.getItem(SLOT_FREQ_FIRST);
    }

    public ItemStack getFrequencySecond() {
        return frequencyInv.getItem(SLOT_FREQ_SECOND);
    }

    // ------------------------------------------------------------------ AbstractContainerMenu

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (blockEntity == null) return true; // client side
        return blockEntity.getLevel() != null
                && blockEntity.getLevel().getBlockEntity(blockPos) == blockEntity
                && player.distanceToSqr(blockPos.getX() + 0.5,
                blockPos.getY() + 0.5,
                blockPos.getZ() + 0.5) < 64;
    }

    /**
     * On close (server side), push the frequency items back to the block entity.
     */
    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        if (!player.level().isClientSide && blockEntity != null) {
            blockEntity.setFrequency(
                    frequencyInv.getItem(SLOT_FREQ_FIRST),
                    frequencyInv.getItem(SLOT_FREQ_SECOND)
            );
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        // Ghost slots: return empty, no shift-click transfers
        return ItemStack.EMPTY;
    }

    // ------------------------------------------------------------------ ghost slot

    /**
     * A slot that accepts any item stack but never removes it from the player's inventory —
     * it copies it as a "frequency token" (same behaviour as Create's ghost slots).
     */
    private static class GhostSlot extends Slot {

        public GhostSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPickup(@NotNull Player player) {
            return false; // can't take the ghost item out
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return true;
        }

        @Override
        public void set(@NotNull ItemStack stack) {
            // Copy one item as the "token"; don't consume stack
            ItemStack copy = stack.copy();
            copy.setCount(1);
            super.set(copy);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}