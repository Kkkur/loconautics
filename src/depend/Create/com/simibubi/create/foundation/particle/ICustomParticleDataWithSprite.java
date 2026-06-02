/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.client.particle.ParticleEngine$SpriteParticleRegistration
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.particle;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.particle.ICustomParticleData;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.NotNull;

public interface ICustomParticleDataWithSprite<T extends ParticleOptions>
extends ICustomParticleData<T> {
    @Override
    default public ParticleType<T> createType() {
        return new ParticleType<T>(false){

            @NotNull
            public MapCodec<T> codec() {
                return ICustomParticleDataWithSprite.this.getCodec(this);
            }

            @NotNull
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return ICustomParticleDataWithSprite.this.getStreamCodec();
            }
        };
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    default public ParticleProvider<T> getFactory() {
        throw new IllegalAccessError("This particle type uses a metaFactory!");
    }

    @OnlyIn(value=Dist.CLIENT)
    public ParticleEngine.SpriteParticleRegistration<T> getMetaFactory();

    @Override
    @OnlyIn(value=Dist.CLIENT)
    default public void register(ParticleType<T> type, RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(type, this.getMetaFactory());
    }
}
