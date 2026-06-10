package com.lycoris.loconautics.content.transmission;

import com.lycoris.loconautics.foundation.screen.BindFrequencyScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Screen for the Transmission frequency-binding GUI.
 *
 * All rendering is handled by {@link BindFrequencyScreen}. Row labels
 * ("Speed" / "Direction") are decoded from the server-written extra-data buffer
 * and stored on {@link TransmissionMenu#firstRowLabel} / {@link TransmissionMenu#secondRowLabel}.
 */
public class TransmissionScreen
        extends BindFrequencyScreen<TransmissionBlockEntity, TransmissionMenu> {

    public TransmissionScreen(TransmissionMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }
}