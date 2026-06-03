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
        // Pin each physics-train sub-level's logicalPose to the carriage pose inside the physics tick,
        // so collision/raytrace (which read logicalPose) coincide with the visible body.
        PhysicsTrainTickHandler.register();
    }
}
