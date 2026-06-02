/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.api.behaviour.spouting;

import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public record StateChangingBehavior(int amount, Predicate<Fluid> fluidTest, Predicate<BlockState> canFill, UnaryOperator<BlockState> fillFunction) implements BlockSpoutingBehaviour
{
    @Override
    public int fillBlock(Level level, BlockPos pos, SpoutBlockEntity spout, FluidStack availableFluid, boolean simulate) {
        if (availableFluid.getAmount() < this.amount || !this.fluidTest.test(availableFluid.getFluid())) {
            return 0;
        }
        BlockState state = level.getBlockState(pos);
        if (!this.canFill.test(state)) {
            return 0;
        }
        if (!simulate) {
            BlockState newState = (BlockState)this.fillFunction.apply(state);
            level.setBlockAndUpdate(pos, newState);
        }
        return this.amount;
    }

    public static BlockSpoutingBehaviour setTo(int amount, Predicate<Fluid> fluidTest, Block block) {
        return StateChangingBehavior.setTo(amount, fluidTest, block.defaultBlockState());
    }

    public static BlockSpoutingBehaviour setTo(int amount, Predicate<Fluid> fluidTest, BlockState newState) {
        return new StateChangingBehavior(amount, fluidTest, state -> true, state -> newState);
    }

    public static BlockSpoutingBehaviour incrementingState(int amount, Predicate<Fluid> fluidTest, IntegerProperty property) {
        int max = (Integer)property.getPossibleValues().stream().max(Integer::compareTo).orElseThrow();
        Predicate<BlockState> canFill = state -> state.hasProperty((Property)property) && (Integer)state.getValue((Property)property) < max;
        UnaryOperator fillFunction = state -> (BlockState)state.setValue((Property)property, (Comparable)Integer.valueOf((Integer)state.getValue((Property)property) + 1));
        return new StateChangingBehavior(amount, fluidTest, canFill, fillFunction);
    }
}
