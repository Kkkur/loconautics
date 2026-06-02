/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.redstone.displayLink;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.redstone.displayLink.LinkWithBulbBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.RenderTypes;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class LinkBulbRenderer
extends SafeBlockEntityRenderer<LinkWithBulbBlockEntity> {
    public LinkBulbRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(LinkWithBulbBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        float glow = be.getGlow(partialTicks);
        if (glow < 0.125f) {
            return;
        }
        glow = (float)(1.0 - 2.0 * Math.pow(glow - 0.75f, 2.0));
        glow = Mth.clamp((float)glow, (float)-1.0f, (float)1.0f);
        int color = (int)(200.0f * glow);
        BlockState blockState = be.getBlockState();
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        Direction face = be.getBulbFacing(blockState);
        ms.pushPose();
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)msr.center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)face) + 180.0f)).rotateXDegrees(-AngleHelper.verticalAngle((Direction)face) - 90.0f)).uncenter();
        ((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.DISPLAY_LINK_TUBE, (BlockState)blockState).translate(be.getBulbOffset(blockState))).light(0xF000F0).renderInto(ms, buffer.getBuffer(RenderType.translucent()));
        ((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.DISPLAY_LINK_GLOW, (BlockState)blockState).translate(be.getBulbOffset(blockState))).light(0xF000F0).color(color, color, color, 255).disableDiffuse().renderInto(ms, buffer.getBuffer(RenderTypes.additive()));
        ms.popPose();
    }
}
