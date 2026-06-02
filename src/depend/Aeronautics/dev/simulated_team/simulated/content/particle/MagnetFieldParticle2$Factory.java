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

import dev.simulated_team.simulated.content.particle.MagnetFieldParticle2;
import dev.simulated_team.simulated.content.particle.MagnetFieldParticleData2;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public static class MagnetFieldParticle2.Factory
implements ParticleProvider<MagnetFieldParticleData2> {
    private final SpriteSet spriteSet;

    public MagnetFieldParticle2.Factory(SpriteSet animatedSprite) {
        this.spriteSet = animatedSprite;
    }

    public Particle createParticle(MagnetFieldParticleData2 data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new MagnetFieldParticle2(level, x, y, z, data.previousOffset.x, data.previousOffset.y, data.previousOffset.z, data.nextOffset.x, data.nextOffset.y, data.nextOffset.z, this.spriteSet, data.isNegative(), data.getTimeUntilEnd());
    }
}
