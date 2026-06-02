/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.SpriteSet
 */
package com.simibubi.create.foundation.particle;

import com.simibubi.create.foundation.particle.AirParticle;
import com.simibubi.create.foundation.particle.AirParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public static class AirParticle.Factory
implements ParticleProvider<AirParticleData> {
    private final SpriteSet spriteSet;

    public AirParticle.Factory(SpriteSet animatedSprite) {
        this.spriteSet = animatedSprite;
    }

    public Particle createParticle(AirParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new AirParticle(worldIn, data, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
    }
}
