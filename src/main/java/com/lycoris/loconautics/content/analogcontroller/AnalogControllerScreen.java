package com.lycoris.loconautics.content.analogcontroller;

import com.lycoris.loconautics.foundation.screen.BindFrequencyScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Screen for the Analog Controller frequency-binding GUI.
 *
 * All rendering is handled by {@link BindFrequencyScreen}; this class exists only so
 * the screen can be registered against {@link AnalogControllerMenu} specifically.
 *
 * Row labels ("Forward" / "Backward") are read from {@link AnalogControllerMenu#firstRowLabel}
 * and {@link AnalogControllerMenu#secondRowLabel}, which are populated from translation keys
 * by the server-side constructor and carried to the client via the extra-data buffer.
 */
public class AnalogControllerScreen
        extends BindFrequencyScreen<AnalogControllerBlockEntity, AnalogControllerMenu> {

    public AnalogControllerScreen(AnalogControllerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }
}