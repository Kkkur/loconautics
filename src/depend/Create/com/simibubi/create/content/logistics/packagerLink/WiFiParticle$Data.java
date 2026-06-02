/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 */
package com.simibubi.create.content.logistics.packagerLink;

import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.equipment.bell.BasicParticleData;
import com.simibubi.create.content.logistics.packagerLink.WiFiParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public static class WiFiParticle.Data
extends BasicParticleData<WiFiParticle>
implements ParticleOptions {
    @Override
    public BasicParticleData.IBasicParticleFactory<WiFiParticle> getBasicFactory() {
        return WiFiParticle::new;
    }

    public ParticleType<?> getType() {
        return AllParticleTypes.WIFI.get();
    }
}
