/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ui.BaseConfigScreen
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.IExtensionPoint
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.neoforge.client.gui.IConfigScreenFactory
 *  net.neoforged.neoforge.common.NeoForge
 */
package dev.eriksonn.aeronautics.neoforge;

import dev.eriksonn.aeronautics.AeronauticsClient;
import dev.eriksonn.aeronautics.index.client.AeroRenderTypes;
import dev.eriksonn.aeronautics.neoforge.events.AeroNeoForgeClientEvents;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value="aeronautics", dist={Dist.CLIENT})
public class AeronauticsNeoForgeClient {
    public AeronauticsNeoForgeClient(IEventBus modBus, ModContainer container) {
        NeoForge.EVENT_BUS.register(AeroNeoForgeClientEvents.class);
        modBus.register(AeroNeoForgeClientEvents.ModBusEvents.class);
        container.registerExtensionPoint(IConfigScreenFactory.class, (IExtensionPoint)((IConfigScreenFactory)(c, l) -> new BaseConfigScreen(l, "aeronautics")));
        modBus.addListener(event -> event.registerBlockLayer(AeroRenderTypes.levitite()));
        AeronauticsClient.init();
    }
}
