/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.simibubi.create;

import com.simibubi.create.foundation.particle.ICustomParticleData;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

private static class AllParticleTypes.ParticleEntry<D extends ParticleOptions> {
    private static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create((ResourceKey)Registries.PARTICLE_TYPE, (String)"create");
    private final String name;
    private final Supplier<? extends ICustomParticleData<D>> typeFactory;
    private final DeferredHolder<ParticleType<?>, ParticleType<D>> object;

    public AllParticleTypes.ParticleEntry(String name, Supplier<? extends ICustomParticleData<D>> typeFactory) {
        this.name = name;
        this.typeFactory = typeFactory;
        this.object = REGISTER.register(name, () -> this.typeFactory.get().createType());
    }

    @OnlyIn(value=Dist.CLIENT)
    public void registerFactory(RegisterParticleProvidersEvent event) {
        this.typeFactory.get().register((ParticleType)this.object.get(), event);
    }
}
