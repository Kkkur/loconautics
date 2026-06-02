/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.minecraft.client.particle.ParticleEngine$SpriteParticleRegistration
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.kinetics.base;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.kinetics.base.RotationIndicatorParticle;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class RotationIndicatorParticleData
implements ParticleOptions,
ICustomParticleDataWithSprite<RotationIndicatorParticleData> {
    public static final MapCodec<RotationIndicatorParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)Codec.INT.fieldOf("color").forGetter(p -> p.color), (App)Codec.FLOAT.fieldOf("speed").forGetter(p -> Float.valueOf(p.speed)), (App)Codec.FLOAT.fieldOf("radius1").forGetter(p -> Float.valueOf(p.radius1)), (App)Codec.FLOAT.fieldOf("radius2").forGetter(p -> Float.valueOf(p.radius2)), (App)Codec.INT.fieldOf("life_span").forGetter(p -> p.lifeSpan), (App)Direction.Axis.CODEC.fieldOf("axis").forGetter(p -> p.axis)).apply((Applicative)i, RotationIndicatorParticleData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, RotationIndicatorParticleData> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, p -> p.color, (StreamCodec)ByteBufCodecs.FLOAT, p -> Float.valueOf(p.speed), (StreamCodec)ByteBufCodecs.FLOAT, p -> Float.valueOf(p.radius1), (StreamCodec)ByteBufCodecs.FLOAT, p -> Float.valueOf(p.radius2), (StreamCodec)ByteBufCodecs.INT, p -> p.lifeSpan, (StreamCodec)CatnipStreamCodecs.AXIS, p -> p.axis, RotationIndicatorParticleData::new);
    final int color;
    final float speed;
    final float radius1;
    final float radius2;
    final int lifeSpan;
    final Direction.Axis axis;

    public RotationIndicatorParticleData(int color, float speed, float radius1, float radius2, int lifeSpan, Direction.Axis axis) {
        this.color = color;
        this.speed = speed;
        this.radius1 = radius1;
        this.radius2 = radius2;
        this.lifeSpan = lifeSpan;
        this.axis = axis;
    }

    public RotationIndicatorParticleData() {
        this(0, 0.0f, 0.0f, 0.0f, 0, Direction.Axis.X);
    }

    public ParticleType<?> getType() {
        return AllParticleTypes.ROTATION_INDICATOR.get();
    }

    public Direction.Axis getAxis() {
        return this.axis;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, RotationIndicatorParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public MapCodec<RotationIndicatorParticleData> getCodec(ParticleType<RotationIndicatorParticleData> type) {
        return CODEC;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ParticleEngine.SpriteParticleRegistration<RotationIndicatorParticleData> getMetaFactory() {
        return RotationIndicatorParticle.Factory::new;
    }
}
