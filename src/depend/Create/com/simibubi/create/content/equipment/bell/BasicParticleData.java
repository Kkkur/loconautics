/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleEngine$SpriteParticleRegistration
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.bell;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BasicParticleData<T extends Particle>
implements ParticleOptions,
ICustomParticleDataWithSprite<BasicParticleData<T>> {
    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, BasicParticleData<T>> getStreamCodec() {
        return StreamCodec.unit((Object)this);
    }

    @Override
    public MapCodec<BasicParticleData<T>> getCodec(ParticleType<BasicParticleData<T>> type) {
        return MapCodec.unit((Object)this);
    }

    @OnlyIn(value=Dist.CLIENT)
    public abstract IBasicParticleFactory<T> getBasicFactory();

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ParticleEngine.SpriteParticleRegistration<BasicParticleData<T>> getMetaFactory() {
        return animatedSprite -> (data, worldIn, x, y, z, vx, vy, vz) -> this.getBasicFactory().makeParticle(worldIn, x, y, z, vx, vy, vz, animatedSprite);
    }

    public static interface IBasicParticleFactory<U extends Particle> {
        public U makeParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14);
    }
}
