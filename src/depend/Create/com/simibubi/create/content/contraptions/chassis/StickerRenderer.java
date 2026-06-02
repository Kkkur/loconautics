/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.chassis;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.chassis.StickerBlock;
import com.simibubi.create.content.contraptions.chassis.StickerBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class StickerRenderer
extends SafeBlockEntityRenderer<StickerBlockEntity> {
    public StickerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(StickerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        BlockState state = be.getBlockState();
        SuperByteBuffer head = CachedBuffers.partial((PartialModel)AllPartialModels.STICKER_HEAD, (BlockState)state);
        float offset = be.piston.getValue(AnimationTickHolder.getPartialTicks((LevelAccessor)be.getLevel()));
        if (be.getLevel() != Minecraft.getInstance().level && !be.isVirtual()) {
            offset = (Boolean)state.getValue((Property)StickerBlock.EXTENDED) != false ? 1.0f : 0.0f;
        }
        Direction facing = (Direction)state.getValue((Property)StickerBlock.FACING);
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)head.nudge(be.hashCode())).center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing))).rotateXDegrees(AngleHelper.verticalAngle((Direction)facing) + 90.0f)).uncenter()).translate(0.0f, offset * offset * 4.0f / 16.0f, 0.0f);
        head.light(light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }
}
