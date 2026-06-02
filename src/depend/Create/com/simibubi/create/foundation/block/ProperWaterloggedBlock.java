/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 */
package com.simibubi.create.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public interface ProperWaterloggedBlock
extends SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    default public FluidState fluidState(BlockState state) {
        return (Boolean)state.getValue((Property)WATERLOGGED) != false ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    default public void updateWater(LevelAccessor level, BlockState state, BlockPos pos) {
        if (((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)).booleanValue()) {
            level.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader)level));
        }
    }

    default public BlockState withWater(BlockState placementState, BlockPlaceContext ctx) {
        return ProperWaterloggedBlock.withWater((LevelAccessor)ctx.getLevel(), placementState, ctx.getClickedPos());
    }

    public static BlockState withWater(LevelAccessor level, BlockState placementState, BlockPos pos) {
        if (placementState == null) {
            return null;
        }
        FluidState ifluidstate = level.getFluidState(pos);
        if (placementState.isAir()) {
            return ifluidstate.getType() == Fluids.WATER ? ifluidstate.createLegacyBlock() : placementState;
        }
        if (!(placementState.getBlock() instanceof SimpleWaterloggedBlock)) {
            return placementState;
        }
        return (BlockState)placementState.setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(ifluidstate.getType() == Fluids.WATER));
    }
}
