/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleType
 */
package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.equipment.bell.BasicParticleData;
import com.simibubi.create.content.equipment.bell.SoulParticle;
import net.minecraft.core.particles.ParticleType;

public static class SoulParticle.Data
extends BasicParticleData<SoulParticle> {
    @Override
    public BasicParticleData.IBasicParticleFactory<SoulParticle> getBasicFactory() {
        return (worldIn, x, y, z, vx, vy, vz, spriteSet) -> new SoulParticle(worldIn, x, y, z, vx, vy, vz, spriteSet, this);
    }

    public ParticleType<?> getType() {
        return AllParticleTypes.SOUL.get();
    }
}
