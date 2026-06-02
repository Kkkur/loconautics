/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.stress.BlockStressValues
 *  com.simibubi.create.infrastructure.config.CStress
 *  net.createmod.catnip.config.ConfigBase
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.ModLoadingContext
 *  net.neoforged.fml.config.IConfigSpec
 *  net.neoforged.fml.config.ModConfig$Type
 *  net.neoforged.neoforge.common.ModConfigSpec
 *  net.neoforged.neoforge.common.ModConfigSpec$Builder
 *  org.apache.commons.lang3.tuple.Pair
 */
package dev.simulated_team.simulated.neoforge.service;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.infrastructure.config.CStress;
import dev.simulated_team.simulated.config.client.SimClient;
import dev.simulated_team.simulated.config.server.SimServer;
import dev.simulated_team.simulated.config.server.blocks.SimStress;
import dev.simulated_team.simulated.service.SimConfigService;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import net.createmod.catnip.config.ConfigBase;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class NeoForgeSimConfigService
implements SimConfigService {
    public static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<ModConfig.Type, ConfigBase>(ModConfig.Type.class);
    private static SimServer server;
    private static SimClient client;

    @Override
    public boolean serverLoaded() {
        return server != null && NeoForgeSimConfigService.server.specification != null && NeoForgeSimConfigService.server.specification.isLoaded();
    }

    @Override
    public boolean clientLoaded() {
        return client != null && NeoForgeSimConfigService.client.specification != null && NeoForgeSimConfigService.client.specification.isLoaded();
    }

    @Override
    public SimServer server() {
        return server;
    }

    @Override
    public SimClient client() {
        return client;
    }

    public static ConfigBase byType(ModConfig.Type type) {
        return CONFIGS.get(type);
    }

    public static void registerCommon() {
        server = NeoForgeSimConfigService.register(SimServer::new, ModConfig.Type.SERVER);
        client = NeoForgeSimConfigService.register(SimClient::new, ModConfig.Type.CLIENT);
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
        server = NeoForgeSimConfigService.register(SimServer::new, ModConfig.Type.SERVER);
        client = NeoForgeSimConfigService.register(SimClient::new, ModConfig.Type.CLIENT);
        for (Map.Entry<ModConfig.Type, ConfigBase> typeConfigBaseEntry : CONFIGS.entrySet()) {
            container.registerConfig(typeConfigBaseEntry.getKey(), (IConfigSpec)typeConfigBaseEntry.getValue().specification);
        }
        SimStress stress = SimConfigService.INSTANCE.server().kinetics.stressValues;
        BlockStressValues.IMPACTS.registerProvider(arg_0 -> ((CStress)stress).getImpact(arg_0));
        BlockStressValues.CAPACITIES.registerProvider(arg_0 -> ((CStress)stress).getCapacity(arg_0));
    }
}
