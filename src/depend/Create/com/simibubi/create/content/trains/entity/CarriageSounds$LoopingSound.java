/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.SubtitleOverlay
 *  net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.client.sounds.WeighedSoundEvents
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.foundation.mixin.accessor.GuiAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

static class CarriageSounds.LoopingSound
extends AbstractTickableSoundInstance {
    private static final SubtitleOverlay OVERLAY = ((GuiAccessor)Minecraft.getInstance().gui).create$getSubtitleOverlay();
    private final boolean repeatSubtitle;
    private final WeighedSoundEvents weighedSoundEvents = this.resolve(Minecraft.getInstance().getSoundManager());
    private byte subtitleTimer = 0;

    protected CarriageSounds.LoopingSound(SoundEvent soundEvent, SoundSource source) {
        this(soundEvent, source, false);
    }

    protected CarriageSounds.LoopingSound(SoundEvent soundEvent, SoundSource source, boolean repeatSubtitle) {
        super(soundEvent, source, SoundInstance.createUnseededRandom());
        this.attenuation = SoundInstance.Attenuation.LINEAR;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0E-4f;
        this.repeatSubtitle = repeatSubtitle;
    }

    public void tick() {
        if (this.repeatSubtitle) {
            this.subtitleTimer = (byte)(this.subtitleTimer + 1);
            if (this.subtitleTimer == 20) {
                OVERLAY.onPlaySound((SoundInstance)this, this.weighedSoundEvents, (float)this.sound.getAttenuationDistance());
                this.subtitleTimer = 0;
            }
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getVolume() {
        return this.volume;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setLocation(Vec3 location) {
        this.x = location.x;
        this.y = location.y;
        this.z = location.z;
    }

    public void stopSound() {
        Minecraft.getInstance().getSoundManager().stop((SoundInstance)this);
    }
}
