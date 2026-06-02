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
package dev.simulated_team.simulated.client.sections;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.simulated_team.simulated.client.sections.SimulatedSection;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public static class SimulatedSectionManager.ReloadListener
extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();

    public SimulatedSectionManager.ReloadListener() {
        super(GSON, "simulated_sections");
    }

    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        SECTIONS.clear();
        BY_SECTION.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            DataResult result = SimulatedSection.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)entry.getValue());
            if (!result.isSuccess()) continue;
            SimulatedSection tab = (SimulatedSection)result.getOrThrow();
            SECTIONS.put(entry.getKey(), tab);
            BY_SECTION.put(tab, entry.getKey());
        }
        sortedSections = SECTIONS.values().stream().sorted().toList();
    }
}
