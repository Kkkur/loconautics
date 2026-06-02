/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids.particle;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.fluids.particle.FluidStackParticle;
import com.simibubi.create.foundation.particle.ICustomParticleData;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidParticleData
implements ParticleOptions,
ICustomParticleData<FluidParticleData> {
    private ParticleType<FluidParticleData> type;
    private FluidStack fluid;
    public static final MapCodec<FluidParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)FluidStack.CODEC.fieldOf("fluid").forGetter(p -> p.fluid)).apply((Applicative)i, fs -> new FluidParticleData(AllParticleTypes.FLUID_PARTICLE.get(), (FluidStack)fs)));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidParticleData> STREAM_CODEC = FluidStack.STREAM_CODEC.map(fs -> new FluidParticleData(AllParticleTypes.FLUID_PARTICLE.get(), (FluidStack)fs), p -> p.fluid);
    public static final MapCodec<FluidParticleData> BASIN_CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)FluidStack.CODEC.fieldOf("fluid").forGetter(p -> p.fluid)).apply((Applicative)i, fs -> new FluidParticleData(AllParticleTypes.BASIN_FLUID.get(), (FluidStack)fs)));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidParticleData> BASIN_STREAM_CODEC = FluidStack.STREAM_CODEC.map(fs -> new FluidParticleData(AllParticleTypes.BASIN_FLUID.get(), (FluidStack)fs), p -> p.fluid);
    public static final MapCodec<FluidParticleData> DRIP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)FluidStack.CODEC.fieldOf("fluid").forGetter(p -> p.fluid)).apply((Applicative)i, fs -> new FluidParticleData(AllParticleTypes.FLUID_DRIP.get(), (FluidStack)fs)));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidParticleData> DRIP_STREAM_CODEC = FluidStack.STREAM_CODEC.map(fs -> new FluidParticleData(AllParticleTypes.FLUID_DRIP.get(), (FluidStack)fs), p -> p.fluid);

    public FluidParticleData() {
    }

    public FluidParticleData(ParticleType<?> type, FluidStack fluid) {
        this.type = type;
        this.fluid = fluid;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ParticleProvider<FluidParticleData> getFactory() {
        return (data, world, x, y, z, vx, vy, vz) -> FluidStackParticle.create(data.type, world, data.fluid, x, y, z, vx, vy, vz);
    }

    public ParticleType<?> getType() {
        return this.type;
    }

    @Override
    public MapCodec<FluidParticleData> getCodec(ParticleType<FluidParticleData> type) {
        if (type == AllParticleTypes.BASIN_FLUID.get()) {
            return BASIN_CODEC;
        }
        if (type == AllParticleTypes.FLUID_DRIP.get()) {
            return DRIP_CODEC;
        }
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, FluidParticleData> getStreamCodec() {
        if (this.type == AllParticleTypes.BASIN_FLUID.get()) {
            return BASIN_STREAM_CODEC;
        }
        if (this.type == AllParticleTypes.FLUID_DRIP.get()) {
            return DRIP_STREAM_CODEC;
        }
        return STREAM_CODEC;
    }
}
