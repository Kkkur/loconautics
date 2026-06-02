/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.index;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.worldgen.AirshipReadyPreset;
import dev.simulated_team.simulated.content.worldgen.EndSeaPreset;
import dev.simulated_team.simulated.content.worldgen.SimulatedWorldPreset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SimWorldPresets {
    public static final Map<ResourceLocation, SimulatedWorldPreset> PRESETS = new HashMap<ResourceLocation, SimulatedWorldPreset>();
    public static final SimulatedWorldPreset AIRSHIP_READY = SimWorldPresets.create(AirshipReadyPreset::new, "airship_ready", (Component)Component.translatable((String)"generator.simulated.airship_ready.info"));
    public static final SimulatedWorldPreset END_SEA = SimWorldPresets.create(EndSeaPreset::new, "end_sea", null);

    private static <T extends SimulatedWorldPreset> T create(BiFunction<ResourceLocation, Component, T> constructor, String name, @Nullable Component description) {
        ResourceLocation id = Simulated.path(name);
        SimulatedWorldPreset preset = (SimulatedWorldPreset)constructor.apply(id, description);
        PRESETS.put(id, preset);
        return (T)preset;
    }
}
