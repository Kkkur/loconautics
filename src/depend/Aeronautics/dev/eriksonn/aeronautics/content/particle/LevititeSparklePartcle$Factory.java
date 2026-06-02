/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.SpriteSet
 *  org.jetbrains.annotations.Nullable
 */
package dev.eriksonn.aeronautics.content.particle;

import dev.eriksonn.aeronautics.content.particle.LevititeSparklePartcle;
import dev.eriksonn.aeronautics.content.particle.LevititeSparklePartcleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import org.jetbrains.annotations.Nullable;

public static class LevititeSparklePartcle.Factory
implements ParticleProvider<LevititeSparklePartcleData> {
    private final SpriteSet spriteSet;

    public LevititeSparklePartcle.Factory(SpriteSet animatedSprite) {
        this.spriteSet = animatedSprite;
    }

    @Nullable
    public Particle createParticle(LevititeSparklePartcleData levititeSparklePartcleData, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
        return new LevititeSparklePartcle(level, x, y, z, dx, dy, dz, this.spriteSet, levititeSparklePartcleData.color);
    }
}
