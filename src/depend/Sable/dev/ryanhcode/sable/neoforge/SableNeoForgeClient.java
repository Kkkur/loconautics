/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.config.IConfigSpec
 *  net.neoforged.fml.config.ModConfig$Type
 *  net.neoforged.neoforge.common.NeoForge
 */
package dev.ryanhcode.sable.neoforge;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.SableClient;
import dev.ryanhcode.sable.SableClientConfig;
import dev.ryanhcode.sable.neoforge.compatibility.flywheel.FlywheelCompatNeoForge;
import dev.ryanhcode.sable.physics.config.FloatingBlockMaterialDataHandler;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value="sable", dist={Dist.CLIENT})
public final class SableNeoForgeClient {
    public SableNeoForgeClient(ModContainer modContainer, IEventBus modBus) {
        IEventBus neoBus = NeoForge.EVENT_BUS;
        SableClient.init();
        modContainer.registerConfig(ModConfig.Type.CLIENT, (IConfigSpec)SableClientConfig.SPEC);
        modBus.addListener(event -> SableClientConfig.onUpdate(false));
        modBus.addListener(event -> SableClientConfig.onUpdate(true));
        neoBus.addListener(event -> {
            if (event.getPlayer() != null) {
                FloatingBlockMaterialDataHandler.clearMaterials();
            }
        });
        modBus.addListener(event -> event.registerReloadListener((arg, arg2, arg3, arg4, executor, executor2) -> SubLevelRenderDispatcher.get().reload(arg, arg2, arg3, arg4, executor, executor2)));
        if (FlywheelCompatNeoForge.FLYWHEEL_LOADED) {
            Sable.LOGGER.warn("NOTE: Sable is loaded with Flywheel. Sable contains extensive shader overrides and a full light-storage replacement. Expect this to cause compatibility issues. If issues arise, please report them to the Sable issue tracker ({}) instead of the Flywheel issue tracker.", (Object)"https://github.com/ryanhcode/sable/issues");
        }
    }
}
