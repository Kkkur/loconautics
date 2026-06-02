/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.stress.BlockStressValues
 *  com.simibubi.create.infrastructure.config.CStress
 *  net.createmod.catnip.config.ConfigBase
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.config.IConfigSpec
 *  net.neoforged.fml.config.ModConfig$Type
 *  net.neoforged.neoforge.common.ModConfigSpec
 *  net.neoforged.neoforge.common.ModConfigSpec$Builder
 *  org.apache.commons.lang3.tuple.Pair
 */
package dev.eriksonn.aeronautics.neoforge.service;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.infrastructure.config.CStress;
import dev.eriksonn.aeronautics.config.AeroConfig;
import dev.eriksonn.aeronautics.config.client.AeroClient;
import dev.eriksonn.aeronautics.config.server.AeroServer;
import dev.eriksonn.aeronautics.config.server.AeroStress;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import net.createmod.catnip.config.ConfigBase;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class NeoForgeAeroConfigService
implements AeroConfig {
    public static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<ModConfig.Type, ConfigBase>(ModConfig.Type.class);
    private static AeroServer server;
    private static AeroClient client;

    @Override
    public AeroServer getServerConfig() {
        return server;
    }

    @Override
    public AeroClient getClientConfig() {
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
        server = NeoForgeAeroConfigService.register(AeroServer::new, ModConfig.Type.SERVER);
        client = NeoForgeAeroConfigService.register(AeroClient::new, ModConfig.Type.CLIENT);
        for (Map.Entry<ModConfig.Type, ConfigBase> typeConfigBaseEntry : CONFIGS.entrySet()) {
            container.registerConfig(typeConfigBaseEntry.getKey(), (IConfigSpec)typeConfigBaseEntry.getValue().specification);
        }
        AeroStress stress = NeoForgeAeroConfigService.server.kinetics.stressValues;
        BlockStressValues.IMPACTS.registerProvider(arg_0 -> ((CStress)stress).getImpact(arg_0));
        BlockStressValues.CAPACITIES.registerProvider(arg_0 -> ((CStress)stress).getCapacity(arg_0));
    }
}
