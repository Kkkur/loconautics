/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleType
 */
package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.equipment.bell.SoulParticle;
import net.minecraft.core.particles.ParticleType;

public static class SoulParticle.ExpandingPerimeterData
extends SoulParticle.PerimeterData {
    @Override
    public ParticleType<?> getType() {
        return AllParticleTypes.SOUL_EXPANDING_PERIMETER.get();
    }
}
