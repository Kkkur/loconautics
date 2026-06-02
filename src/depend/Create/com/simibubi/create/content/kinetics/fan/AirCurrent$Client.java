/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 */
package com.simibubi.create.content.kinetics.fan;

import com.simibubi.create.content.kinetics.fan.AirCurrentSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public static class AirCurrent.Client {
    private static boolean isClientPlayerInAirCurrent;
    private static AirCurrentSound flyingSound;

    private static void enableClientPlayerSound(Entity e, float maxVolume) {
        if (e != Minecraft.getInstance().getCameraEntity()) {
            return;
        }
        isClientPlayerInAirCurrent = true;
        float pitch = (float)Mth.clamp((double)(e.getDeltaMovement().length() * 0.5), (double)0.5, (double)2.0);
        if (flyingSound == null || flyingSound.isStopped()) {
            flyingSound = new AirCurrentSound(SoundEvents.ELYTRA_FLYING, pitch);
            Minecraft.getInstance().getSoundManager().play((SoundInstance)flyingSound);
        }
        flyingSound.setPitch(pitch);
        flyingSound.fadeIn(maxVolume);
    }

    public static void tickClientPlayerSounds() {
        if (!isClientPlayerInAirCurrent && flyingSound != null) {
            if (flyingSound.isFaded()) {
                flyingSound.stopSound();
            } else {
                flyingSound.fadeOut();
            }
        }
        isClientPlayerInAirCurrent = false;
    }
}
