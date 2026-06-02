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
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.kinetics.fan;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.kinetics.fan.AirFlowParticle;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class AirFlowParticleData
implements ParticleOptions,
ICustomParticleDataWithSprite<AirFlowParticleData> {
    public static final MapCodec<AirFlowParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)Codec.INT.fieldOf("x").forGetter(p -> p.posX), (App)Codec.INT.fieldOf("y").forGetter(p -> p.posY), (App)Codec.INT.fieldOf("z").forGetter(p -> p.posZ)).apply((Applicative)i, AirFlowParticleData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AirFlowParticleData> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, p -> p.posX, (StreamCodec)ByteBufCodecs.INT, p -> p.posY, (StreamCodec)ByteBufCodecs.INT, p -> p.posZ, AirFlowParticleData::new);
    final int posX;
    final int posY;
    final int posZ;

    public AirFlowParticleData(Vec3i pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public AirFlowParticleData(int posX, int posY, int posZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public AirFlowParticleData() {
        this(0, 0, 0);
    }

    @NotNull
    public ParticleType<?> getType() {
        return AllParticleTypes.AIR_FLOW.get();
    }

    @Override
    public MapCodec<AirFlowParticleData> getCodec(ParticleType<AirFlowParticleData> type) {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, AirFlowParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ParticleEngine.SpriteParticleRegistration<AirFlowParticleData> getMetaFactory() {
        return AirFlowParticle.Factory::new;
    }
}
