/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.materials;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ExperienceBlock
extends Block {
    public ExperienceBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRand) {
        if (pRand.nextInt(5) != 0) {
            return;
        }
        Vec3 vec3 = VecHelper.clampComponentWise((Vec3)VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)pRand, (float)0.75f), (float)0.55f).add(VecHelper.getCenterOf((Vec3i)pPos));
        pLevel.addParticle((ParticleOptions)ParticleTypes.END_ROD, vec3.x, vec3.y, vec3.z, pRand.nextGaussian() * 0.005, pRand.nextGaussian() * 0.005, pRand.nextGaussian() * 0.005);
    }
}
