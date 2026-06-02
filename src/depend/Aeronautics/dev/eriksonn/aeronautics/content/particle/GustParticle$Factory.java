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

import dev.eriksonn.aeronautics.content.particle.GustParticle;
import dev.eriksonn.aeronautics.content.particle.GustParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public static class GustParticle.Factory
implements ParticleProvider<GustParticleData> {
    private final SpriteSet spriteSet;

    public GustParticle.Factory(SpriteSet animatedSprite) {
        this.spriteSet = animatedSprite;
    }

    public Particle createParticle(GustParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        GustParticle particle = new GustParticle(worldIn, x, y, z, data.orientation());
        particle.setSprite(this.spriteSet.get(worldIn.random));
        return particle;
    }
}
