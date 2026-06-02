/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LayeredCauldronBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.neoforge.fluids.BaseFlowingFluid$Flowing
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids.pipes;

import com.simibubi.create.AllFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class VanillaFluidTargets {
    public static boolean canProvideFluidWithoutCapability(BlockState state) {
        if (state.hasProperty((Property)BlockStateProperties.LEVEL_HONEY)) {
            return true;
        }
        if (state.is(Blocks.CAULDRON)) {
            return true;
        }
        if (state.is(Blocks.LAVA_CAULDRON)) {
            return true;
        }
        return state.is(Blocks.WATER_CAULDRON);
    }

    public static FluidStack drainBlock(Level level, BlockPos pos, BlockState state, boolean simulate) {
        Block block;
        if (state.hasProperty((Property)BlockStateProperties.LEVEL_HONEY) && (Integer)state.getValue((Property)BlockStateProperties.LEVEL_HONEY) >= 5) {
            if (!simulate) {
                level.setBlock(pos, (BlockState)state.setValue((Property)BlockStateProperties.LEVEL_HONEY, (Comparable)Integer.valueOf(0)), 3);
            }
            return new FluidStack(((BaseFlowingFluid.Flowing)AllFluids.HONEY.get()).getSource(), 250);
        }
        if (state.is(Blocks.LAVA_CAULDRON)) {
            if (!simulate) {
                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
            }
            return new FluidStack((Fluid)Fluids.LAVA, 1000);
        }
        if (state.is(Blocks.WATER_CAULDRON) && (block = state.getBlock()) instanceof LayeredCauldronBlock) {
            LayeredCauldronBlock lcb = (LayeredCauldronBlock)block;
            if (!lcb.isFull(state)) {
                return FluidStack.EMPTY;
            }
            if (!simulate) {
                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
            }
            return new FluidStack((Fluid)Fluids.WATER, 1000);
        }
        return FluidStack.EMPTY;
    }
}
