package com.lycoris.loconautics;

import com.lycoris.loconautics.allsable.SableTrainClientRegistry;
import com.lycoris.loconautics.allsable.SableTrainRelocator;
import com.lycoris.loconautics.client.LoconauticsPartialModels;
import com.lycoris.loconautics.client.LoconauticsSpriteShifts;
import com.lycoris.loconautics.client.ponder.LoconauticsPonderPlugin;
import com.lycoris.loconautics.content.steelcable.SteelCableTracker;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerClientHandler;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerHUD;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerRenderer;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerScreen;
import com.lycoris.loconautics.content.transmission.TransmissionRenderer;
import com.lycoris.loconautics.content.transmission.TransmissionScreen;
import com.lycoris.loconautics.content.transmission.TransmissionVisual;
import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.network.packets.AnalogControllerScrollPacket;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.lycoris.loconautics.content.bearingaxle.BearingAxleRenderer;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipModifier;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

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
        NeoForge.EVENT_BUS.addListener(this::onClientDisconnect);
        NeoForge.EVENT_BUS.addListener(this::onUseItem);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        LoconauticsConstants.LOGGER.info("Loconautics client setup");
        LoconauticsPartialModels.init();
        LoconauticsSpriteShifts.init();

        // Steel Cable tooltip (drives item.loconautics.steel_cable.tooltip.* lang keys)
        ItemDescription.useKey(LoconauticsRegistries.STEEL_CABLE.get(), "item.loconautics.steel_cable");
        TooltipModifier.REGISTRY.register(LoconauticsRegistries.STEEL_CABLE.get(),
                new ItemDescription.Modifier(LoconauticsRegistries.STEEL_CABLE.get(),
                        net.createmod.catnip.lang.FontHelper.Palette.STANDARD_CREATE));

        // Steel Cable ponder scenes (reuses Simulated's rope scenes)
        PonderIndex.addPlugin(new LoconauticsPonderPlugin());

        // Register Flywheel visual for the Transmission
        SimpleBlockEntityVisualizer.builder(LoconauticsRegistries.TRANSMISSION_BE.get())
                .factory(TransmissionVisual::new)
                .skipVanillaRender(be -> true)
                .apply();
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
        event.registerBlockEntityRenderer(
                LoconauticsRegistries.TRANSMISSION_BE.get(),
                TransmissionRenderer::new
        );
    }

    private void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(LoconauticsRegistries.ANALOG_CONTROLLER_MENU.get(),
                AnalogControllerScreen::new);
        event.register(LoconauticsRegistries.TRANSMISSION_MENU.get(),
                TransmissionScreen::new);
    }

    private void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.EXPERIENCE_BAR,
                LoconauticsConstants.id("analog_controller_hud"),
                AnalogControllerHUD.OVERLAY);
    }

    private void onClientTick(ClientTickEvent.Pre event) {
        AnalogControllerClientHandler.tick();
        SableTrainRelocator.clientTick();
    }

    /** Use-item key: starts/confirms/aborts a Sable train-sub-level wrench relocation (see {@link SableTrainRelocator}). */
    private void onUseItem(InputEvent.InteractionKeyMappingTriggered event) {
        SableTrainRelocator.onInteract(event);
    }

    private void onKeyInput(InputEvent.Key event) {
        if (!AnalogControllerClientHandler.isControlling()) return;
        // ESC (256) key press -- close any open screen and dismount.
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

    private void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        SteelCableTracker.clearClient();
        SableTrainClientRegistry.clear();
    }
}