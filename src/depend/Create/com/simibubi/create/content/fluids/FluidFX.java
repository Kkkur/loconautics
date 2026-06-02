/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.fluids.particle.FluidParticleData;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidFX {
    static RandomSource r = RandomSource.create();

    public static void splash(BlockPos pos, FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        if (fluid == Fluids.EMPTY) {
            return;
        }
        FluidState defaultState = fluid.defaultFluidState();
        if (defaultState == null || defaultState.isEmpty()) {
            return;
        }
        BlockParticleOption blockParticleData = new BlockParticleOption(ParticleTypes.BLOCK, defaultState.createLegacyBlock());
        Vec3 center = VecHelper.getCenterOf((Vec3i)pos);
        for (int i = 0; i < 20; ++i) {
            Vec3 v = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)r, (float)0.25f);
            FluidFX.particle((ParticleOptions)blockParticleData, center.add(v), v);
        }
    }

    public static ParticleOptions getFluidParticle(FluidStack fluid) {
        return new FluidParticleData(AllParticleTypes.FLUID_PARTICLE.get(), fluid);
    }

    public static ParticleOptions getDrippingParticle(FluidStack fluid) {
        Object particle = null;
        if (FluidHelper.isWater(fluid.getFluid())) {
            particle = ParticleTypes.DRIPPING_WATER;
        }
        if (FluidHelper.isLava(fluid.getFluid())) {
            particle = ParticleTypes.DRIPPING_LAVA;
        }
        if (particle == null) {
            particle = new FluidParticleData(AllParticleTypes.FLUID_DRIP.get(), fluid);
        }
        return particle;
    }

    public static void spawnRimParticles(Level world, BlockPos pos, Direction side, int amount, ParticleOptions particle, float rimRadius) {
        Vec3 directionVec = Vec3.atLowerCornerOf((Vec3i)side.getNormal());
        for (int i = 0; i < amount; ++i) {
            Vec3 vec = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)r, (float)1.0f).normalize();
            vec = VecHelper.clampComponentWise((Vec3)vec, (float)rimRadius).multiply(VecHelper.axisAlingedPlaneOf((Vec3)directionVec)).add(directionVec.scale(0.45 + (double)(r.nextFloat() / 16.0f)));
            Vec3 m = vec.scale((double)0.05f);
            vec = vec.add(VecHelper.getCenterOf((Vec3i)pos));
            world.addAlwaysVisibleParticle(particle, vec.x, vec.y - 0.0625, vec.z, m.x, m.y, m.z);
        }
    }

    public static void spawnPouringLiquid(Level world, BlockPos pos, int amount, ParticleOptions particle, float rimRadius, Vec3 directionVec, boolean inbound) {
        for (int i = 0; i < amount; ++i) {
            Vec3 vec = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)r, (float)(rimRadius * 0.75f));
            vec = vec.multiply(VecHelper.axisAlingedPlaneOf((Vec3)directionVec)).add(directionVec.scale(0.5 + (double)(r.nextFloat() / 4.0f)));
            Vec3 m = vec.scale(0.25);
            Vec3 centerOf = VecHelper.getCenterOf((Vec3i)pos);
            vec = vec.add(centerOf);
            if (inbound) {
                vec = vec.add(m);
                m = centerOf.add(directionVec.scale(0.5)).subtract(vec).scale(0.0625);
            }
            world.addAlwaysVisibleParticle(particle, vec.x, vec.y - 0.0625, vec.z, m.x, m.y, m.z);
        }
    }

    private static void particle(ParticleOptions data, Vec3 pos, Vec3 motion) {
        FluidFX.world().addParticle(data, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
    }

    private static Level world() {
        return Minecraft.getInstance().level;
    }
}
