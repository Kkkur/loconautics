/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.particle.ParticleEngine$SpriteParticleRegistration
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 */
package dev.simulated_team.simulated.content.particle;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import dev.simulated_team.simulated.content.particle.MagnetFieldParticle;
import dev.simulated_team.simulated.index.SimParticleTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class MagnetFieldParticleData
implements ParticleOptions,
ICustomParticleDataWithSprite<MagnetFieldParticleData> {
    public static final MapCodec<MagnetFieldParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)Codec.BOOL.fieldOf("negative").forGetter(p -> p.negative)).apply((Applicative)i, MagnetFieldParticleData::new));
    public static final StreamCodec<ByteBuf, MagnetFieldParticleData> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.BOOL, p -> p.negative, MagnetFieldParticleData::new);
    private boolean negative;

    public MagnetFieldParticleData(boolean negative) {
        this.negative = negative;
    }

    public MagnetFieldParticleData() {
        this.negative = false;
    }

    public ParticleType<?> getType() {
        return SimParticleTypes.MAGNET_FIELD.get();
    }

    public MapCodec<MagnetFieldParticleData> getCodec(ParticleType<MagnetFieldParticleData> type) {
        return CODEC;
    }

    public ParticleEngine.SpriteParticleRegistration<MagnetFieldParticleData> getMetaFactory() {
        return MagnetFieldParticle.Factory::new;
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, MagnetFieldParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }

    public boolean isNegative() {
        return this.negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }
}
