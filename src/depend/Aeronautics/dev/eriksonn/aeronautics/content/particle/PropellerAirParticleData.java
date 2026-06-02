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
import dev.eriksonn.aeronautics.content.particle.PropellerAirParticle;
import dev.eriksonn.aeronautics.index.AeroParticleTypes;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class PropellerAirParticleData
implements ParticleOptions,
ICustomParticleDataWithSprite<PropellerAirParticleData> {
    private static final MapCodec<PropellerAirParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)Codec.BOOL.fieldOf("collision").forGetter(p -> p.enableCollision), (App)Codec.BOOL.fieldOf("virtual").forGetter(p -> p.isVirtual)).apply((Applicative)i, PropellerAirParticleData::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, PropellerAirParticleData> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.BOOL, p -> p.enableCollision, (StreamCodec)ByteBufCodecs.BOOL, p -> p.isVirtual, PropellerAirParticleData::new);
    boolean enableCollision;
    boolean isVirtual;

    public PropellerAirParticleData(boolean enableCollision, boolean isVirtual) {
        this.enableCollision = enableCollision;
        this.isVirtual = isVirtual;
    }

    public PropellerAirParticleData() {
        this(true, false);
    }

    public ParticleType<?> getType() {
        return AeroParticleTypes.PROPELLER_AIR_FLOW.get();
    }

    public ParticleEngine.SpriteParticleRegistration<PropellerAirParticleData> getMetaFactory() {
        return PropellerAirParticle.Factory::new;
    }

    public MapCodec<PropellerAirParticleData> getCodec(ParticleType<PropellerAirParticleData> particleType) {
        return CODEC;
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, PropellerAirParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }
}
