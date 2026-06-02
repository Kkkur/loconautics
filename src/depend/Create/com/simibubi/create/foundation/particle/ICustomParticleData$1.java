/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

class ICustomParticleData.1
extends ParticleType<T> {
    ICustomParticleData.1(boolean arg0) {
        super(arg0);
    }

    @NotNull
    public MapCodec<T> codec() {
        return ICustomParticleData.this.getCodec(this);
    }

    @NotNull
    public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
        return ICustomParticleData.this.getStreamCodec();
    }
}
