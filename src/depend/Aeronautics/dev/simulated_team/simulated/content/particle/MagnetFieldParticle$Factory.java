/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.SpriteSet
 */
package dev.simulated_team.simulated.content.particle;

import dev.simulated_team.simulated.content.particle.MagnetFieldParticle;
import dev.simulated_team.simulated.content.particle.MagnetFieldParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public static class MagnetFieldParticle.Factory
implements ParticleProvider<MagnetFieldParticleData> {
    private final SpriteSet spriteSet;

    public MagnetFieldParticle.Factory(SpriteSet animatedSprite) {
        this.spriteSet = animatedSprite;
    }

    public Particle createParticle(MagnetFieldParticleData data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new MagnetFieldParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet, data.isNegative());
    }
}
