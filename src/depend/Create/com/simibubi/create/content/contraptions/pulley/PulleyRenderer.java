/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.contraptions.pulley;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.pulley.AbstractPulleyRenderer;
import com.simibubi.create.content.contraptions.pulley.PulleyBlock;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.content.contraptions.pulley.PulleyContraption;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class PulleyRenderer
extends AbstractPulleyRenderer<PulleyBlockEntity> {
    public PulleyRenderer(BlockEntityRendererProvider.Context context) {
        super(context, AllPartialModels.ROPE_HALF, AllPartialModels.ROPE_HALF_MAGNET);
    }

    @Override
    protected Direction.Axis getShaftAxis(PulleyBlockEntity be) {
        return (Direction.Axis)be.getBlockState().getValue(PulleyBlock.HORIZONTAL_AXIS);
    }

    @Override
    protected PartialModel getCoil() {
        return AllPartialModels.ROPE_COIL;
    }

    @Override
    protected SuperByteBuffer renderRope(PulleyBlockEntity be) {
        return CachedBuffers.block((BlockState)AllBlocks.ROPE.getDefaultState());
    }

    @Override
    protected SuperByteBuffer renderMagnet(PulleyBlockEntity be) {
        return CachedBuffers.block((BlockState)AllBlocks.PULLEY_MAGNET.getDefaultState());
    }

    @Override
    protected float getOffset(PulleyBlockEntity be, float partialTicks) {
        return PulleyRenderer.getBlockEntityOffset(partialTicks, be);
    }

    @Override
    protected boolean isRunning(PulleyBlockEntity be) {
        return PulleyRenderer.isPulleyRunning(be);
    }

    public static boolean isPulleyRunning(PulleyBlockEntity be) {
        return be.running || be.mirrorParent != null || be.isVirtual();
    }

    @Override
    protected SpriteShiftEntry getCoilShift() {
        return AllSpriteShifts.ROPE_PULLEY_COIL;
    }

    public static float getBlockEntityOffset(float partialTicks, PulleyBlockEntity blockEntity) {
        float offset = blockEntity.getInterpolatedOffset(partialTicks);
        AbstractContraptionEntity attachedContraption = blockEntity.getAttachedContraption();
        if (attachedContraption != null) {
            PulleyContraption c = (PulleyContraption)attachedContraption.getContraption();
            double entityPos = Mth.lerp((double)partialTicks, (double)attachedContraption.yOld, (double)attachedContraption.getY());
            offset = (float)(-(entityPos - (double)c.anchor.getY() - (double)c.getInitialOffset()));
        }
        return offset;
    }
}
