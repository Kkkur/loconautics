/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllSoundEvents$SoundEntry
 *  net.minecraft.sounds.SoundEvent
 *  net.neoforged.neoforge.registries.RegisterEvent$RegisterHelper
 */
package dev.ryanhcode.offroad.neoforge.index;

import com.simibubi.create.AllSoundEvents;
import dev.ryanhcode.offroad.index.OffroadSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class OffroadSoundEventsNeoForge {
    public static void register(RegisterEvent.RegisterHelper<SoundEvent> helper) {
        for (AllSoundEvents.SoundEntry entry : OffroadSoundEvents.ALL.values()) {
            entry.register(helper);
        }
    }
}
