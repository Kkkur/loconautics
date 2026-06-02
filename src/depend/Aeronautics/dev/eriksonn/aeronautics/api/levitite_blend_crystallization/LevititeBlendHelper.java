/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.processing.burner.BlazeBurnerBlock
 *  com.simibubi.create.content.processing.burner.BlazeBurnerBlock$HeatLevel
 *  com.simibubi.create.content.processing.burner.LitBlazeBurnerBlock
 *  com.simibubi.create.content.processing.burner.LitBlazeBurnerBlock$FlameType
 *  foundry.veil.platform.registry.RegistryObject
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 */
package dev.eriksonn.aeronautics.api.levitite_blend_crystallization;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.LitBlazeBurnerBlock;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.CrystalPropagationContext;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeBlendDummyInterface;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeCrystallizerManager;
import dev.eriksonn.aeronautics.index.AeroLevititeBlendPropagationContexts;
import dev.eriksonn.aeronautics.index.AeroRegistries;
import dev.eriksonn.aeronautics.service.AeroLevititeService;
import foundry.veil.platform.registry.RegistryObject;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class LevititeBlendHelper {
    public static Fluid getFluid() {
        return AeroLevititeService.INSTANCE.getFluid();
    }

    public static BlockState crystallizeLevititeBlend(Level level, BlockPos pos, CrystalPropagationContext context) {
        context.onCrystallize(level, pos);
        if (!level.isClientSide) {
            LevititeBlendHelper.updateSurroundingLevititeBlend(level, pos, context);
        }
        return context.getCrystalBlockState(level, pos);
    }

    public static void updateSurroundingLevititeBlend(Level pLevel, BlockPos pPos, CrystalPropagationContext context) {
        for (Direction direction : Direction.values()) {
            BlockPos newPos = pPos.relative(direction);
            FluidState state = pLevel.getFluidState(newPos);
            if (!(state.getType() instanceof LevititeBlendDummyInterface) || !context.canSpreadTo(state)) continue;
            LevititeBlendHelper.addLevititeBlendTicker(pLevel, newPos, false, false, context.getContextForSpread(pLevel, newPos));
        }
    }

    public static void checkSurroundingSources(Level level, BlockPos pos, FluidState state) {
        if (state.getType() instanceof LevititeBlendDummyInterface && state.isSource()) {
            for (Direction direction : Direction.values()) {
                BlockPos blockpos = pos.relative(direction);
                CrystalPropagationContext context = LevititeBlendHelper.getContextFromBlock(level, blockpos);
                if (context == null) continue;
                LevititeBlendHelper.addLevititeBlendTicker(level, pos, true, true, context);
                break;
            }
        }
    }

    public static void spawnParticles(Level pLevel, BlockPos pPos, ParticleOptions type, int count) {
        if (!pLevel.isClientSide) {
            double d0 = 0.5625;
            RandomSource random = pLevel.random;
            ServerLevel serverLevel = (ServerLevel)pLevel;
            serverLevel.sendParticles(type, (double)pPos.getX() + 0.5, (double)pPos.getY() + 0.5, (double)pPos.getZ() + 0.5, count, 0.3, 0.3, 0.3, 0.0);
        }
    }

    public static void addLevititeBlendTicker(Level level, BlockPos pPos, boolean requiresCatalyst, boolean isDormant, CrystalPropagationContext context) {
        if (!level.isClientSide) {
            LevititeCrystallizerManager.addTicker(level, pPos, context.getNewAge(level, 0, isDormant), requiresCatalyst, isDormant, context);
        }
    }

    public static CrystalPropagationContext getContextFromBlock(Level pLevel, BlockPos pPos) {
        BlockState state = pLevel.getBlockState(pPos);
        CrystalPropagationContext standardContext = (CrystalPropagationContext)AeroLevititeBlendPropagationContexts.STANDARD_CONTEXT.get();
        CrystalPropagationContext soulContext = (CrystalPropagationContext)AeroLevititeBlendPropagationContexts.SOUL_CONTEXT.get();
        if (state.getBlock() instanceof BlazeBurnerBlock && ((BlazeBurnerBlock.HeatLevel)state.getValue((Property)BlazeBurnerBlock.HEAT_LEVEL)).isAtLeast(BlazeBurnerBlock.HeatLevel.SMOULDERING)) {
            return standardContext;
        }
        if (state.getBlock() instanceof LitBlazeBurnerBlock) {
            return state.getValue((Property)LitBlazeBurnerBlock.FLAME_TYPE) == LitBlazeBurnerBlock.FlameType.REGULAR ? standardContext : soulContext;
        }
        Optional litState = state.getOptionalValue((Property)BlockStateProperties.LIT);
        if (litState.isPresent() && !((Boolean)litState.get()).booleanValue()) {
            return null;
        }
        for (RegistryObject entry : AeroRegistries.LEVITITE_CRYSTAL_PROPAGATION_CONTEXT.getEntries()) {
            CrystalPropagationContext context = (CrystalPropagationContext)entry.get();
            if (!state.is(context.getCatalyzerTag())) continue;
            return context;
        }
        return null;
    }
}
