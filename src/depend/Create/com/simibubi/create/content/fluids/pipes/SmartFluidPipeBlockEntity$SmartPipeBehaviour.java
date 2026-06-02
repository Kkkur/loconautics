/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids.pipes;

import com.simibubi.create.content.fluids.pipes.SmartFluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

class SmartFluidPipeBlockEntity.SmartPipeBehaviour
extends StraightPipeBlockEntity.StraightPipeFluidTransportBehaviour {
    public SmartFluidPipeBlockEntity.SmartPipeBehaviour(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public boolean canPullFluidFrom(FluidStack fluid, BlockState state, Direction direction) {
        if (fluid.isEmpty() || SmartFluidPipeBlockEntity.this.filter != null && SmartFluidPipeBlockEntity.this.filter.test(fluid)) {
            return super.canPullFluidFrom(fluid, state, direction);
        }
        return false;
    }

    @Override
    public boolean canHaveFlowToward(BlockState state, Direction direction) {
        return state.getBlock() instanceof SmartFluidPipeBlock && SmartFluidPipeBlock.getPipeAxis(state) == direction.getAxis();
    }
}
