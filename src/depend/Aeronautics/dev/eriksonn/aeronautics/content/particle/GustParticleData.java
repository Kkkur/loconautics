/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite
 *  net.minecraft.client.particle.ParticleEngine$SpriteParticleRegistration
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.util.ExtraCodecs
 *  org.joml.Quaternionf
 */
package dev.eriksonn.aeronautics.content.particle;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import dev.eriksonn.aeronautics.content.particle.GustParticle;
import dev.eriksonn.aeronautics.index.AeroParticleTypes;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Quaternionf;

public record GustParticleData(Quaternionf orientation) implements ParticleOptions,
ICustomParticleDataWithSprite<GustParticleData>
{
    private static final MapCodec<GustParticleData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)ExtraCodecs.QUATERNIONF.fieldOf("orientation").forGetter(o -> o.orientation)).apply((Applicative)instance, GustParticleData::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, GustParticleData> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.QUATERNIONF, o -> o.orientation, GustParticleData::new);

    public GustParticleData() {
        this(new Quaternionf());
    }

    public ParticleEngine.SpriteParticleRegistration<GustParticleData> getMetaFactory() {
        return GustParticle.Factory::new;
    }

    public MapCodec<GustParticleData> getCodec(ParticleType<GustParticleData> type) {
        return CODEC;
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, GustParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }

    public ParticleType<?> getType() {
        return AeroParticleTypes.GUST.get();
    }
}
