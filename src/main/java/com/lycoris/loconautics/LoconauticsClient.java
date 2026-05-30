package com.lycoris.loconautics;

import com.lycoris.loconautics.core.LoconauticsConstants;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Client-only entry point. Will own client registration (screen mixins setup, renderers,
 * client packet handlers) as those phases land. For now it just registers the config screen.
 */
@Mod(value = LoconauticsConstants.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = LoconauticsConstants.MOD_ID, value = Dist.CLIENT)
public final class LoconauticsClient {

    public LoconauticsClient(ModContainer container) {
        // Expose this mod's config in the Mods screen.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        LoconauticsConstants.LOGGER.info("Loconautics client setup");
    }
}
