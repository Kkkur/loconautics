/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity
 *  dev.ryanhcode.sable.util.LevelAccelerator
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.ryanhcode.offroad.handlers.client;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import dev.ryanhcode.offroad.handlers.MultiminingDataTickResult;
import dev.ryanhcode.sable.util.LevelAccelerator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public static class MultiMiningClientHandler.ClientBlockBreakingData {
    public boolean invalid;
    public float destroyProgress;

    public MultiminingDataTickResult tick(Level level, LevelAccelerator accelerator, BlockPos pos, int blocksBeingBroken) {
        BlockState state = accelerator.getBlockState(pos);
        if (!BlockBreakingKineticBlockEntity.isBreakable((BlockState)state, (float)state.getDestroySpeed((BlockGetter)level, pos)) || this.invalid || this.destroyProgress >= 10.0f || this.destroyProgress < 0.0f) {
            this.destroyProgress = -1.0f;
            return MultiminingDataTickResult.BROKEN;
        }
        ClientLevel clientLevel = (ClientLevel)level;
        double radius = 0.5;
        if (accelerator.getBlockState(pos.below()).isAir()) {
            if ((double)level.random.nextFloat() > 0.8) {
                BlockParticleOption blockBreakingParticles = new BlockParticleOption(ParticleTypes.BLOCK, state);
                clientLevel.addParticle((ParticleOptions)blockBreakingParticles, (double)pos.getX() + 0.5 + ((double)level.random.nextFloat() - 0.5) * 2.0 * 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5 + ((double)level.random.nextFloat() - 0.5) * 2.0 * 0.5, 0.0, -1.0, 0.0);
                if ((double)level.random.nextFloat() > 0.8) {
                    clientLevel.addParticle((ParticleOptions)ParticleTypes.ASH, (double)pos.getX() + 0.5 + ((double)level.random.nextFloat() - 0.5) * 2.0 * 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5 + ((double)level.random.nextFloat() - 0.5) * 2.0 * 0.5, 0.0, -1.0, 0.0);
                }
            }
        } else if (accelerator.getBlockState(pos.above()).isAir() && (double)level.random.nextFloat() > 0.9 && (double)level.random.nextFloat() > 0.5) {
            clientLevel.addParticle((ParticleOptions)ParticleTypes.CRIT, (double)pos.getX() + 0.5 + ((double)level.random.nextFloat() - 0.5) * 2.0 * 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5 + ((double)level.random.nextFloat() - 0.5) * 2.0 * 0.5, 0.0, 0.0, 0.0);
        }
        double chance = 1.0 - 0.4 / Math.sqrt(Math.pow(blocksBeingBroken, 2.0));
        if (soundCount < 64 && (double)level.random.nextFloat() > chance) {
            ++soundCount;
            clientLevel.playLocalSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, state.getSoundType().getHitSound(), SoundSource.BLOCKS, 0.4f, 0.1f, false);
        }
        return MultiminingDataTickResult.CONTINUE;
    }
}
