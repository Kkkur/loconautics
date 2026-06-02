/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.actors.trainControls;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class ControlsRenderer {
    public static void render(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer, float equipAnimation, float firstLever, float secondLever) {
        BlockState state = context.state;
        Direction facing = (Direction)state.getValue((Property)ControlsBlock.FACING);
        SuperByteBuffer cover = CachedBuffers.partial((PartialModel)AllPartialModels.TRAIN_CONTROLS_COVER, (BlockState)state);
        float hAngle = 180.0f + AngleHelper.horizontalAngle((Direction)facing);
        PoseStack ms = matrices.getModel();
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)cover.transform(ms)).center()).rotateYDegrees(hAngle)).uncenter()).light(LevelRenderer.getLightColor((BlockAndTintGetter)renderWorld, (BlockPos)context.localPos)).useLevelLight((BlockAndTintGetter)context.world, matrices.getWorld()).renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.cutoutMipped()));
        double yOffset = Mth.lerp((float)(equipAnimation * equipAnimation), (float)-0.15f, (float)0.05f);
        for (boolean first : Iterate.trueAndFalse) {
            float vAngle = Mth.clamp((float)(first ? firstLever * 70.0f - 25.0f : secondLever * 15.0f), (float)-45.0f, (float)45.0f);
            SuperByteBuffer lever = CachedBuffers.partial((PartialModel)AllPartialModels.TRAIN_CONTROLS_LEVER, (BlockState)state);
            ms.pushPose();
            ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).center()).rotateYDegrees(hAngle)).translate(0.0f, 0.25f, 0.25f).rotateXDegrees(vAngle - 45.0f)).translate(0.0, yOffset, 0.0)).rotateXDegrees(45.0f)).uncenter()).translate(0.0f, -0.375f, -0.1875f).translate(first ? 0.0f : 0.375f, 0.0f, 0.0f);
            ((SuperByteBuffer)lever.transform(ms)).light(LevelRenderer.getLightColor((BlockAndTintGetter)renderWorld, (BlockPos)context.localPos)).useLevelLight((BlockAndTintGetter)context.world, matrices.getWorld()).renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.solid()));
            ms.popPose();
        }
    }
}
