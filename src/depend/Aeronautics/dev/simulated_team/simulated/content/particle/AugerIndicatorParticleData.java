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
 *  net.minecraft.core.Direction
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package dev.simulated_team.simulated.content.particle;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import dev.simulated_team.simulated.content.particle.AugerIndicatorParticle;
import dev.simulated_team.simulated.index.SimParticleTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class AugerIndicatorParticleData
implements ParticleOptions,
ICustomParticleDataWithSprite<AugerIndicatorParticleData> {
    public static final MapCodec<AugerIndicatorParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)Codec.INT.fieldOf("color").forGetter(p -> p.color), (App)Codec.FLOAT.fieldOf("speed").forGetter(p -> Float.valueOf(p.speed)), (App)Codec.FLOAT.fieldOf("radius1").forGetter(p -> Float.valueOf(p.radius1)), (App)Codec.FLOAT.fieldOf("radius2").forGetter(p -> Float.valueOf(p.radius2)), (App)Codec.FLOAT.fieldOf("angle_offset").forGetter(p -> Float.valueOf(p.angleOffset)), (App)Codec.INT.fieldOf("life_span").forGetter(p -> p.lifeSpan), (App)Direction.CODEC.fieldOf("direction").forGetter(p -> p.direction)).apply((Applicative)i, AugerIndicatorParticleData::new));
    public static final StreamCodec<ByteBuf, AugerIndicatorParticleData> STREAM_CODEC = ByteBufCodecs.fromCodec((Codec)CODEC.codec());
    public final int color;
    public final float speed;
    public final float radius1;
    public final float radius2;
    public float angleOffset;
    public final int lifeSpan;
    public final Direction direction;

    public AugerIndicatorParticleData(int color, float speed, float radius1, float radius2, float angleOffset, int lifeSpan, Direction direction) {
        this.color = color;
        this.speed = speed;
        this.radius1 = radius1;
        this.radius2 = radius2;
        this.angleOffset = angleOffset;
        this.lifeSpan = lifeSpan;
        this.direction = direction;
    }

    public AugerIndicatorParticleData() {
        this(0, 0.0f, 0.0f, 0.0f, 0.0f, 0, Direction.NORTH);
    }

    public ParticleType<?> getType() {
        return SimParticleTypes.AUGER_INDICATOR.get();
    }

    public MapCodec<AugerIndicatorParticleData> getCodec(ParticleType<AugerIndicatorParticleData> type) {
        return CODEC;
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, AugerIndicatorParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }

    @OnlyIn(value=Dist.CLIENT)
    public ParticleEngine.SpriteParticleRegistration<AugerIndicatorParticleData> getMetaFactory() {
        return AugerIndicatorParticle.Factory::new;
    }
}
