/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.level.Level
 */
package dev.simulated_team.simulated.content.end_sea;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.end_sea.EndSeaPhysics;
import dev.simulated_team.simulated.content.end_sea.EndSeaPhysicsData;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;

public static class EndSeaPhysicsData.ReloadListener
extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    public static final EndSeaPhysicsData.ReloadListener INSTANCE = new EndSeaPhysicsData.ReloadListener();
    public static final String NAME = "end_sea";
    public static final ResourceLocation ID = Simulated.path("end_sea");

    public EndSeaPhysicsData.ReloadListener() {
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
