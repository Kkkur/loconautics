/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.ModLoadingContext
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.config.IConfigSpec
 *  net.neoforged.fml.config.ModConfig$Type
 *  net.neoforged.fml.event.config.ModConfigEvent$Loading
 *  net.neoforged.fml.event.config.ModConfigEvent$Reloading
 *  net.neoforged.neoforge.common.ModConfigSpec
 *  net.neoforged.neoforge.common.ModConfigSpec$Builder
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.infrastructure.config;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.infrastructure.config.CClient;
import com.simibubi.create.infrastructure.config.CCommon;
import com.simibubi.create.infrastructure.config.CServer;
import com.simibubi.create.infrastructure.config.CStress;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import net.createmod.catnip.config.ConfigBase;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

@EventBusSubscriber
public class AllConfigs {
    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<ModConfig.Type, ConfigBase>(ModConfig.Type.class);
    private static CClient client;
    private static CCommon common;
    private static CServer server;

    public static CClient client() {
        return client;
    }

    public static CCommon common() {
        return common;
    }

    public static CServer server() {
        return server;
    }

    public static ConfigBase byType(ModConfig.Type type) {
        return CONFIGS.get(type);
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

    public static void register(ModLoadingContext context, ModContainer container) {
        client = AllConfigs.register(CClient::new, ModConfig.Type.CLIENT);
        common = AllConfigs.register(CCommon::new, ModConfig.Type.COMMON);
        server = AllConfigs.register(CServer::new, ModConfig.Type.SERVER);
        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet()) {
            container.registerConfig(pair.getKey(), (IConfigSpec)pair.getValue().specification);
        }
        CStress stress = AllConfigs.server().kinetics.stressValues;
        BlockStressValues.IMPACTS.registerProvider(stress::getImpact);
        BlockStressValues.CAPACITIES.registerProvider(stress::getCapacity);
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
