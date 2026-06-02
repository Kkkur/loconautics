/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.fluids.pipes;

import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.content.fluids.pipes.GlassFluidPipeBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

class FluidPipeBlockEntity.StandardPipeFluidTransportBehaviour
extends FluidTransportBehaviour {
    public FluidPipeBlockEntity.StandardPipeFluidTransportBehaviour(FluidPipeBlockEntity this$0, SmartBlockEntity be) {
        super(be);
    }

    @Override
    public boolean canHaveFlowToward(BlockState state, Direction direction) {
        return (FluidPipeBlock.isPipe(state) || state.getBlock() instanceof EncasedPipeBlock) && (Boolean)state.getValue((Property)FluidPipeBlock.PROPERTY_BY_DIRECTION.get(direction)) != false;
    }

    @Override
    public FluidTransportBehaviour.AttachmentTypes getRenderedRimAttachment(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        FluidTransportBehaviour.AttachmentTypes attachment = super.getRenderedRimAttachment(world, pos, state, direction);
        BlockPos offsetPos = pos.relative(direction);
        BlockState otherState = world.getBlockState(offsetPos);
        if (state.getBlock() instanceof EncasedPipeBlock && attachment != FluidTransportBehaviour.AttachmentTypes.DRAIN) {
            return FluidTransportBehaviour.AttachmentTypes.NONE;
        }
        if (attachment == FluidTransportBehaviour.AttachmentTypes.RIM) {
            FluidTransportBehaviour pipeBehaviour;
            if (!FluidPipeBlock.isPipe(otherState) && !(otherState.getBlock() instanceof EncasedPipeBlock) && !(otherState.getBlock() instanceof GlassFluidPipeBlock) && (pipeBehaviour = BlockEntityBehaviour.get((BlockGetter)world, offsetPos, FluidTransportBehaviour.TYPE)) != null && pipeBehaviour.canHaveFlowToward(otherState, direction.getOpposite())) {
                return FluidTransportBehaviour.AttachmentTypes.DETAILED_CONNECTION;
            }
            if (!FluidPipeBlock.shouldDrawRim(world, pos, state, direction)) {
                return FluidPropagator.getStraightPipeAxis(state) == direction.getAxis() ? FluidTransportBehaviour.AttachmentTypes.CONNECTION : FluidTransportBehaviour.AttachmentTypes.DETAILED_CONNECTION;
            }
        }
        if (attachment == FluidTransportBehaviour.AttachmentTypes.NONE && ((Boolean)state.getValue((Property)FluidPipeBlock.PROPERTY_BY_DIRECTION.get(direction))).booleanValue()) {
            return FluidTransportBehaviour.AttachmentTypes.DETAILED_CONNECTION;
        }
        return attachment;
    }
}
