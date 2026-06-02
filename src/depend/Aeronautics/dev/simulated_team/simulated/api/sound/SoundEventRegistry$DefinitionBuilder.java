/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllSoundEvents$SoundEntry
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 */
package dev.simulated_team.simulated.api.sound;

import com.simibubi.create.AllSoundEvents;
import dev.simulated_team.simulated.api.sound.SimSoundEntry;
import dev.simulated_team.simulated.api.sound.SoundDefinition;
import dev.simulated_team.simulated.api.sound.SoundEventRegistry;
import dev.simulated_team.simulated.api.sound.SoundFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEventRegistry.DefinitionBuilder {
    private final String name;
    private String subtitle = null;
    private final List<SoundFile> sounds = new ArrayList<SoundFile>();

    private SoundEventRegistry.DefinitionBuilder(String name) {
        this.name = name;
    }

    public SoundEventRegistry.DefinitionBuilder defaultSubtitle(String key) {
        this.subtitle = key;
        return this;
    }

    public SoundEventRegistry.DefinitionBuilder defaultSubtitle(String subtitle, String key) {
        SoundEventRegistry.this.subtitles.put(key, subtitle);
        return this.defaultSubtitle(key);
    }

    public SoundEventRegistry.DefinitionBuilder subtitle(String subtitle) {
        String id = SoundEventRegistry.this.modId + ".subtitle." + this.name;
        return this.defaultSubtitle(subtitle, id);
    }

    public SoundEventRegistry.DefinitionBuilder addFileVariant(ResourceLocation path, UnaryOperator<SoundEventRegistry.SoundBuilder> operator) {
        SoundEventRegistry soundEventRegistry = SoundEventRegistry.this;
        Objects.requireNonNull(soundEventRegistry);
        SoundEventRegistry.SoundBuilder builder = new SoundEventRegistry.SoundBuilder(soundEventRegistry, path);
        operator.apply(builder);
        this.sounds.add(builder.build());
        return this;
    }

    public SoundEventRegistry.DefinitionBuilder addFileVariant(String path, UnaryOperator<SoundEventRegistry.SoundBuilder> operator) {
        return this.addFileVariant(SoundEventRegistry.this.path(path), operator);
    }

    public SoundEventRegistry.DefinitionBuilder addFileVariant(String path) {
        return this.addFileVariant(path, UnaryOperator.identity());
    }

    public SoundEventRegistry.DefinitionBuilder addFileVariants(String path, int count) {
        for (int i = 0; i < count; ++i) {
            int n = i + 1;
            this.addFileVariant(path + "_" + n);
        }
        return this;
    }

    public SoundEventRegistry.DefinitionBuilder addEventVariant(SoundEvent event, UnaryOperator<SoundEventRegistry.SoundBuilder> operator) {
        SoundEventRegistry soundEventRegistry = SoundEventRegistry.this;
        Objects.requireNonNull(soundEventRegistry);
        SoundEventRegistry.SoundBuilder builder = new SoundEventRegistry.SoundBuilder(soundEventRegistry, event.getLocation()).setType(SoundFile.Type.EVENT);
        operator.apply(builder);
        this.sounds.add(builder.build());
        return this;
    }

    public SoundEventRegistry.DefinitionBuilder addEventVariant(SimSoundEntry entry, UnaryOperator<SoundEventRegistry.SoundBuilder> operator) {
        SoundEventRegistry soundEventRegistry = SoundEventRegistry.this;
        Objects.requireNonNull(soundEventRegistry);
        SoundEventRegistry.SoundBuilder builder = new SoundEventRegistry.SoundBuilder(soundEventRegistry, entry.id()).setType(SoundFile.Type.EVENT);
        operator.apply(builder);
        this.sounds.add(builder.build());
        return this;
    }

    public SoundEventRegistry.DefinitionBuilder addEventVariant(AllSoundEvents.SoundEntry soundEntry, UnaryOperator<SoundEventRegistry.SoundBuilder> operator) {
        SoundEventRegistry soundEventRegistry = SoundEventRegistry.this;
        Objects.requireNonNull(soundEventRegistry);
        SoundEventRegistry.SoundBuilder builder = new SoundEventRegistry.SoundBuilder(soundEventRegistry, soundEntry.getId()).setType(SoundFile.Type.EVENT);
        operator.apply(builder);
        this.sounds.add(builder.build());
        return this;
    }

    public SoundEventRegistry.DefinitionBuilder addEventVariant(SoundEvent event) {
        return this.addEventVariant(event, UnaryOperator.identity());
    }

    public SoundEventRegistry.DefinitionBuilder addEventVariant(SimSoundEntry entry) {
        return this.addEventVariant(entry, UnaryOperator.identity());
    }

    public SoundEventRegistry.DefinitionBuilder addEventVariant(AllSoundEvents.SoundEntry soundEntry) {
        return this.addEventVariant(soundEntry, UnaryOperator.identity());
    }

    public SoundDefinition build() {
        return new SoundDefinition(false, Optional.ofNullable(this.subtitle), this.sounds);
    }
}
