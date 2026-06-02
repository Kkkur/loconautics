/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package dev.simulated_team.simulated.api.sound;

import dev.simulated_team.simulated.api.sound.SoundEventRegistry;
import dev.simulated_team.simulated.api.sound.SoundFile;
import net.minecraft.resources.ResourceLocation;

public class SoundEventRegistry.SoundBuilder {
    private final ResourceLocation name;
    private float volume = 1.0f;
    private float pitch = 1.0f;
    private int weight = 1;
    private boolean stream = false;
    private int attenuationDistance = 16;
    private boolean preload = false;
    private SoundFile.Type type = SoundFile.Type.FILE;

    private SoundEventRegistry.SoundBuilder(SoundEventRegistry this$0, ResourceLocation name) {
        this.name = name;
    }

    private SoundEventRegistry.SoundBuilder(SoundEventRegistry this$0, String name) {
        this(this$0, this$0.path(name));
    }

    public SoundEventRegistry.SoundBuilder setVolume(float volume) {
        this.volume = volume;
        return this;
    }

    public SoundEventRegistry.SoundBuilder setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public SoundEventRegistry.SoundBuilder setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public SoundEventRegistry.SoundBuilder setStream(boolean stream) {
        this.stream = stream;
        return this;
    }

    public SoundEventRegistry.SoundBuilder setAttenuationDistance(int attenuationDistance) {
        this.attenuationDistance = attenuationDistance;
        return this;
    }

    public SoundEventRegistry.SoundBuilder setPreload(boolean preload) {
        this.preload = preload;
        return this;
    }

    public SoundEventRegistry.SoundBuilder setType(SoundFile.Type type) {
        this.type = type;
        return this;
    }

    public SoundFile build() {
        return new SoundFile(this.name, this.volume, this.pitch, this.weight, this.stream, this.attenuationDistance, this.preload, this.type);
    }
}
