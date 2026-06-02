/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllSoundEvents$SoundEntry
 *  foundry.veil.platform.registry.RegistrationProvider
 *  foundry.veil.platform.registry.RegistryObject
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.data.PackOutput
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 */
package dev.simulated_team.simulated.api.sound;

import com.simibubi.create.AllSoundEvents;
import dev.simulated_team.simulated.api.sound.SimSoundEntry;
import dev.simulated_team.simulated.api.sound.SoundDefinition;
import dev.simulated_team.simulated.api.sound.SoundFile;
import dev.simulated_team.simulated.api.sound.SoundsProvider;
import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundEventRegistry {
    private final Map<String, SoundDefinition> definitions = new LinkedHashMap<String, SoundDefinition>();
    private final Map<String, String> subtitles = new LinkedHashMap<String, String>();
    private final RegistrationProvider<SoundEvent> registry;
    private final String modId;

    public SoundEventRegistry(String modId) {
        this.modId = modId;
        this.registry = RegistrationProvider.get((Registry)BuiltInRegistries.SOUND_EVENT, (String)this.modId);
    }

    public SimSoundEntry create(String name, SoundSource category, UnaryOperator<DefinitionBuilder> operator) {
        ResourceLocation location = this.path(name);
        this.definitions.put(name, ((DefinitionBuilder)operator.apply(new DefinitionBuilder(name))).build());
        RegistryObject registryObject = this.registry.register(name, () -> SoundEvent.createVariableRangeEvent((ResourceLocation)location));
        return new SimSoundEntry(location, (RegistryObject<SoundEvent>)registryObject, category);
    }

    public SimSoundEntry create(String name, UnaryOperator<DefinitionBuilder> operator) {
        return this.create(name, SoundSource.BLOCKS, operator);
    }

    public SoundsProvider getProvider(PackOutput output) {
        return new SoundsProvider(this.modId, output, this.definitions);
    }

    private ResourceLocation path(String path) {
        return ResourceLocation.fromNamespaceAndPath((String)this.modId, (String)path);
    }

    public void provideLang(BiConsumer<String, String> consumer) {
        this.subtitles.forEach(consumer);
    }

    public class DefinitionBuilder {
        private final String name;
        private String subtitle = null;
        private final List<SoundFile> sounds = new ArrayList<SoundFile>();

        private DefinitionBuilder(String name) {
            this.name = name;
        }

        public DefinitionBuilder defaultSubtitle(String key) {
            this.subtitle = key;
            return this;
        }

        public DefinitionBuilder defaultSubtitle(String subtitle, String key) {
            SoundEventRegistry.this.subtitles.put(key, subtitle);
            return this.defaultSubtitle(key);
        }

        public DefinitionBuilder subtitle(String subtitle) {
            String id = SoundEventRegistry.this.modId + ".subtitle." + this.name;
            return this.defaultSubtitle(subtitle, id);
        }

        public DefinitionBuilder addFileVariant(ResourceLocation path, UnaryOperator<SoundBuilder> operator) {
            SoundEventRegistry soundEventRegistry = SoundEventRegistry.this;
            Objects.requireNonNull(soundEventRegistry);
            SoundBuilder builder = new SoundBuilder(soundEventRegistry, path);
            operator.apply(builder);
            this.sounds.add(builder.build());
            return this;
        }

        public DefinitionBuilder addFileVariant(String path, UnaryOperator<SoundBuilder> operator) {
            return this.addFileVariant(SoundEventRegistry.this.path(path), operator);
        }

        public DefinitionBuilder addFileVariant(String path) {
            return this.addFileVariant(path, UnaryOperator.identity());
        }

        public DefinitionBuilder addFileVariants(String path, int count) {
            for (int i = 0; i < count; ++i) {
                int n = i + 1;
                this.addFileVariant(path + "_" + n);
            }
            return this;
        }

        public DefinitionBuilder addEventVariant(SoundEvent event, UnaryOperator<SoundBuilder> operator) {
            SoundEventRegistry soundEventRegistry = SoundEventRegistry.this;
            Objects.requireNonNull(soundEventRegistry);
            SoundBuilder builder = new SoundBuilder(soundEventRegistry, event.getLocation()).setType(SoundFile.Type.EVENT);
            operator.apply(builder);
            this.sounds.add(builder.build());
            return this;
        }

        public DefinitionBuilder addEventVariant(SimSoundEntry entry, UnaryOperator<SoundBuilder> operator) {
            SoundEventRegistry soundEventRegistry = SoundEventRegistry.this;
            Objects.requireNonNull(soundEventRegistry);
            SoundBuilder builder = new SoundBuilder(soundEventRegistry, entry.id()).setType(SoundFile.Type.EVENT);
            operator.apply(builder);
            this.sounds.add(builder.build());
            return this;
        }

        public DefinitionBuilder addEventVariant(AllSoundEvents.SoundEntry soundEntry, UnaryOperator<SoundBuilder> operator) {
            SoundEventRegistry soundEventRegistry = SoundEventRegistry.this;
            Objects.requireNonNull(soundEventRegistry);
            SoundBuilder builder = new SoundBuilder(soundEventRegistry, soundEntry.getId()).setType(SoundFile.Type.EVENT);
            operator.apply(builder);
            this.sounds.add(builder.build());
            return this;
        }

        public DefinitionBuilder addEventVariant(SoundEvent event) {
            return this.addEventVariant(event, UnaryOperator.identity());
        }

        public DefinitionBuilder addEventVariant(SimSoundEntry entry) {
            return this.addEventVariant(entry, UnaryOperator.identity());
        }

        public DefinitionBuilder addEventVariant(AllSoundEvents.SoundEntry soundEntry) {
            return this.addEventVariant(soundEntry, UnaryOperator.identity());
        }

        public SoundDefinition build() {
            return new SoundDefinition(false, Optional.ofNullable(this.subtitle), this.sounds);
        }
    }

    public class SoundBuilder {
        private final ResourceLocation name;
        private float volume = 1.0f;
        private float pitch = 1.0f;
        private int weight = 1;
        private boolean stream = false;
        private int attenuationDistance = 16;
        private boolean preload = false;
        private SoundFile.Type type = SoundFile.Type.FILE;

        private SoundBuilder(SoundEventRegistry this$0, ResourceLocation name) {
            this.name = name;
        }

        private SoundBuilder(SoundEventRegistry this$0, String name) {
            this(this$0, this$0.path(name));
        }

        public SoundBuilder setVolume(float volume) {
            this.volume = volume;
            return this;
        }

        public SoundBuilder setPitch(float pitch) {
            this.pitch = pitch;
            return this;
        }

        public SoundBuilder setWeight(int weight) {
            this.weight = weight;
            return this;
        }

        public SoundBuilder setStream(boolean stream) {
            this.stream = stream;
            return this;
        }

        public SoundBuilder setAttenuationDistance(int attenuationDistance) {
            this.attenuationDistance = attenuationDistance;
            return this;
        }

        public SoundBuilder setPreload(boolean preload) {
            this.preload = preload;
            return this;
        }

        public SoundBuilder setType(SoundFile.Type type) {
            this.type = type;
            return this;
        }

        public SoundFile build() {
            return new SoundFile(this.name, this.volume, this.pitch, this.weight, this.stream, this.attenuationDistance, this.preload, this.type);
        }
    }
}
