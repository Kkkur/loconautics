package com.lycoris.loconautics;

import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.lycoris.loconautics.server.tick.PhysicsTrainTickHandler;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Main mod entry point.
 *
 * Loconautics turns Create trains into physical Sable sub-levels that still follow Create's tracks.
 * This class only wires up registration and lifecycle; actual feature logic lives in the
 * server/, client/, network/ and mixin/ packages.
 */
@Mod(LoconauticsConstants.MOD_ID)
public final class Loconautics {

    public Loconautics(IEventBus modEventBus, ModContainer modContainer) {
        // Deferred registers (blocks/items) — currently empty stubs, see LoconauticsRegistries.
        LoconauticsRegistries.register(modEventBus);

        // Lifecycle
        modEventBus.addListener(this::commonSetup);

        // Config
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LoconauticsConstants.LOGGER.info("Loconautics common setup");
        PhysicsTrainTickHandler.register();

        // All-Sable (Option B) custom trains: drive sub-levels along Create rails with no Create Train.
        // Sable's event platform accumulates listeners, so this coexists with the hybrid driver above.
        com.lycoris.loconautics.allsable.SableTrainDriver.register();

        // NOTE: BlockStressValues.IMPACTS placeholder for the Bearing Axle was removed in Phase 5.
        // Stress is now computed dynamically in BearingAxleBlockEntity.calculateStressApplied()
        // using the train mass set by PhysicsAssemblyOrchestrator after assembly.
    }
}