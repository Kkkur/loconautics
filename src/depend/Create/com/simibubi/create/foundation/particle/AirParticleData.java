/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.client.particle.ParticleEngine$SpriteParticleRegistration
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.particle;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.foundation.particle.AirParticle;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class AirParticleData
implements ParticleOptions,
ICustomParticleDataWithSprite<AirParticleData> {
    public static final MapCodec<AirParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)Codec.FLOAT.fieldOf("drag").forGetter(p -> Float.valueOf(p.drag)), (App)Codec.FLOAT.fieldOf("speed").forGetter(p -> Float.valueOf(p.speed))).apply((Applicative)i, AirParticleData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AirParticleData> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.FLOAT, p -> Float.valueOf(p.drag), (StreamCodec)ByteBufCodecs.FLOAT, p -> Float.valueOf(p.speed), AirParticleData::new);
    float drag;
    float speed;

    public AirParticleData(float drag, float speed) {
        this.drag = drag;
        this.speed = speed;
    }

    public AirParticleData() {
        this(0.0f, 0.0f);
    }

    @NotNull
    public ParticleType<?> getType() {
        return AllParticleTypes.AIR.get();
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, AirParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public MapCodec<AirParticleData> getCodec(ParticleType<AirParticleData> type) {
        return CODEC;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ParticleEngine.SpriteParticleRegistration<AirParticleData> getMetaFactory() {
        return AirParticle.Factory::new;
    }
}
