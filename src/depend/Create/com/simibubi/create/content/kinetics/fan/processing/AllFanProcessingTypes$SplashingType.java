/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.monster.EnderMan
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package com.simibubi.create.content.kinetics.fan.processing;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public static class AllFanProcessingTypes.SplashingType
implements FanProcessingType {
    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);
        if (AllTags.AllFluidTags.FAN_PROCESSING_CATALYSTS_SPLASHING.matches(fluidState)) {
            return true;
        }
        BlockState blockState = level.getBlockState(pos);
        return AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_SPLASHING.matches(blockState);
    }

    @Override
    public int getPriority() {
        return 400;
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        return AllRecipeTypes.SPLASHING.find(new SingleRecipeInput(stack), level).isPresent();
    }

    @Override
    @Nullable
    public List<ItemStack> process(ItemStack stack, Level level) {
        Optional recipe = AllRecipeTypes.SPLASHING.find(new SingleRecipeInput(stack), level);
        return AllRecipeTypes.SPLASHING.find(new SingleRecipeInput(stack), level).map(RecipeHolder::value).map(r -> RecipeApplier.applyRecipeOn(level, stack, r, true)).orElse(null);
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) != 0) {
            return;
        }
        Vector3f color = new Color(22015).asVectorF();
        level.addParticle((ParticleOptions)new DustParticleOptions(color, 1.0f), pos.x + (double)((level.random.nextFloat() - 0.5f) * 0.5f), pos.y + 0.5, pos.z + (double)((level.random.nextFloat() - 0.5f) * 0.5f), 0.0, 0.125, 0.0);
        level.addParticle((ParticleOptions)ParticleTypes.SPIT, pos.x + (double)((level.random.nextFloat() - 0.5f) * 0.5f), pos.y + 0.5, pos.z + (double)((level.random.nextFloat() - 0.5f) * 0.5f), 0.0, 0.125, 0.0);
    }

    @Override
    public void morphAirFlow(FanProcessingType.AirFlowParticleAccess particleAccess, RandomSource random) {
        particleAccess.setColor(Color.mixColors((int)0x4499FF, (int)0x2277FF, (float)random.nextFloat()));
        particleAccess.setAlpha(1.0f);
        if (random.nextFloat() < 0.03125f) {
            particleAccess.spawnExtraParticle((ParticleOptions)ParticleTypes.BUBBLE, 0.125f);
        }
        if (random.nextFloat() < 0.03125f) {
            particleAccess.spawnExtraParticle((ParticleOptions)ParticleTypes.BUBBLE_POP, 0.125f);
        }
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide) {
            return;
        }
        if (entity instanceof EnderMan || entity.getType() == EntityType.SNOW_GOLEM || entity.getType() == EntityType.BLAZE) {
            entity.hurt(entity.damageSources().drown(), 2.0f);
        }
        if (entity.isOnFire()) {
            entity.clearFire();
            level.playSound(null, entity.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.NEUTRAL, 0.7f, 1.6f + (level.random.nextFloat() - level.random.nextFloat()) * 0.4f);
        }
    }
}
