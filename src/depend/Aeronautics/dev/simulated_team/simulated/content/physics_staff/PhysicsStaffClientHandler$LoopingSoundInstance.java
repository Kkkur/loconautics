/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 */
package dev.simulated_team.simulated.content.physics_staff;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public static class PhysicsStaffClientHandler.LoopingSoundInstance
extends AbstractTickableSoundInstance {
    private final LocalPlayer player;

    protected PhysicsStaffClientHandler.LoopingSoundInstance(LocalPlayer player, SoundEvent event, RandomSource random) {
        super(event, SoundSource.PLAYERS, random);
        this.player = player;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public double getX() {
        return this.player.position().x();
    }

    public double getY() {
        return this.player.position().y();
    }

    public double getZ() {
        return this.player.position().z();
    }

    public void tick() {
    }
}
