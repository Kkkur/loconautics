/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 */
package com.simibubi.create;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;

public record AllSoundEvents.ConfiguredSoundEvent(Supplier<SoundEvent> event, float volume, float pitch) {
}
