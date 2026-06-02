/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.SpriteSet
 */
package dev.eriksonn.aeronautics.content.particle;

import dev.eriksonn.aeronautics.content.particle.AirPoofParticle;
import dev.eriksonn.aeronautics.content.particle.AirPoofParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public record AirPoofParticle.Factory(SpriteSet spriteSet) implements ParticleProvider<AirPoofParticleData>
{
    public Particle createParticle(AirPoofParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        AirPoofParticle particle = new AirPoofParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        particle.pickSprite(this.spriteSet);
        return particle;
    }
}
