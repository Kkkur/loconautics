/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.client.event.ScreenEvent$Closing
 *  net.neoforged.neoforge.client.event.ScreenEvent$Opening
 */
package com.simibubi.create.compat.ftb;

import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ScreenEvent;

public class FTBIntegration {
    private static boolean buttonStatePreviously;

    public static void init(IEventBus modEventBus, IEventBus forgeEventBus) {
    }

    private static void removeGUIClutterOpen(ScreenEvent.Opening event) {
        if (FTBIntegration.isCreate(event.getCurrentScreen())) {
            return;
        }
        if (!FTBIntegration.isCreate(event.getNewScreen())) {
            return;
        }
    }

    private static void removeGUIClutterClose(ScreenEvent.Closing event) {
        if (!FTBIntegration.isCreate(event.getScreen())) {
            return;
        }
    }

    private static boolean isCreate(Screen screen) {
        return screen instanceof AbstractSimiContainerScreen || screen instanceof AbstractSimiScreen;
    }
}
