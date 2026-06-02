/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.decoration.steamWhistle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlock;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class WhistleRenderer
extends SafeBlockEntityRenderer<WhistleBlockEntity> {
    public WhistleRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(WhistleBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        if (!(blockState.getBlock() instanceof WhistleBlock)) {
            return;
        }
        Direction direction = (Direction)blockState.getValue((Property)WhistleBlock.FACING);
        WhistleBlock.WhistleSize size = (WhistleBlock.WhistleSize)((Object)blockState.getValue(WhistleBlock.SIZE));
        PartialModel mouth = size == WhistleBlock.WhistleSize.LARGE ? AllPartialModels.WHISTLE_MOUTH_LARGE : (size == WhistleBlock.WhistleSize.MEDIUM ? AllPartialModels.WHISTLE_MOUTH_MEDIUM : AllPartialModels.WHISTLE_MOUTH_SMALL);
        float offset = be.animation.getValue(partialTicks);
        if (be.animation.getChaseTarget() > 0.0f && be.animation.getValue() > 0.5f) {
            float wiggleProgress = ((float)AnimationTickHolder.getTicks((LevelAccessor)be.getLevel()) + partialTicks) / 8.0f;
            offset = (float)((double)offset - Math.sin(wiggleProgress * ((float)Math.PI * 2) * (float)(4 - size.ordinal())) / 16.0);
        }
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)mouth, (BlockState)blockState).center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)direction))).uncenter()).translate(0.0f, offset * 4.0f / 16.0f, 0.0f)).light(light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }
}
