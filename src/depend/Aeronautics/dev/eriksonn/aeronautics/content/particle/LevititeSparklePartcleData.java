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
import dev.eriksonn.aeronautics.content.particle.LevititeSparklePartcle;
import dev.eriksonn.aeronautics.index.AeroParticleTypes;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class LevititeSparklePartcleData
implements ParticleOptions,
ICustomParticleDataWithSprite<LevititeSparklePartcleData> {
    public static final int LEVITITE_GREEN = 9424022;
    public static final int LEVITITE_PINK = 15521489;
    public static final MapCodec<LevititeSparklePartcleData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.INT.fieldOf("color").forGetter(p -> p.color)).apply((Applicative)instance, LevititeSparklePartcleData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LevititeSparklePartcleData> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, p -> p.color, LevititeSparklePartcleData::new);
    public final int color;

    public LevititeSparklePartcleData(int color) {
        this.color = color;
    }

    public LevititeSparklePartcleData() {
        this(9424022);
    }

    public ParticleEngine.SpriteParticleRegistration<LevititeSparklePartcleData> getMetaFactory() {
        return LevititeSparklePartcle.Factory::new;
    }

    public MapCodec<LevititeSparklePartcleData> getCodec(ParticleType<LevititeSparklePartcleData> type) {
        return CODEC;
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, LevititeSparklePartcleData> getStreamCodec() {
        return STREAM_CODEC;
    }

    public ParticleType<?> getType() {
        return AeroParticleTypes.LEVITITE_SPARKLE.get();
    }
}
