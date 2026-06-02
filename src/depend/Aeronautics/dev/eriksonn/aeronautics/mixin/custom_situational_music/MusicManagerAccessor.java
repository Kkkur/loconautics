/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.sounds.MusicManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.eriksonn.aeronautics.mixin.custom_situational_music;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={MusicManager.class})
public interface MusicManagerAccessor {
    @Accessor
    public int getNextSongDelay();

    @Accessor
    public void setNextSongDelay(int var1);

    @Accessor
    public SoundInstance getCurrentMusic();
}
