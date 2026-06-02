/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
 *  net.minecraft.util.profiling.ProfilerFiller
 */
package dev.ryanhcode.sable.physics.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.physics.config.FloatingBlockMaterialDataHandler;
import dev.ryanhcode.sable.physics.floating_block.FloatingBlockMaterial;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public static class FloatingBlockMaterialDataHandler.ReloadListener
extends SimpleJsonResourceReloadListener {
    public static final String NAME = "floating_block_material";
    public static final ResourceLocation ID = Sable.sablePath("floating_block_material");
    private static final Gson GSON = new Gson();
    public static final FloatingBlockMaterialDataHandler.ReloadListener INSTANCE = new FloatingBlockMaterialDataHandler.ReloadListener();

    protected FloatingBlockMaterialDataHandler.ReloadListener() {
        super(GSON, "floating_materials");
    }

    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        allMaterials.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            JsonElement element = entry.getValue();
            try {
                DataResult dataResult = FloatingBlockMaterial.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)element);
                if (dataResult.error().isPresent()) {
                    Sable.LOGGER.error(String.valueOf(dataResult.error().get()));
                    continue;
                }
                ResourceLocation loc = entry.getKey();
                FloatingBlockMaterial floatingBlockMaterial = (FloatingBlockMaterial)dataResult.result().orElseThrow();
                FloatingBlockMaterialDataHandler.addMaterial(loc, floatingBlockMaterial);
            }
            catch (Exception exception) {}
        }
    }
}
