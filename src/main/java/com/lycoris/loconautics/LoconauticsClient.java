package com.lycoris.loconautics;

import com.lycoris.loconautics.client.LoconauticsPartialModels;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerClientHandler;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerHUD;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerRenderer;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerScreen;
import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.network.packets.AnalogControllerScrollPacket;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.lycoris.loconautics.content.bearingaxle.BearingAxleRenderer;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Client-only entry point.
 *
 * Uses the constructor-injection pattern (IEventBus modBus + NeoForge.EVENT_BUS) instead
 * of @EventBusSubscriber, which was deprecated for removal in NeoForge 21.1.x.
 *
 * Mod bus  (modBus)          -- lifecycle events: setup, renderer registration, screen registration
 * Game bus (NeoForge.EVENT_BUS) -- runtime events: client tick, key input, mouse scroll
 */
@Mod(value = LoconauticsConstants.MOD_ID, dist = Dist.CLIENT)
public final class LoconauticsClient {

    public LoconauticsClient(ModContainer container, IEventBus modBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // ---- Mod bus listeners ----
        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::onRegisterRenderers);
        modBus.addListener(this::onRegisterScreens);
        modBus.addListener(this::onRegisterGuiLayers);

        // ---- Game bus listeners ----
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
        NeoForge.EVENT_BUS.addListener(this::onKeyInput);
        NeoForge.EVENT_BUS.addListener(this::onMouseScroll);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        LoconauticsConstants.LOGGER.info("Loconautics client setup");
        LoconauticsPartialModels.init();
    }

    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                LoconauticsRegistries.ANALOG_CONTROLLER_BE.get(),
                AnalogControllerRenderer::new
        );
        event.registerBlockEntityRenderer(
                LoconauticsRegistries.BEARING_AXLE_BE.get(),
                BearingAxleRenderer::new
        );
    }

    private void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(LoconauticsRegistries.ANALOG_CONTROLLER_MENU.get(),
                AnalogControllerScreen::new);
    }

    private void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.EXPERIENCE_BAR,
                LoconauticsConstants.id("analog_controller_hud"),
                AnalogControllerHUD.OVERLAY);
    }

    private void onClientTick(ClientTickEvent.Pre event) {
        AnalogControllerClientHandler.tick();
    }

    private void onKeyInput(InputEvent.Key event) {
        if (!AnalogControllerClientHandler.isControlling()) return;
        // ESC (256) key press -- close any open screen and dismount.
        // InputEvent.Key is not cancellable, so ESC will still open the pause menu;
        // we immediately close it again and dismount.
        if (event.getKey() == 256 && event.getAction() == 1) {
            AnalogControllerClientHandler.stopControllingClient();
            Minecraft.getInstance().setScreen(null);
        }
    }

    private void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (!AnalogControllerClientHandler.isControlling()) return;
        if (Minecraft.getInstance().screen != null) return;
        int delta = event.getScrollDeltaY() > 0 ? 1 : -1;
        CatnipServices.NETWORK.sendToServer(
                new AnalogControllerScrollPacket(delta, AnalogControllerClientHandler.getMountedPos()));
        event.setCanceled(true);
    }
}