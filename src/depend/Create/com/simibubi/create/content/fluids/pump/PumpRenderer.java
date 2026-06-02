/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.fluids.pump;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class PumpRenderer
extends KineticBlockEntityRenderer<PumpBlockEntity> {
    public PumpRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(PumpBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacing((PartialModel)AllPartialModels.MECHANICAL_PUMP_COG, (BlockState)state);
    }
}
