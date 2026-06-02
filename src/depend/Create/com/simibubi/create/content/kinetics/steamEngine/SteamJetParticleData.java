/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.particle.ParticleEngine$SpriteParticleRegistration
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.kinetics.steamEngine;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticle;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SteamJetParticleData
implements ParticleOptions,
ICustomParticleDataWithSprite<SteamJetParticleData> {
    public static final MapCodec<SteamJetParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)Codec.FLOAT.fieldOf("speed").forGetter(p -> Float.valueOf(p.speed))).apply((Applicative)i, SteamJetParticleData::new));
    public static final StreamCodec<ByteBuf, SteamJetParticleData> STREAM_CODEC = ByteBufCodecs.FLOAT.map(SteamJetParticleData::new, p -> Float.valueOf(p.speed));
    float speed;

    public SteamJetParticleData(float speed) {
        this.speed = speed;
    }

    public SteamJetParticleData() {
        this(0.0f);
    }

    public ParticleType<?> getType() {
        return AllParticleTypes.STEAM_JET.get();
    }

    @Override
    public MapCodec<SteamJetParticleData> getCodec(ParticleType<SteamJetParticleData> type) {
        return CODEC;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ParticleEngine.SpriteParticleRegistration<SteamJetParticleData> getMetaFactory() {
        return SteamJetParticle.Factory::new;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, SteamJetParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }
}
