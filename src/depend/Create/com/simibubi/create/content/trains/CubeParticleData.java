/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.trains.CubeParticle;
import com.simibubi.create.foundation.particle.ICustomParticleData;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class CubeParticleData
implements ParticleOptions,
ICustomParticleData<CubeParticleData> {
    public static final MapCodec<CubeParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)Codec.FLOAT.fieldOf("r").forGetter(p -> Float.valueOf(p.r)), (App)Codec.FLOAT.fieldOf("g").forGetter(p -> Float.valueOf(p.g)), (App)Codec.FLOAT.fieldOf("b").forGetter(p -> Float.valueOf(p.b)), (App)Codec.FLOAT.fieldOf("scale").forGetter(p -> Float.valueOf(p.scale)), (App)Codec.INT.fieldOf("avg_age").forGetter(p -> p.avgAge), (App)Codec.BOOL.fieldOf("hot").forGetter(p -> p.hot)).apply((Applicative)i, CubeParticleData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, CubeParticleData> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.FLOAT, p -> Float.valueOf(p.r), (StreamCodec)ByteBufCodecs.FLOAT, p -> Float.valueOf(p.g), (StreamCodec)ByteBufCodecs.FLOAT, p -> Float.valueOf(p.b), (StreamCodec)ByteBufCodecs.FLOAT, p -> Float.valueOf(p.scale), (StreamCodec)ByteBufCodecs.INT, p -> p.avgAge, (StreamCodec)ByteBufCodecs.BOOL, p -> p.hot, CubeParticleData::new);
    final float r;
    final float g;
    final float b;
    final float scale;
    final int avgAge;
    final boolean hot;

    public CubeParticleData(float r, float g, float b, float scale, int avgAge, boolean hot) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.scale = scale;
        this.avgAge = avgAge;
        this.hot = hot;
    }

    public CubeParticleData() {
        this(0.0f, 0.0f, 0.0f, 0.0f, 0, false);
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, CubeParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public MapCodec<CubeParticleData> getCodec(ParticleType<CubeParticleData> type) {
        return CODEC;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ParticleProvider<CubeParticleData> getFactory() {
        return new CubeParticle.Factory();
    }

    public ParticleType<?> getType() {
        return AllParticleTypes.CUBE.get();
    }
}
