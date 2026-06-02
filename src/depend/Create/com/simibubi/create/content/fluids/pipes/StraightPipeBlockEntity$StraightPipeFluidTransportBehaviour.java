/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.fluids.pipes;

import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.AxisPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.GlassFluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.IAxisPipe;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public static class StraightPipeBlockEntity.StraightPipeFluidTransportBehaviour
extends FluidTransportBehaviour {
    public StraightPipeBlockEntity.StraightPipeFluidTransportBehaviour(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public boolean canHaveFlowToward(BlockState state, Direction direction) {
        return state.hasProperty((Property)AxisPipeBlock.AXIS) && state.getValue((Property)AxisPipeBlock.AXIS) == direction.getAxis();
    }

    @Override
    public FluidTransportBehaviour.AttachmentTypes getRenderedRimAttachment(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        FluidTransportBehaviour.AttachmentTypes attachment = super.getRenderedRimAttachment(world, pos, state, direction);
        BlockState otherState = world.getBlockState(pos.relative(direction));
        Direction.Axis axis = IAxisPipe.getAxisOf(state);
        Direction.Axis otherAxis = IAxisPipe.getAxisOf(otherState);
        if (attachment == FluidTransportBehaviour.AttachmentTypes.RIM && state.getBlock() instanceof FluidValveBlock) {
            return FluidTransportBehaviour.AttachmentTypes.NONE;
        }
        if (attachment == FluidTransportBehaviour.AttachmentTypes.RIM && !(state.getBlock() instanceof GlassFluidPipeBlock) && otherState.getBlock() instanceof GlassFluidPipeBlock) {
            return FluidTransportBehaviour.AttachmentTypes.PARTIAL_RIM;
        }
        if (attachment == FluidTransportBehaviour.AttachmentTypes.RIM && FluidPipeBlock.isPipe(otherState)) {
            return FluidTransportBehaviour.AttachmentTypes.NONE;
        }
        if (axis == otherAxis && axis != null) {
            return FluidTransportBehaviour.AttachmentTypes.NONE;
        }
        if (otherState.getBlock() instanceof FluidValveBlock && FluidValveBlock.getPipeAxis(otherState) == direction.getAxis()) {
            return FluidTransportBehaviour.AttachmentTypes.NONE;
        }
        return attachment.withoutConnector();
    }
}
