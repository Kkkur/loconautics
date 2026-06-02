/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
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
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.NotNull;

public interface ICustomParticleData<T extends ParticleOptions> {
    public MapCodec<T> getCodec(ParticleType<T> var1);

    public StreamCodec<? super RegistryFriendlyByteBuf, T> getStreamCodec();

    default public ParticleType<T> createType() {
        return new ParticleType<T>(false){

            @NotNull
            public MapCodec<T> codec() {
                return ICustomParticleData.this.getCodec(this);
            }

            @NotNull
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return ICustomParticleData.this.getStreamCodec();
            }
        };
    }

    @OnlyIn(value=Dist.CLIENT)
    public ParticleProvider<T> getFactory();

    @OnlyIn(value=Dist.CLIENT)
    default public void register(ParticleType<T> type, RegisterParticleProvidersEvent event) {
        event.registerSpecial(type, this.getFactory());
    }
}
