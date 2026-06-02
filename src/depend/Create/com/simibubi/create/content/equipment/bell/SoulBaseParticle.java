/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.world.level.Level
 *  org.joml.Quaternionf
 */
package com.simibubi.create.content.equipment.bell;

import com.mojang.math.Axis;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.equipment.bell.BasicParticleData;
import com.simibubi.create.content.equipment.bell.CustomRotationParticle;
import com.simibubi.create.content.equipment.bell.SoulPulseEffect;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;

public class SoulBaseParticle
extends CustomRotationParticle {
    private final SpriteSet animatedSprite;

    public SoulBaseParticle(ClientLevel worldIn, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(worldIn, x, y, z, spriteSet, 0.0f);
        this.animatedSprite = spriteSet;
        this.quadSize = 0.5f;
        this.setSize(this.quadSize, this.quadSize);
        this.loopLength = 16 + (int)(this.random.nextFloat() * 2.0f - 1.0f);
        this.lifetime = (int)(90.0f / (this.random.nextFloat() * 0.36f + 0.64f));
        this.selectSpriteLoopingWithAge(this.animatedSprite);
        this.stoppedByCollision = true;
    }

    public void tick() {
        this.selectSpriteLoopingWithAge(this.animatedSprite);
        BlockPos pos = BlockPos.containing((double)this.x, (double)this.y, (double)this.z);
        if (this.age++ >= this.lifetime || !SoulPulseEffect.isDark((Level)this.level, pos)) {
            this.remove();
        }
    }

    @Override
    public Quaternionf getCustomRotation(Camera camera, float partialTicks) {
        return Axis.XP.rotationDegrees(90.0f);
    }

    public static class Data
    extends BasicParticleData<SoulBaseParticle> {
        @Override
        public BasicParticleData.IBasicParticleFactory<SoulBaseParticle> getBasicFactory() {
            return SoulBaseParticle::new;
        }

        public ParticleType<?> getType() {
            return AllParticleTypes.SOUL_BASE.get();
        }
    }
}
