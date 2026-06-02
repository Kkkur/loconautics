/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.redstone.diodes;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.redstone.diodes.BrassDiodeBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.ColoredOverlayBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class BrassDiodeRenderer
extends ColoredOverlayBlockEntityRenderer<BrassDiodeBlockEntity> {
    public BrassDiodeRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected int getColor(BrassDiodeBlockEntity be, float partialTicks) {
        return Color.mixColors((int)2884352, (int)0xCD0000, (float)be.getProgress());
    }

    @Override
    protected SuperByteBuffer getOverlayBuffer(BrassDiodeBlockEntity be) {
        return CachedBuffers.partial((PartialModel)AllPartialModels.FLEXPEATER_INDICATOR, (BlockState)be.getBlockState());
    }
}
