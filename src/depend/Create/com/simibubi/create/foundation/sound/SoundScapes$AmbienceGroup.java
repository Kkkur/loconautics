/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.sound;

import com.simibubi.create.foundation.sound.SoundScape;
import com.simibubi.create.foundation.sound.SoundScapes;
import java.util.function.BiFunction;

public static enum SoundScapes.AmbienceGroup {
    KINETIC(SoundScapes::kinetic),
    COG(SoundScapes::cogwheel),
    CRUSHING(SoundScapes::crushing),
    MILLING(SoundScapes::milling);

    private BiFunction<Float, SoundScapes.AmbienceGroup, SoundScape> factory;

    private SoundScapes.AmbienceGroup(BiFunction<Float, SoundScapes.AmbienceGroup, SoundScape> factory) {
        this.factory = factory;
    }

    public SoundScape instantiate(float pitch) {
        return this.factory.apply(Float.valueOf(pitch), this);
    }
}
