/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.stress.BlockStressValues
 *  com.simibubi.create.infrastructure.config.CStress
 *  net.createmod.catnip.config.ConfigBase
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.common.EventBusSubscriber$Bus
 *  net.neoforged.fml.config.IConfigSpec
 *  net.neoforged.fml.config.ModConfig$Type
 *  net.neoforged.fml.event.config.ModConfigEvent$Loading
 *  net.neoforged.fml.event.config.ModConfigEvent$Reloading
 *  net.neoforged.neoforge.common.ModConfigSpec
 *  net.neoforged.neoforge.common.ModConfigSpec$Builder
 *  org.apache.commons.lang3.tuple.Pair
 */
package dev.ryanhcode.offroad.neoforge.service;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.infrastructure.config.CStress;
import dev.ryanhcode.offroad.config.OffroadConfig;
import dev.ryanhcode.offroad.config.client.OffroadClientConfig;
import dev.ryanhcode.offroad.config.server.OffroadServer;
import dev.ryanhcode.offroad.config.server.OffroadStress;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import net.createmod.catnip.config.ConfigBase;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

@EventBusSubscriber(bus=EventBusSubscriber.Bus.MOD)
public class NeoForgeOffroadConfigService
implements OffroadConfig {
    public static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<ModConfig.Type, ConfigBase>(ModConfig.Type.class);
    private static OffroadServer server;
    private static OffroadClientConfig client;

    @Override
    public OffroadServer getServerConfig() {
        return server;
    }

    @Override
    public OffroadClientConfig getClientConfig() {
        return client;
    }

    private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        Pair specPair = new ModConfigSpec.Builder().configure(builder -> {
            ConfigBase config = (ConfigBase)factory.get();
            config.registerAll(builder);
            return config;
        });
        ConfigBase config = (ConfigBase)specPair.getLeft();
        config.specification = (ModConfigSpec)specPair.getRight();
        CONFIGS.put(side, config);
        return (T)config;
    }

    public static void register(ModContainer container) {
        server = NeoForgeOffroadConfigService.register(OffroadServer::new, ModConfig.Type.SERVER);
        client = NeoForgeOffroadConfigService.register(OffroadClientConfig::new, ModConfig.Type.CLIENT);
        for (Map.Entry<ModConfig.Type, ConfigBase> typeConfigBaseEntry : CONFIGS.entrySet()) {
            container.registerConfig(typeConfigBaseEntry.getKey(), (IConfigSpec)typeConfigBaseEntry.getValue().specification);
        }
        OffroadStress stress = NeoForgeOffroadConfigService.server.kinetics.stressValues;
        BlockStressValues.IMPACTS.registerProvider(arg_0 -> ((CStress)stress).getImpact(arg_0));
        BlockStressValues.CAPACITIES.registerProvider(arg_0 -> ((CStress)stress).getCapacity(arg_0));
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        for (ConfigBase config : CONFIGS.values()) {
            if (config.specification != event.getConfig().getSpec()) continue;
            config.onLoad();
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        for (ConfigBase config : CONFIGS.values()) {
            if (config.specification != event.getConfig().getSpec()) continue;
            config.onReload();
        }
    }
}
