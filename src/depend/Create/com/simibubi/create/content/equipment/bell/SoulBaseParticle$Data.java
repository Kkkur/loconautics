/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleType
 */
package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.equipment.bell.BasicParticleData;
import com.simibubi.create.content.equipment.bell.SoulBaseParticle;
import net.minecraft.core.particles.ParticleType;

public static class SoulBaseParticle.Data
extends BasicParticleData<SoulBaseParticle> {
    @Override
    public BasicParticleData.IBasicParticleFactory<SoulBaseParticle> getBasicFactory() {
        return SoulBaseParticle::new;
    }

    public ParticleType<?> getType() {
        return AllParticleTypes.SOUL_BASE.get();
    }
}
