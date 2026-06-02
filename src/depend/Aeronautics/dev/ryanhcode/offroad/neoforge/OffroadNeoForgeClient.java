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
 */
package dev.ryanhcode.offroad.neoforge;

import dev.ryanhcode.offroad.OffroadClient;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value="offroad", dist={Dist.CLIENT})
public class OffroadNeoForgeClient {
    public OffroadNeoForgeClient(IEventBus modBus, ModContainer container) {
        this.listenClientEvents(modBus);
        container.registerExtensionPoint(IConfigScreenFactory.class, (IExtensionPoint)((IConfigScreenFactory)(c, l) -> new BaseConfigScreen(l, "offroad")));
        OffroadClient.init();
    }

    private void listenClientEvents(IEventBus modBus) {
    }
}
