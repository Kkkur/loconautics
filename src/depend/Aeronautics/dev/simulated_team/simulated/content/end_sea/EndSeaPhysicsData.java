/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  foundry.veil.api.network.VeilPacketManager$PacketSink
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.end_sea;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.end_sea.EndSeaPhysics;
import dev.simulated_team.simulated.network.packets.end_sea.ClientboundEndSeaPacket;
import foundry.veil.api.network.VeilPacketManager;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EndSeaPhysicsData {
    private static final HashMap<ResourceKey<Level>, EndSeaPhysics> END_SEA_PHYSICS_DATA = new HashMap();

    @Nullable
    public static EndSeaPhysics of(Level level) {
        return END_SEA_PHYSICS_DATA.get(level.dimension());
    }

    public static void physicsTick(double substepTimeStep, ServerLevel level) {
        EndSeaPhysics physics = EndSeaPhysicsData.of((Level)level);
        if (physics != null) {
            physics.physicsTick(substepTimeStep, level);
        }
    }

    public static void addKeyWithPriority(ResourceKey<Level> dimension, EndSeaPhysics newPhysics) {
        EndSeaPhysics existing = END_SEA_PHYSICS_DATA.get(dimension);
        if (existing != null) {
            if (existing.priority().isEmpty()) {
                END_SEA_PHYSICS_DATA.put(dimension, newPhysics);
            } else if (!newPhysics.priority().isEmpty() && newPhysics.priority().get() > existing.priority().get()) {
                END_SEA_PHYSICS_DATA.put(dimension, newPhysics);
            }
        } else {
            END_SEA_PHYSICS_DATA.put(dimension, newPhysics);
        }
    }

    public static void syncDataPacket(VeilPacketManager.PacketSink sink) {
        sink.sendPacket(new CustomPacketPayload[]{new ClientboundEndSeaPacket(END_SEA_PHYSICS_DATA.entrySet().stream().map(Map.Entry::getValue).toList())});
    }

    public static void handleDataPacket(ClientboundEndSeaPacket packet) {
        END_SEA_PHYSICS_DATA.clear();
        for (EndSeaPhysics physics : packet.physics()) {
            EndSeaPhysicsData.addKeyWithPriority((ResourceKey<Level>)ResourceKey.create((ResourceKey)Registries.DIMENSION, (ResourceLocation)physics.dimension()), physics);
        }
    }

    public static class ReloadListener
    extends SimpleJsonResourceReloadListener {
        private static final Gson GSON = new Gson();
        public static final ReloadListener INSTANCE = new ReloadListener();
        public static final String NAME = "end_sea";
        public static final ResourceLocation ID = Simulated.path("end_sea");

        public ReloadListener() {
            super(GSON, NAME);
        }

        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
            END_SEA_PHYSICS_DATA.clear();
            for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
                try {
                    DataResult dataResult = EndSeaPhysics.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)entry.getValue());
                    if (dataResult.isError()) {
                        Simulated.LOGGER.error(String.valueOf(dataResult.error().get()));
                    }
                    EndSeaPhysics physics = (EndSeaPhysics)dataResult.getOrThrow();
                    ResourceKey dimension = ResourceKey.create((ResourceKey)Registries.DIMENSION, (ResourceLocation)physics.dimension());
                    EndSeaPhysicsData.addKeyWithPriority((ResourceKey<Level>)dimension, physics);
                }
                catch (Exception e) {
                    Simulated.LOGGER.error("Error while parsing EndSeaPhysics \"{}\" : {}", (Object)entry.getKey(), (Object)e.getMessage());
                }
            }
        }

        public String getName() {
            return NAME;
        }
    }
}
