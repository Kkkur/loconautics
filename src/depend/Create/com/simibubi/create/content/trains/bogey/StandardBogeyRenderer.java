/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.trains.bogey;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class StandardBogeyRenderer
implements BogeyRenderer {
    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean inContraption) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutoutMipped());
        SuperByteBuffer shaft = CachedBuffers.block((BlockState)((BlockState)AllBlocks.SHAFT.getDefaultState().setValue((Property)ShaftBlock.AXIS, (Comparable)Direction.Axis.Z)));
        for (int i : Iterate.zeroAndOne) {
            ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)shaft.translate(-0.5f, 0.25f, (float)(i * -1))).center()).rotateZDegrees(wheelAngle)).uncenter()).light(light).overlay(overlay).renderInto(poseStack, buffer);
        }
    }

    public static class Large
    extends StandardBogeyRenderer {
        public static final float BELT_RADIUS_PX = 5.0f;
        public static final float BELT_RADIUS_IN_UV_SPACE = 0.3125f;

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean inContraption) {
            super.render(bogeyData, wheelAngle, partialTick, poseStack, bufferSource, light, overlay, inContraption);
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutoutMipped());
            SuperByteBuffer secondaryShaft = CachedBuffers.block((BlockState)((BlockState)AllBlocks.SHAFT.getDefaultState().setValue((Property)ShaftBlock.AXIS, (Comparable)Direction.Axis.X)));
            for (int i : Iterate.zeroAndOne) {
                ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)secondaryShaft.translate(-0.5f, 0.25f, 0.5f + (float)(i * -2))).center()).rotateXDegrees(wheelAngle)).uncenter()).light(light).overlay(overlay).renderInto(poseStack, buffer);
            }
            ((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.BOGEY_DRIVE, (BlockState)Blocks.AIR.defaultBlockState()).scale(0.9980469f)).light(light).overlay(overlay).renderInto(poseStack, buffer);
            float spriteSize = AllSpriteShifts.BOGEY_BELT.getTarget().getV1() - AllSpriteShifts.BOGEY_BELT.getTarget().getV0();
            float scroll = 0.0054541538f * wheelAngle;
            scroll -= (float)Mth.floor((float)scroll);
            scroll = scroll * spriteSize * 0.5f;
            ((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.BOGEY_DRIVE_BELT, (BlockState)Blocks.AIR.defaultBlockState()).scale(0.9980469f)).light(light).overlay(overlay).shiftUVScrolling(AllSpriteShifts.BOGEY_BELT, scroll).renderInto(poseStack, buffer);
            ((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.BOGEY_PISTON, (BlockState)Blocks.AIR.defaultBlockState()).translate(0.0, 0.0, 0.25 * Math.sin(AngleHelper.rad((double)wheelAngle)))).light(light).overlay(overlay).renderInto(poseStack, buffer);
            ((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.LARGE_BOGEY_WHEELS, (BlockState)Blocks.AIR.defaultBlockState()).translate(0.0f, 1.0f, 0.0f)).rotateXDegrees(wheelAngle)).light(light).overlay(overlay).renderInto(poseStack, buffer);
            ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.BOGEY_PIN, (BlockState)Blocks.AIR.defaultBlockState()).translate(0.0f, 1.0f, 0.0f)).rotateXDegrees(wheelAngle)).translate(0.0f, 0.25f, 0.0f)).rotateXDegrees(-wheelAngle)).light(light).overlay(overlay).renderInto(poseStack, buffer);
        }
    }

    public static class Small
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
}
