package com.lycoris.loconautics.content.transmission;

import com.lycoris.loconautics.foundation.menu.BindFrequencyMenu;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * Frequency-binding menu for the Transmission block.
 *
 * Row labels:
 *   Row 1 (top)    → "Speed"     — analog signal 0–15, controls output RPM
 *   Row 2 (bottom) → "Direction" — binary ON/OFF, reverses output shaft direction
 *
 * Slot indices (inherited from {@link BindFrequencyMenu}):
 *   0–35  player inventory
 *   36–37 speed frequency     (ghost, top row)
 *   38–39 direction frequency (ghost, bottom row)
 */
public class TransmissionMenu extends BindFrequencyMenu<TransmissionBlockEntity> {

    // ------------------------------------------------------------------ label keys

    public static final String SPEED_KEY     = "gui.loconautics.bind_frequency.speed";
    public static final String DIRECTION_KEY = "gui.loconautics.bind_frequency.direction";

    // ------------------------------------------------------------------ constructors

    /** Client-side constructor — decodes labels from the buf, then delegates. */
    public TransmissionMenu(MenuType<?> type, int id, Inventory inv,
                            RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData,
                ComponentSerialization.TRUSTED_STREAM_CODEC.decode(extraData),
                ComponentSerialization.TRUSTED_STREAM_CODEC.decode(extraData));
    }

    /** Server-side constructor. */
    public TransmissionMenu(MenuType<?> type, int id, Inventory inv,
                            TransmissionBlockEntity be) {
        super(type, id, inv, be,
                Component.translatable(SPEED_KEY),
                Component.translatable(DIRECTION_KEY));
    }

    // ------------------------------------------------------------------ GhostItemMenu impl

    @Override
    protected TransmissionBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos());
        if (be instanceof TransmissionBlockEntity tbe) {
            tbe.readClient(extraData.readNbt(), extraData.registryAccess());
            return tbe;
        }
        return null;
    }

    @Override
    protected ItemStackHandler createGhostInventory() {
        ItemStackHandler handler = new ItemStackHandler(4);
        if (contentHolder != null) {
            handler.setStackInSlot(0, contentHolder.getSpeedFreqFirst().copy());
            handler.setStackInSlot(1, contentHolder.getSpeedFreqSecond().copy());
            handler.setStackInSlot(2, contentHolder.getDirFreqFirst().copy());
            handler.setStackInSlot(3, contentHolder.getDirFreqSecond().copy());
        }
        return handler;
    }

    @Override
    protected void saveData(TransmissionBlockEntity be) {
        if (be == null) return;
        be.setSpeedFrequency(
                ghostInventory.getStackInSlot(0),
                ghostInventory.getStackInSlot(1)
        );
        be.setDirectionFrequency(
                ghostInventory.getStackInSlot(2),
                ghostInventory.getStackInSlot(3)
        );
    }
}