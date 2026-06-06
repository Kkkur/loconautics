package com.lycoris.loconautics;

import com.lycoris.loconautics.client.LoconauticsPartialModels;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerClientHandler;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerHUD;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerRenderer;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerScreen;
import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

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
        LoconauticsPartialModels.init();
    }

    /** Register the Analog Controller block entity renderer. */
    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                LoconauticsRegistries.ANALOG_CONTROLLER_BE.get(),
                AnalogControllerRenderer::new
        );
    }

    /** Register the Analog Controller screen against its menu type. */
    @SubscribeEvent
    static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(LoconauticsRegistries.ANALOG_CONTROLLER_MENU.get(),
                AnalogControllerScreen::new);
    }

    /** Register the Analog Controller HUD overlay. */
    @SubscribeEvent
    static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.EXPERIENCE_BAR,
                LoconauticsConstants.id("analog_controller_hud"),
                AnalogControllerHUD.OVERLAY);
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

    @SubscribeEvent
    static void onMouseScroll(net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent event) {
        if (!AnalogControllerClientHandler.isControlling()) return;
        if (net.minecraft.client.Minecraft.getInstance().screen != null) return;
        int delta = event.getScrollDeltaY() > 0 ? 1 : -1;
        net.createmod.catnip.platform.CatnipServices.NETWORK.sendToServer(
                new com.lycoris.loconautics.network.packets.AnalogControllerScrollPacket(
                        delta, AnalogControllerClientHandler.getMountedPos()));
        event.setCanceled(true);
    }
}