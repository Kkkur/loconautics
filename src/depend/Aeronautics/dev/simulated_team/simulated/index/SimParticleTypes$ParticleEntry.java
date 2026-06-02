/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.particle.ICustomParticleData
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 */
package dev.simulated_team.simulated.index;

import com.simibubi.create.foundation.particle.ICustomParticleData;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public static class SimParticleTypes.ParticleEntry<D extends ParticleOptions> {
    private final Supplier<? extends ICustomParticleData<D>> typeFactory;
    private final ParticleType<D> object;

    public SimParticleTypes.ParticleEntry(Supplier<? extends ICustomParticleData<D>> typeFactory) {
        this.typeFactory = typeFactory;
        this.object = this.typeFactory.get().createType();
    }

    public Supplier<? extends ICustomParticleData<D>> getTypeFactory() {
        return this.typeFactory;
    }

    public ParticleType<D> getObject() {
        return this.object;
    }
}
