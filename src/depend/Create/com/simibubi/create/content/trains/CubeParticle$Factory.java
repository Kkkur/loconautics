/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 */
package com.simibubi.create.content.trains;

import com.simibubi.create.content.trains.CubeParticle;
import com.simibubi.create.content.trains.CubeParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;

public static class CubeParticle.Factory
implements ParticleProvider<CubeParticleData> {
    public Particle createParticle(CubeParticleData data, ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        CubeParticle particle = new CubeParticle(world, x, y, z, motionX, motionY, motionZ);
        particle.setColor(data.r, data.g, data.b);
        particle.setScale(data.scale);
        particle.averageAge(data.avgAge);
        particle.setHot(data.hot);
        return particle;
    }
}
