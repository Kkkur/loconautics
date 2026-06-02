/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids.pipes.valve;

import com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlock;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.fluids.FluidStack;

class FluidValveBlockEntity.ValvePipeBehaviour
extends StraightPipeBlockEntity.StraightPipeFluidTransportBehaviour {
    public FluidValveBlockEntity.ValvePipeBehaviour(FluidValveBlockEntity this$0, SmartBlockEntity be) {
        super(be);
    }

    @Override
    public boolean canHaveFlowToward(BlockState state, Direction direction) {
        return FluidValveBlock.getPipeAxis(state) == direction.getAxis();
    }

    @Override
    public boolean canPullFluidFrom(FluidStack fluid, BlockState state, Direction direction) {
        if (state.hasProperty((Property)FluidValveBlock.ENABLED) && ((Boolean)state.getValue((Property)FluidValveBlock.ENABLED)).booleanValue()) {
            return super.canPullFluidFrom(fluid, state, direction);
        }
        return false;
    }
}
