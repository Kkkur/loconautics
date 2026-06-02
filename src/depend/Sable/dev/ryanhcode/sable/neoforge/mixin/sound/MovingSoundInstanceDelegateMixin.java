/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.sounds.Sound
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.sounds.AudioStream
 *  net.minecraft.client.sounds.SoundBufferLibrary
 *  org.jetbrains.annotations.NotNull
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.neoforge.mixin.sound;

import dev.ryanhcode.sable.sound.MovingSoundInstanceDelegate;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={MovingSoundInstanceDelegate.class})
public abstract class MovingSoundInstanceDelegateMixin
implements SoundInstance {
    @Shadow
    public SoundInstance instance;

    @NotNull
    public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
        return this.instance.getStream(soundBuffers, sound, looping);
    }
}
