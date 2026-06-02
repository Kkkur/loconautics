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
package dev.ryanhcode.sable.physics.config.dimension_physics;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysics;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;

public static class DimensionPhysicsData.ReloadListener
extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    public static final DimensionPhysicsData.ReloadListener INSTANCE = new DimensionPhysicsData.ReloadListener();
    public static final String NAME = "dimension_physics";
    public static final ResourceLocation ID = Sable.sablePath("dimension_physics");

    public DimensionPhysicsData.ReloadListener() {
        super(GSON, NAME);
    }

    public static void addKeyWithPriority(Map<ResourceKey<Level>, DimensionPhysics> data, ResourceKey<Level> key, DimensionPhysics newProperties) {
        DimensionPhysics existing = data.get(key);
        if (existing != null) {
            if (newProperties.priority() > existing.priority()) {
                data.put(key, newProperties);
            }
        } else {
            data.put(key, newProperties);
        }
    }

    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        DIMENSION_PHYSICS_DATA.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            try {
                DataResult dataResult = DimensionPhysics.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)entry.getValue());
                if (dataResult.error().isPresent()) {
                    Sable.LOGGER.error(String.valueOf(dataResult.error().get()));
                }
                DimensionPhysics dimensionPhysics = (DimensionPhysics)dataResult.getOrThrow();
                ResourceKey dimension = ResourceKey.create((ResourceKey)Registries.DIMENSION, (ResourceLocation)dimensionPhysics.dimension());
                DimensionPhysicsData.ReloadListener.addKeyWithPriority(DIMENSION_PHYSICS_DATA, (ResourceKey<Level>)dimension, dimensionPhysics);
            }
            catch (Exception e) {
                Sable.LOGGER.error("Error while loading dimension data \"{}\" : {} ", (Object)entry.getKey(), (Object)e.getMessage());
            }
        }
    }
}
