/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeType
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
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.LitBlazeBurnerBlock;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import java.util.List;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public static class AllFanProcessingTypes.SmokingType
implements FanProcessingType {
    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);
        if (AllTags.AllFluidTags.FAN_PROCESSING_CATALYSTS_SMOKING.matches(fluidState)) {
            return true;
        }
        BlockState blockState = level.getBlockState(pos);
        if (AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_SMOKING.matches(blockState)) {
            if (blockState.is(BlockTags.CAMPFIRES) && blockState.hasProperty((Property)CampfireBlock.LIT) && !((Boolean)blockState.getValue((Property)CampfireBlock.LIT)).booleanValue()) {
                return false;
            }
            if (blockState.hasProperty(LitBlazeBurnerBlock.FLAME_TYPE) && blockState.getValue(LitBlazeBurnerBlock.FLAME_TYPE) != LitBlazeBurnerBlock.FlameType.REGULAR) {
                return false;
            }
            return !blockState.hasProperty(BlazeBurnerBlock.HEAT_LEVEL) || blockState.getValue(BlazeBurnerBlock.HEAT_LEVEL) == BlazeBurnerBlock.HeatLevel.SMOULDERING;
        }
        return false;
    }

    @Override
    public int getPriority() {
        return 200;
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        return level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, (RecipeInput)new SingleRecipeInput(stack), level).filter(AllRecipeTypes.CAN_BE_AUTOMATED).isPresent();
    }

    @Override
    @Nullable
    public List<ItemStack> process(ItemStack stack, Level level) {
        return level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, (RecipeInput)new SingleRecipeInput(stack), level).filter(AllRecipeTypes.CAN_BE_AUTOMATED).map(RecipeHolder::value).map(r -> RecipeApplier.applyRecipeOn(level, stack, r, false)).orElse(null);
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) != 0) {
            return;
        }
        level.addParticle((ParticleOptions)ParticleTypes.POOF, pos.x, pos.y + 0.25, pos.z, 0.0, 0.0625, 0.0);
    }

    @Override
    public void morphAirFlow(FanProcessingType.AirFlowParticleAccess particleAccess, RandomSource random) {
        particleAccess.setColor(Color.mixColors((int)0, (int)0x555555, (float)random.nextFloat()));
        particleAccess.setAlpha(1.0f);
        if (random.nextFloat() < 0.03125f) {
            particleAccess.spawnExtraParticle((ParticleOptions)ParticleTypes.SMOKE, 0.125f);
        }
        if (random.nextFloat() < 0.03125f) {
            particleAccess.spawnExtraParticle((ParticleOptions)ParticleTypes.LARGE_SMOKE, 0.125f);
        }
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide) {
            return;
        }
        if (!entity.fireImmune()) {
            entity.igniteForSeconds(2.0f);
            entity.hurt(CreateDamageSources.fanFire(level), 2.0f);
        }
    }
}
