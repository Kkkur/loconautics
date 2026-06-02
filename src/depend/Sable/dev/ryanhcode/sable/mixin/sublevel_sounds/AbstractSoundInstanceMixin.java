/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.sounds.AbstractSoundInstance
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.ryanhcode.sable.mixin.sublevel_sounds;

import dev.ryanhcode.sable.sound.MovingSoundInstanceDelegate;
import dev.ryanhcode.sable.sound.SoundInstanceDelegated;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={AbstractSoundInstance.class})
public class AbstractSoundInstanceMixin
implements SoundInstanceDelegated {
    @Unique
    private MovingSoundInstanceDelegate sable$delegate;

    @Override
    public MovingSoundInstanceDelegate getDelegate() {
        return this.sable$delegate;
    }

    @Override
    public void setDelegate(MovingSoundInstanceDelegate delegate) {
        this.sable$delegate = delegate;
    }
}
