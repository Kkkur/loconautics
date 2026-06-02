/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.trains.bogey;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.bogey.StandardBogeyRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public static class StandardBogeyRenderer.Small
extends StandardBogeyRenderer {
    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean inContraption) {
        super.render(bogeyData, wheelAngle, partialTick, poseStack, bufferSource, light, overlay, inContraption);
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutoutMipped());
        ((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.BOGEY_FRAME, (BlockState)Blocks.AIR.defaultBlockState()).scale(0.9980469f)).light(light).overlay(overlay).renderInto(poseStack, buffer);
        SuperByteBuffer wheels = CachedBuffers.partial((PartialModel)AllPartialModels.SMALL_BOGEY_WHEELS, (BlockState)Blocks.AIR.defaultBlockState());
        for (int side : Iterate.positiveAndNegative) {
            ((SuperByteBuffer)((SuperByteBuffer)wheels.translate(0.0f, 0.75f, (float)side)).rotateXDegrees(wheelAngle)).light(light).overlay(overlay).renderInto(poseStack, buffer);
        }
    }
}
