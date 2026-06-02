/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.AbstractCookingRecipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.item.crafting.SmokingRecipe
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.fan.processing;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public static class AllFanProcessingTypes.BlastingType
implements FanProcessingType {
    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);
        if (AllTags.AllFluidTags.FAN_PROCESSING_CATALYSTS_BLASTING.matches(fluidState)) {
            return true;
        }
        BlockState blockState = level.getBlockState(pos);
        if (AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_BLASTING.matches(blockState)) {
            return !blockState.hasProperty(BlazeBurnerBlock.HEAT_LEVEL) || ((BlazeBurnerBlock.HeatLevel)((Object)blockState.getValue(BlazeBurnerBlock.HEAT_LEVEL))).isAtLeast(BlazeBurnerBlock.HeatLevel.FADING);
        }
        return false;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        Optional<RecipeHolder<?>> smeltingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, (RecipeInput)new SingleRecipeInput(stack), level).filter(AllRecipeTypes.CAN_BE_AUTOMATED);
        if (smeltingRecipe.isPresent()) {
            return true;
        }
        Optional<RecipeHolder<?>> blastingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, (RecipeInput)new SingleRecipeInput(stack), level).filter(AllRecipeTypes.CAN_BE_AUTOMATED);
        if (blastingRecipe.isPresent()) {
            return true;
        }
        return !stack.has(DataComponents.FIRE_RESISTANT);
    }

    @Override
    @Nullable
    public List<ItemStack> process(ItemStack stack, Level level) {
        Optional<RecipeHolder<?>> smokingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, (RecipeInput)new SingleRecipeInput(stack), level).filter(AllRecipeTypes.CAN_BE_AUTOMATED);
        Optional<RecipeHolder<?>> smeltingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, (RecipeInput)new SingleRecipeInput(stack), level).filter(AllRecipeTypes.CAN_BE_AUTOMATED);
        if (smeltingRecipe.isEmpty()) {
            smeltingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, (RecipeInput)new SingleRecipeInput(stack), level).filter(AllRecipeTypes.CAN_BE_AUTOMATED);
        }
        if (smeltingRecipe.isPresent()) {
            RegistryAccess registryAccess = level.registryAccess();
            if (smokingRecipe.isEmpty() || !ItemStack.isSameItem((ItemStack)((SmokingRecipe)smokingRecipe.get().value()).getResultItem((HolderLookup.Provider)registryAccess), (ItemStack)((AbstractCookingRecipe)smeltingRecipe.get().value()).getResultItem((HolderLookup.Provider)registryAccess))) {
                return RecipeApplier.applyRecipeOn(level, stack, smeltingRecipe.get().value(), false);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) != 0) {
            return;
        }
        level.addParticle((ParticleOptions)ParticleTypes.LARGE_SMOKE, pos.x, pos.y + 0.25, pos.z, 0.0, 0.0625, 0.0);
    }

    @Override
    public void morphAirFlow(FanProcessingType.AirFlowParticleAccess particleAccess, RandomSource random) {
        particleAccess.setColor(Color.mixColors((int)0xFF4400, (int)0xFF8855, (float)random.nextFloat()));
        particleAccess.setAlpha(0.5f);
        if (random.nextFloat() < 0.03125f) {
            particleAccess.spawnExtraParticle((ParticleOptions)ParticleTypes.FLAME, 0.25f);
        }
        if (random.nextFloat() < 0.0625f) {
            particleAccess.spawnExtraParticle((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, Blocks.LAVA.defaultBlockState()), 0.25f);
        }
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide) {
            return;
        }
        if (!entity.fireImmune()) {
            entity.igniteForSeconds(10.0f);
            entity.hurt(CreateDamageSources.fanLava(level), 4.0f);
        }
    }
}
