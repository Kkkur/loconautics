/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.neoforged.neoforge.registries.DeferredHolder
 */
package com.simibubi.create;

import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

private record AllSoundEvents.WrappedSoundEntry.CompiledSoundEvent(DeferredHolder<SoundEvent, SoundEvent> event, float volume, float pitch) {
}
