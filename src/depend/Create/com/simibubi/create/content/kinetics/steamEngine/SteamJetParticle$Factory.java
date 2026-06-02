/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.SpriteSet
 */
package com.simibubi.create.content.kinetics.steamEngine;

import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticle;
import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public static class SteamJetParticle.Factory
implements ParticleProvider<SteamJetParticleData> {
    private final SpriteSet spriteSet;

    public SteamJetParticle.Factory(SpriteSet animatedSprite) {
        this.spriteSet = animatedSprite;
    }

    public Particle createParticle(SteamJetParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new SteamJetParticle(worldIn, data, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
    }
}
