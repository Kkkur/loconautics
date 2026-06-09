package com.lycoris.loconautics.content.analogcontroller;

import com.lycoris.loconautics.foundation.menu.BindFrequencyMenu;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * Frequency-binding menu for the Analog Controller.
 *
 * Extends {@link BindFrequencyMenu} with the two labels:
 *   Row 1 → {@code "Forward"}  (translatable)
 *   Row 2 → {@code "Backward"} (translatable)
 *
 * The labels are written into the extra-data buffer by
 * {@link AnalogControllerBlockEntity#sendToMenu} so the client constructor can
 * decode them before passing the buf to the parent.
 *
 * Slot indices (inherited layout):
 *   0–35   player inventory
 *   36–37  forward frequency  (ghost)
 *   38–39  backward frequency (ghost)
 */
public class AnalogControllerMenu extends BindFrequencyMenu<AnalogControllerBlockEntity> {

    // ------------------------------------------------------------------ label keys

    public static final String FORWARD_KEY  = "gui.loconautics.bind_frequency.forward";
    public static final String BACKWARD_KEY = "gui.loconautics.bind_frequency.backward";

    // ------------------------------------------------------------------ constructors

    /** Client-side constructor — decodes labels from the buf, then delegates. */
    public AnalogControllerMenu(MenuType<?> type, int id, Inventory inv,
                                RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData,
                ComponentSerialization.TRUSTED_STREAM_CODEC.decode(extraData),
                ComponentSerialization.TRUSTED_STREAM_CODEC.decode(extraData));
    }

    /** Server-side constructor — labels are the default Forward / Backward translations. */
    public AnalogControllerMenu(MenuType<?> type, int id, Inventory inv,
                                AnalogControllerBlockEntity be) {
        super(type, id, inv, be,
                Component.translatable(FORWARD_KEY),
                Component.translatable(BACKWARD_KEY));
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
}