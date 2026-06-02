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

import dev.eriksonn.aeronautics.content.particle.PropellerAirParticle;
import dev.eriksonn.aeronautics.content.particle.PropellerAirParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public static class PropellerAirParticle.Factory
implements ParticleProvider<PropellerAirParticleData> {
    private final SpriteSet spriteSet;

    public PropellerAirParticle.Factory(SpriteSet animatedSprite) {
        this.spriteSet = animatedSprite;
    }

    public Particle createParticle(PropellerAirParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new PropellerAirParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet, data.enableCollision, data.isVirtual);
    }
}
