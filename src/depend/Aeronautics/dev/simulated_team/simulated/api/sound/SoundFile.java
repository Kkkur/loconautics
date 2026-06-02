/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.StringRepresentable
 */
package dev.simulated_team.simulated.api.sound;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.simulated_team.simulated.util.SimCodecUtil;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public record SoundFile(ResourceLocation name, float volume, float pitch, int weight, boolean stream, int attenuationDistance, boolean preload, Type type) {
    public static final SoundFile DEFAULT = new SoundFile(ResourceLocation.withDefaultNamespace((String)"default"), 1.0f, 1.0f, 1, false, 16, false, Type.FILE);
    public static final Codec<SoundFile> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ResourceLocation.CODEC.fieldOf("name").forGetter(SoundFile::name), (App)Codec.FLOAT.optionalFieldOf("volume", (Object)Float.valueOf(DEFAULT.volume())).forGetter(SoundFile::volume), (App)Codec.FLOAT.optionalFieldOf("pitch", (Object)Float.valueOf(DEFAULT.pitch())).forGetter(SoundFile::pitch), (App)Codec.INT.optionalFieldOf("weight", (Object)DEFAULT.weight()).forGetter(SoundFile::weight), (App)Codec.BOOL.optionalFieldOf("stream", (Object)DEFAULT.stream()).forGetter(SoundFile::stream), (App)Codec.INT.optionalFieldOf("attenuation_distance", (Object)DEFAULT.attenuationDistance()).forGetter(SoundFile::attenuationDistance), (App)Codec.BOOL.optionalFieldOf("preload", (Object)DEFAULT.preload()).forGetter(SoundFile::preload), (App)StringRepresentable.fromEnum(Type::values).optionalFieldOf("type", (Object)DEFAULT.type()).forGetter(SoundFile::type)).apply((Applicative)instance, SoundFile::new));
    public static final Codec<SoundFile> SIMPLE_CODEC = ResourceLocation.CODEC.flatXmap(rl -> DataResult.success((Object)new SoundFile((ResourceLocation)rl)), sf -> sf.isDefault() ? DataResult.success((Object)sf.name()) : DataResult.error(() -> "Object is not default"));
    public static final Codec<SoundFile> FULL_CODEC = SimCodecUtil.withAlternative(SIMPLE_CODEC, CODEC);

    public SoundFile(ResourceLocation name) {
        this(name, DEFAULT.volume(), DEFAULT.pitch(), DEFAULT.weight(), DEFAULT.stream(), DEFAULT.attenuationDistance(), DEFAULT.preload(), DEFAULT.type());
    }

    public boolean isDefault() {
        return this.weight == DEFAULT.weight() && this.pitch == DEFAULT.pitch() && this.volume == DEFAULT.volume() && this.stream == DEFAULT.stream() && this.preload == DEFAULT.preload() && this.attenuationDistance == DEFAULT.attenuationDistance() && this.type == DEFAULT.type();
    }

    public static enum Type implements StringRepresentable
    {
        FILE,
        EVENT;


        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
