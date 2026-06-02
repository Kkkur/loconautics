/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllSoundEvents$SoundEntry
 *  dev.simulated_team.simulated.api.sound.SoundEventRegistry
 *  net.minecraft.resources.ResourceLocation
 */
package dev.ryanhcode.offroad.index;

import com.simibubi.create.AllSoundEvents;
import dev.simulated_team.simulated.api.sound.SoundEventRegistry;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class OffroadSoundEvents {
    public static final SoundEventRegistry REGISTRY = new SoundEventRegistry("offroad");
    public static final Map<ResourceLocation, AllSoundEvents.SoundEntry> ALL = new HashMap<ResourceLocation, AllSoundEvents.SoundEntry>();

    public static void init() {
    }
}
