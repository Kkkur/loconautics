package com.lycoris.loconautics;

import com.lycoris.loconautics.content.analogcontroller.AnalogControllerClientHandler;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerScreen;
import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.registry.LoconauticsRegistries;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * Client-only entry point.
 * Registers:
 *  – The Analog Controller frequency screen.
 *  – The per-tick key-polling for mounted players.
 */
@Mod(value = LoconauticsConstants.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = LoconauticsConstants.MOD_ID, value = Dist.CLIENT,
        bus = EventBusSubscriber.Bus.MOD)
public final class LoconauticsClient {

    public LoconauticsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        LoconauticsConstants.LOGGER.info("Loconautics client setup");
    }

    /** Register the Analog Controller screen against its menu type. */
    @SubscribeEvent
    static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(LoconauticsRegistries.ANALOG_CONTROLLER_MENU.get(),
                AnalogControllerScreen::new);
    }
}

/**
 * Game event bus subscriber (separate class — must use GAME bus for ClientTickEvent).
 */
@EventBusSubscriber(modid = LoconauticsConstants.MOD_ID, value = Dist.CLIENT,
        bus = EventBusSubscriber.Bus.GAME)
final class LoconauticsClientGameEvents {

    private LoconauticsClientGameEvents() {}

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Pre event) {
        AnalogControllerClientHandler.tick();
    }
}