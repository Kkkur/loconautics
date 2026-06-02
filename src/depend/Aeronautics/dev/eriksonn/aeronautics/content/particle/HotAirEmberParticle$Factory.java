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

import dev.eriksonn.aeronautics.content.particle.HotAirEmberParticle;
import dev.eriksonn.aeronautics.content.particle.HotAirEmberParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public static class HotAirEmberParticle.Factory
implements ParticleProvider<HotAirEmberParticleData> {
    private final SpriteSet spriteSet;

    public HotAirEmberParticle.Factory(SpriteSet animatedSprite) {
        this.spriteSet = animatedSprite;
    }

    public Particle createParticle(HotAirEmberParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        HotAirEmberParticle particle = new HotAirEmberParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.isSoul);
        particle.setSprite(this.spriteSet.get(0, 1));
        return particle;
    }
}
