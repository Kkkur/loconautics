/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.common.EventBusSubscriber$Bus
 *  net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent
 *  net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
 *  net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
 *  net.neoforged.neoforge.client.gui.VanillaGuiLayers
 */
package dev.simulated_team.simulated.neoforge.events;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterItemBindHandler;
import dev.simulated_team.simulated.index.SimKeys;
import dev.simulated_team.simulated.neoforge.service.SimpleResourceManagerRegistryService;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid="simulated", value={Dist.CLIENT}, bus=EventBusSubscriber.Bus.MOD)
public static class SimNeoForgeClientEvents.ModBusEvents {
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        SimKeys.registerTo(arg_0 -> ((RegisterKeyMappingsEvent)event).register(arg_0));
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, Simulated.path("linked_typewriter_binding"), LinkedTypewriterItemBindHandler.OVERLAY);
    }

    @SubscribeEvent
    public static void addReloadListener(RegisterClientReloadListenersEvent event) {
        for (PreparableReloadListener listener : SimpleResourceManagerRegistryService.LISTENERS) {
            event.registerReloadListener(listener);
        }
    }
}
