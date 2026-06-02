/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.event.ModifyDefaultComponentsEvent
 */
package dev.ryanhcode.offroad.neoforge;

import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.data.OffroadTags;
import dev.ryanhcode.offroad.events.OffroadCommonEvents;
import dev.ryanhcode.offroad.neoforge.data.OffroadDatagen;
import dev.ryanhcode.offroad.neoforge.service.NeoForgeOffroadConfigService;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

@Mod(value="offroad")
public class OffroadNeoForge {
    public OffroadNeoForge(IEventBus modBus, ModContainer modContainer) {
        this.modBusRegistry(modBus);
        this.listenCommonEvents(NeoForge.EVENT_BUS);
        Offroad.init();
        NeoForgeOffroadConfigService.register(modContainer);
    }

    private void listenCommonEvents(IEventBus eventBus) {
    }

    private void modBusRegistry(IEventBus modBus) {
        modBus.register(NeoForgeOffroadConfigService.class);
        modBus.addListener(OffroadNeoForge::init);
        modBus.addListener(EventPriority.HIGHEST, OffroadDatagen::gatherDataHighPriority);
        modBus.addListener(EventPriority.LOWEST, OffroadDatagen::gatherData);
        modBus.addListener(OffroadDatagen::registerEvent);
        modBus.addListener(event -> OffroadCommonEvents.modifyDefaultComponents((arg_0, arg_1) -> ((ModifyDefaultComponentsEvent)event).modify(arg_0, arg_1)));
        NeoForge.EVENT_BUS.addListener(event -> OffroadCommonEvents.tickLevelEvent(event.getLevel()));
        modBus.addListener(event -> {
            if (event.getMods().contains("offroad")) {
                OffroadTags.addGenerators();
            }
        });
        Offroad.getRegistrate().registerEventListeners(modBus);
    }

    private static void init(FMLCommonSetupEvent event) {
    }
}
