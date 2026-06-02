/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.animal.horse.Horse
 *  net.minecraft.world.entity.animal.horse.SkeletonHorse
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.CampfireBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.fan.processing;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.burner.LitBlazeBurnerBlock;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import java.util.List;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public static class AllFanProcessingTypes.HauntingType
implements FanProcessingType {
    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);
        if (AllTags.AllFluidTags.FAN_PROCESSING_CATALYSTS_HAUNTING.matches(fluidState)) {
            return true;
        }
        BlockState blockState = level.getBlockState(pos);
        if (AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_HAUNTING.matches(blockState)) {
            if (blockState.is(BlockTags.CAMPFIRES) && blockState.hasProperty((Property)CampfireBlock.LIT) && !((Boolean)blockState.getValue((Property)CampfireBlock.LIT)).booleanValue()) {
                return false;
            }
            return !blockState.hasProperty(LitBlazeBurnerBlock.FLAME_TYPE) || blockState.getValue(LitBlazeBurnerBlock.FLAME_TYPE) == LitBlazeBurnerBlock.FlameType.SOUL;
        }
        return false;
    }

    @Override
    public int getPriority() {
        return 300;
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        return AllRecipeTypes.HAUNTING.find(new SingleRecipeInput(stack), level).isPresent();
    }

    @Override
    @Nullable
    public List<ItemStack> process(ItemStack stack, Level level) {
        return AllRecipeTypes.HAUNTING.find(new SingleRecipeInput(stack), level).map(RecipeHolder::value).map(r -> RecipeApplier.applyRecipeOn(level, stack, r, true)).orElse(null);
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) != 0) {
            return;
        }
        pos = pos.add(VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)level.random, (float)1.0f).multiply(1.0, (double)0.05f, 1.0).normalize().scale((double)0.15f));
        level.addParticle((ParticleOptions)ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y + (double)0.45f, pos.z, 0.0, 0.0, 0.0);
        if (level.random.nextInt(2) == 0) {
            level.addParticle((ParticleOptions)ParticleTypes.SMOKE, pos.x, pos.y + 0.25, pos.z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void morphAirFlow(FanProcessingType.AirFlowParticleAccess particleAccess, RandomSource random) {
        particleAccess.setColor(Color.mixColors((int)0, (int)1205608, (float)random.nextFloat()));
        particleAccess.setAlpha(1.0f);
        if (random.nextFloat() < 0.0078125f) {
            particleAccess.spawnExtraParticle((ParticleOptions)ParticleTypes.SOUL_FIRE_FLAME, 0.125f);
        }
        if (random.nextFloat() < 0.03125f) {
            particleAccess.spawnExtraParticle((ParticleOptions)ParticleTypes.SMOKE, 0.125f);
        }
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide) {
            if (entity instanceof Horse) {
                Vec3 p = entity.getPosition(0.0f);
                Vec3 v = p.add(0.0, 0.5, 0.0).add(VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)level.random, (float)1.0f).multiply(1.0, (double)0.2f, 1.0).normalize().scale(1.0));
                level.addParticle((ParticleOptions)ParticleTypes.SOUL_FIRE_FLAME, v.x, v.y, v.z, 0.0, (double)0.1f, 0.0);
                if (level.random.nextInt(3) == 0) {
                    level.addParticle((ParticleOptions)ParticleTypes.LARGE_SMOKE, p.x, p.y + 0.5, p.z, (double)((level.random.nextFloat() - 0.5f) * 0.5f), (double)0.1f, (double)((level.random.nextFloat() - 0.5f) * 0.5f));
                }
            }
            return;
        }
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 30, 0, false, false));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1, false, false));
        }
        if (entity instanceof Horse) {
            Horse horse = (Horse)entity;
            int progress = horse.getPersistentData().getInt("CreateHaunting");
            if (progress < 100) {
                if (progress % 10 == 0) {
                    level.playSound(null, entity.blockPosition(), (SoundEvent)SoundEvents.SOUL_ESCAPE.value(), SoundSource.NEUTRAL, 1.0f, 1.5f * (float)progress / 100.0f);
                }
                horse.getPersistentData().putInt("CreateHaunting", progress + 1);
                return;
            }
            level.playSound(null, entity.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.NEUTRAL, 1.25f, 0.65f);
            SkeletonHorse skeletonHorse = (SkeletonHorse)EntityType.SKELETON_HORSE.create(level);
            CompoundTag serializeNBT = horse.saveWithoutId(new CompoundTag());
            serializeNBT.remove("UUID");
            if (!horse.getBodyArmorItem().isEmpty()) {
                horse.spawnAtLocation(horse.getBodyArmorItem());
            }
            skeletonHorse.deserializeNBT((HolderLookup.Provider)entity.registryAccess(), serializeNBT);
            skeletonHorse.setPos(horse.getPosition(0.0f));
            level.addFreshEntity((Entity)skeletonHorse);
            horse.discard();
        }
    }
}
