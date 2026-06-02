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
 *  net.minecraft.client.particle.ParticleEngine$SpriteParticleRegistration
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 */
package dev.eriksonn.aeronautics.content.particle;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import dev.eriksonn.aeronautics.content.particle.HotAirEmberParticle;
import dev.eriksonn.aeronautics.index.AeroParticleTypes;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class HotAirEmberParticleData
implements ParticleOptions,
ICustomParticleDataWithSprite<HotAirEmberParticleData> {
    private static final MapCodec<HotAirEmberParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)Codec.BOOL.fieldOf("isSoul").forGetter(p -> p.isSoul)).apply((Applicative)i, HotAirEmberParticleData::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, HotAirEmberParticleData> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.BOOL, p -> p.isSoul, HotAirEmberParticleData::new);
    protected final boolean isSoul;

    public HotAirEmberParticleData(boolean isSoul) {
        this.isSoul = isSoul;
    }

    public HotAirEmberParticleData() {
        this.isSoul = false;
    }

    public ParticleType<?> getType() {
        return AeroParticleTypes.HOT_AIR_EMBER.get();
    }

    public ParticleEngine.SpriteParticleRegistration<HotAirEmberParticleData> getMetaFactory() {
        return HotAirEmberParticle.Factory::new;
    }

    public MapCodec<HotAirEmberParticleData> getCodec(ParticleType<HotAirEmberParticleData> particleType) {
        return CODEC;
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, HotAirEmberParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }
}
