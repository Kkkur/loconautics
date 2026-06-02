/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 */
package dev.eriksonn.aeronautics.content.blocks.levitite;

import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.CrystalPropagationContext;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeBlendHelper;
import dev.eriksonn.aeronautics.content.blocks.levitite.LevititeCrystalPropagationContext;
import dev.eriksonn.aeronautics.index.AeroAdvancements;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.eriksonn.aeronautics.index.AeroSoundEvents;
import dev.eriksonn.aeronautics.index.AeroTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class LevititeSoulCrystalPropagationContext
implements CrystalPropagationContext {
    @Override
    public void onCrystallizationInitialize(Level level, BlockPos pos, boolean isDormant) {
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.2f, 1.5f);
        if (!isDormant) {
            LevititeBlendHelper.spawnParticles(level, pos, (ParticleOptions)ParticleTypes.SOUL_FIRE_FLAME, 20);
        }
        LevititeBlendHelper.spawnParticles(level, pos, (ParticleOptions)ParticleTypes.SMOKE, 15);
    }

    @Override
    public void onCrystallize(Level level, BlockPos pos) {
        this.onDefaultCrystallize(level, pos);
        if (!level.isClientSide) {
            AeroSoundEvents.LEVITITE_BLEND_CRYSTALLIZE.play(level, null, (Vec3i)pos, 1.0f, 1.0f);
            LevititeBlendHelper.spawnParticles(level, pos, (ParticleOptions)ParticleTypes.SOUL_FIRE_FLAME, 30);
            AeroAdvancements.NOW_AVAILABLE_IN_PINK.awardToNearby(pos, level);
        }
    }

    @Override
    public void onCrystallizationFail(Level level, BlockPos pos, int attempts, boolean isDormant) {
        LevititeBlendHelper.spawnParticles(level, pos, (ParticleOptions)ParticleTypes.SMOKE, 15);
    }

    @Override
    public BlockState getCrystalBlockState(Level level, BlockPos pos) {
        return AeroBlocks.PEARLESCENT_LEVITITE.getDefaultState();
    }

    @Override
    public boolean canSpreadTo(FluidState state) {
        return state.is(LevititeBlendHelper.getFluid());
    }

    @Override
    public CrystalPropagationContext getContextForSpread(Level level, BlockPos pos) {
        return LevititeCrystalPropagationContext.getRandomContext(this, level, pos);
    }

    @Override
    public TagKey<Block> getCatalyzerTag() {
        return AeroTags.BlockTags.LEVITITE_SOUL_CATALYZER;
    }
}
