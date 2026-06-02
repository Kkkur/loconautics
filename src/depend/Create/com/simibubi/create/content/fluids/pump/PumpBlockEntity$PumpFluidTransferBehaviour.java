/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.fluids.pump;

import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import java.util.Map;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

class PumpBlockEntity.PumpFluidTransferBehaviour
extends FluidTransportBehaviour {
    public PumpBlockEntity.PumpFluidTransferBehaviour(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public void tick() {
        super.tick();
        for (Map.Entry entry : this.interfaces.entrySet()) {
            boolean pull = PumpBlockEntity.this.isPullingOnSide(PumpBlockEntity.this.isFront((Direction)entry.getKey()));
            Couple<Float> pressure = ((PipeConnection)entry.getValue()).getPressure();
            pressure.set(pull, (Object)Float.valueOf(Math.abs(PumpBlockEntity.this.getSpeed())));
            pressure.set(!pull, (Object)Float.valueOf(0.0f));
        }
    }

    @Override
    public boolean canHaveFlowToward(BlockState state, Direction direction) {
        return PumpBlockEntity.this.isSideAccessible(direction);
    }

    @Override
    public FluidTransportBehaviour.AttachmentTypes getRenderedRimAttachment(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        FluidTransportBehaviour.AttachmentTypes attachment = super.getRenderedRimAttachment(world, pos, state, direction);
        if (attachment == FluidTransportBehaviour.AttachmentTypes.RIM) {
            return FluidTransportBehaviour.AttachmentTypes.NONE;
        }
        return attachment;
    }
}
