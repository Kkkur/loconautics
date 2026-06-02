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
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.minecraft.client.particle.ParticleEngine$SpriteParticleRegistration
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.particle;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import dev.simulated_team.simulated.content.particle.MagnetFieldParticle2;
import dev.simulated_team.simulated.index.SimParticleTypes;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class MagnetFieldParticleData2
implements ParticleOptions,
ICustomParticleDataWithSprite<MagnetFieldParticleData2> {
    public static final MapCodec<MagnetFieldParticleData2> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Vec3.CODEC.fieldOf("previous_offset").forGetter(p -> p.previousOffset), (App)Vec3.CODEC.fieldOf("next_offset").forGetter(p -> p.nextOffset), (App)Codec.BOOL.fieldOf("negative").forGetter(p -> p.negative), (App)Codec.INT.fieldOf("timeUntilEnd").forGetter(p -> p.timeUntilEnd)).apply((Applicative)instance, MagnetFieldParticleData2::new));
    public static final StreamCodec<ByteBuf, MagnetFieldParticleData2> STREAM_CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecs.VEC3, p -> p.previousOffset, (StreamCodec)CatnipStreamCodecs.VEC3, p -> p.nextOffset, (StreamCodec)ByteBufCodecs.BOOL, p -> p.negative, (StreamCodec)ByteBufCodecs.INT, p -> p.timeUntilEnd, MagnetFieldParticleData2::new);
    Vec3 previousOffset;
    Vec3 nextOffset;
    private boolean negative;
    private int timeUntilEnd;

    public MagnetFieldParticleData2(Vec3 previousOffset, Vec3 nextOffset, boolean negative, int timeUntilEnd) {
        this.negative = negative;
        this.previousOffset = previousOffset;
        this.nextOffset = nextOffset;
        this.timeUntilEnd = timeUntilEnd;
    }

    public MagnetFieldParticleData2() {
        this.negative = false;
    }

    public ParticleType<?> getType() {
        return SimParticleTypes.MAGNET_FIELD2.get();
    }

    public MapCodec<MagnetFieldParticleData2> getCodec(ParticleType<MagnetFieldParticleData2> type) {
        return CODEC;
    }

    public ParticleEngine.SpriteParticleRegistration<MagnetFieldParticleData2> getMetaFactory() {
        return MagnetFieldParticle2.Factory::new;
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, MagnetFieldParticleData2> getStreamCodec() {
        return STREAM_CODEC;
    }

    public boolean isNegative() {
        return this.negative;
    }

    public int getTimeUntilEnd() {
        return this.timeUntilEnd;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }
}
