/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.fluids.hosePulley;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.pulley.AbstractPulleyRenderer;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyBlock;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyBlockEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class HosePulleyRenderer
extends AbstractPulleyRenderer<HosePulleyBlockEntity> {
    public HosePulleyRenderer(BlockEntityRendererProvider.Context context) {
        super(context, AllPartialModels.HOSE_HALF, AllPartialModels.HOSE_HALF_MAGNET);
    }

    @Override
    protected Direction.Axis getShaftAxis(HosePulleyBlockEntity be) {
        return ((Direction)be.getBlockState().getValue(HosePulleyBlock.HORIZONTAL_FACING)).getClockWise().getAxis();
    }

    @Override
    protected PartialModel getCoil() {
        return AllPartialModels.HOSE_COIL;
    }

    @Override
    protected SuperByteBuffer renderRope(HosePulleyBlockEntity be) {
        return CachedBuffers.partial((PartialModel)AllPartialModels.HOSE, (BlockState)be.getBlockState());
    }

    @Override
    protected SuperByteBuffer renderMagnet(HosePulleyBlockEntity be) {
        return CachedBuffers.partial((PartialModel)AllPartialModels.HOSE_MAGNET, (BlockState)be.getBlockState());
    }

    @Override
    protected float getOffset(HosePulleyBlockEntity be, float partialTicks) {
        return be.getInterpolatedOffset(partialTicks);
    }

    @Override
    protected SpriteShiftEntry getCoilShift() {
        return AllSpriteShifts.HOSE_PULLEY_COIL;
    }

    @Override
    protected boolean isRunning(HosePulleyBlockEntity be) {
        return true;
    }
}
