/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.redstone.deskBell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.redstone.deskBell.DeskBellBlock;
import com.simibubi.create.content.redstone.deskBell.DeskBellBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class DeskBellRenderer
extends SmartBlockEntityRenderer<DeskBellBlockEntity> {
    public DeskBellRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(DeskBellBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = blockEntity.getBlockState();
        float p = blockEntity.animation.getValue(partialTicks);
        if ((double)p < 0.004 && !blockState.getOptionalValue((Property)DeskBellBlock.POWERED).orElse(false).booleanValue()) {
            return;
        }
        float f = (float)(1.0 - 4.0 * Math.pow(Math.max((double)p - 0.5, 0.0) - 0.5, 2.0));
        float f2 = (float)Math.pow(p, 1.25);
        Direction facing = (Direction)blockState.getValue((Property)DeskBellBlock.FACING);
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.DESK_BELL_PLUNGER, (BlockState)blockState).center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing))).rotateXDegrees(AngleHelper.verticalAngle((Direction)facing) + 90.0f)).uncenter()).translate(0.0f, f * -0.75f / 16.0f, 0.0f)).light(light).overlay(overlay).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.DESK_BELL_BELL, (BlockState)blockState).center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing))).rotateXDegrees(AngleHelper.verticalAngle((Direction)facing) + 90.0f)).translate(0.0f, 0.0f, 0.0f)).rotateXDegrees(f2 * 8.0f * Mth.sin((float)(p * (float)Math.PI * 4.0f + blockEntity.animationOffset)))).rotateZDegrees(f2 * 8.0f * Mth.cos((float)(p * (float)Math.PI * 4.0f + blockEntity.animationOffset)))).translate(0.0f, 0.0f, 0.0f)).scale(0.995f)).uncenter()).light(light).overlay(overlay).renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }
}
