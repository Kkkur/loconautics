/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotationIndicatorParticleData;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class KineticEffectHandler {
    int overStressedTime;
    float overStressedEffect;
    int particleSpawnCountdown;
    KineticBlockEntity kte;

    public KineticEffectHandler(KineticBlockEntity kte) {
        this.kte = kte;
    }

    public void tick() {
        Level world = this.kte.getLevel();
        if (world.isClientSide) {
            if (this.overStressedTime > 0 && --this.overStressedTime == 0) {
                if (this.kte.isOverStressed()) {
                    this.overStressedEffect = 1.0f;
                    this.spawnEffect((ParticleOptions)ParticleTypes.SMOKE, 0.2f, 5);
                } else {
                    this.overStressedEffect = -1.0f;
                    this.spawnEffect((ParticleOptions)ParticleTypes.CLOUD, 0.075f, 2);
                }
            }
            if (this.overStressedEffect != 0.0f) {
                this.overStressedEffect -= this.overStressedEffect * 0.1f;
                if (Math.abs(this.overStressedEffect) < 0.0078125f) {
                    this.overStressedEffect = 0.0f;
                }
            }
        } else if (this.particleSpawnCountdown > 0 && --this.particleSpawnCountdown == 0) {
            this.spawnRotationIndicators();
        }
    }

    public void queueRotationIndicators() {
        this.particleSpawnCountdown = 2;
    }

    public void spawnEffect(ParticleOptions particle, float maxMotion, int amount) {
        Level world = this.kte.getLevel();
        if (world == null) {
            return;
        }
        if (!world.isClientSide) {
            return;
        }
        RandomSource r = world.random;
        for (int i = 0; i < amount; ++i) {
            Vec3 motion = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)r, (float)maxMotion);
            Vec3 position = VecHelper.getCenterOf((Vec3i)this.kte.getBlockPos());
            world.addParticle(particle, position.x, position.y, position.z, motion.x, motion.y, motion.z);
        }
    }

    public void spawnRotationIndicators() {
        float speed = this.kte.getSpeed();
        if (speed == 0.0f) {
            return;
        }
        BlockState state = this.kte.getBlockState();
        Block block = state.getBlock();
        if (!(block instanceof KineticBlock)) {
            return;
        }
        KineticBlock kb = (KineticBlock)block;
        float radius1 = kb.getParticleInitialRadius();
        float radius2 = kb.getParticleTargetRadius();
        Direction.Axis axis = kb.getRotationAxis(state);
        BlockPos pos = this.kte.getBlockPos();
        Level world = this.kte.getLevel();
        if (axis == null) {
            return;
        }
        if (world == null) {
            return;
        }
        Vec3 vec = VecHelper.getCenterOf((Vec3i)pos);
        IRotate.SpeedLevel speedLevel = IRotate.SpeedLevel.of(speed);
        int color = speedLevel.getColor();
        int particleSpeed = speedLevel.getParticleSpeed();
        particleSpeed = (int)((float)particleSpeed * Math.signum(speed));
        if (world instanceof ServerLevel) {
            RotationIndicatorParticleData particleData = new RotationIndicatorParticleData(color, particleSpeed, radius1, radius2, 10, axis);
            ((ServerLevel)world).sendParticles((ParticleOptions)particleData, vec.x, vec.y, vec.z, 20, 0.0, 0.0, 0.0, 1.0);
        }
    }

    public void triggerOverStressedEffect() {
        this.overStressedTime = this.overStressedTime == 0 ? 2 : 0;
    }
}
