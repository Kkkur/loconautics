package com.lycoris.loconautics;

import com.lycoris.loconautics.content.steelcable.SteelCableTracker;
import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.lycoris.loconautics.server.tick.PhysicsTrainTickHandler;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/**
 * Main mod entry point.
 */
@Mod(LoconauticsConstants.MOD_ID)
public final class Loconautics {

    public Loconautics(IEventBus modEventBus, ModContainer modContainer) {
        LoconauticsRegistries.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Game bus — runtime events
        NeoForge.EVENT_BUS.addListener(this::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(this::onServerStopped);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LoconauticsConstants.LOGGER.info("Loconautics common setup");
        PhysicsTrainTickHandler.register();
        com.lycoris.loconautics.allsable.SableTrainDriver.register();
    }

    /**
     * When a player logs in, load the SavedData (which also populates the static set)
     * and send all tracked steel cable UUIDs to them.
     */
    private void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ServerLevel overworld = player.getServer().overworld();
        // Ensures the SavedData is loaded and static set is populated
        SteelCableTracker.getOrCreate(overworld);
        // Send the full set to this player
        SteelCableTracker.syncToPlayer(player);
    }

    /**
     * When the server stops, drop the cached SavedData instance so it gets
     * reloaded fresh on the next start (important for integrated/LAN servers).
     */
    private void onServerStopped(ServerStoppedEvent event) {
        SteelCableTracker.invalidateServer();
    }
}