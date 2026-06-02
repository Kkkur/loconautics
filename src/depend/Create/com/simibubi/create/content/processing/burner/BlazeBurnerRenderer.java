/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.processing.burner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlazeBurnerRenderer
extends SafeBlockEntityRenderer<BlazeBurnerBlockEntity> {
    public BlazeBurnerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(BlazeBurnerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        BlazeBurnerBlock.HeatLevel heatLevel = be.getHeatLevelFromBlock();
        if (heatLevel == BlazeBurnerBlock.HeatLevel.NONE) {
            return;
        }
        Level level = be.getLevel();
        BlockState blockState = be.getBlockState();
        float animation = be.headAnimation.getValue(partialTicks) * 0.175f;
        float horizontalAngle = AngleHelper.rad((double)be.headAngle.getValue(partialTicks));
        boolean canDrawFlame = heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING);
        boolean drawGoggles = be.goggles;
        PartialModel drawHat = be.hat ? AllPartialModels.TRAIN_HAT : (be.stockKeeper ? AllPartialModels.LOGISTICS_HAT : null);
        int hashCode = be.hashCode();
        BlazeBurnerRenderer.renderShared(ms, null, bufferSource, level, blockState, heatLevel, animation, horizontalAngle, canDrawFlame, drawGoggles, drawHat, hashCode);
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource bufferSource, LerpedFloat headAngle, boolean conductor) {
        BlockState state = context.state;
        BlazeBurnerBlock.HeatLevel heatLevel = BlazeBurnerBlock.getHeatLevelOf(state);
        if (heatLevel == BlazeBurnerBlock.HeatLevel.NONE) {
            return;
        }
        if (!heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
            heatLevel = BlazeBurnerBlock.HeatLevel.FADING;
        }
        Level level = context.world;
        float horizontalAngle = AngleHelper.rad((double)headAngle.getValue(AnimationTickHolder.getPartialTicks((LevelAccessor)level)));
        boolean drawGoggles = context.blockEntityData.contains("Goggles");
        boolean drawHat = conductor || context.blockEntityData.contains("TrainHat");
        int hashCode = context.hashCode();
        BlazeBurnerRenderer.renderShared(matrices.getViewProjection(), matrices.getModel(), bufferSource, level, state, heatLevel, 0.0f, horizontalAngle, false, drawGoggles, (PartialModel)(drawHat ? AllPartialModels.TRAIN_HAT : null), hashCode);
    }

    public static void renderShared(PoseStack ms, @Nullable PoseStack modelTransform, MultiBufferSource bufferSource, Level level, BlockState blockState, BlazeBurnerBlock.HeatLevel heatLevel, float animation, float horizontalAngle, boolean canDrawFlame, boolean drawGoggles, PartialModel drawHat, int hashCode) {
        boolean blockAbove = animation > 0.125f;
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)level);
        float renderTick = time + (float)(hashCode % 13) * 16.0f;
        float offsetMult = heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING) ? 64.0f : 16.0f;
        float offset = Mth.sin((float)((float)((double)(renderTick / 16.0f) % (Math.PI * 2)))) / offsetMult;
        float offset1 = Mth.sin((float)((float)(((double)(renderTick / 16.0f) + Math.PI) % (Math.PI * 2)))) / offsetMult;
        float offset2 = Mth.sin((float)((float)(((double)(renderTick / 16.0f) + 1.5707963267948966) % (Math.PI * 2)))) / offsetMult;
        float headY = offset - animation * 0.75f;
        ms.pushPose();
        PartialModel blazeModel = BlazeBurnerRenderer.getBlazeModel(heatLevel, blockAbove);
        SuperByteBuffer blazeBuffer = CachedBuffers.partial((PartialModel)blazeModel, (BlockState)blockState);
        if (modelTransform != null) {
            blazeBuffer.transform(modelTransform);
        }
        blazeBuffer.translate(0.0f, headY, 0.0f);
        BlazeBurnerRenderer.draw(blazeBuffer, horizontalAngle, ms, bufferSource.getBuffer(RenderType.solid()));
        if (drawGoggles) {
            PartialModel gogglesModel = blazeModel == AllPartialModels.BLAZE_INERT ? AllPartialModels.BLAZE_GOGGLES_SMALL : AllPartialModels.BLAZE_GOGGLES;
            SuperByteBuffer gogglesBuffer = CachedBuffers.partial((PartialModel)gogglesModel, (BlockState)blockState);
            if (modelTransform != null) {
                gogglesBuffer.transform(modelTransform);
            }
            gogglesBuffer.translate(0.0f, headY + 0.5f, 0.0f);
            BlazeBurnerRenderer.draw(gogglesBuffer, horizontalAngle, ms, bufferSource.getBuffer(RenderType.solid()));
        }
        if (drawHat != null) {
            SuperByteBuffer hatBuffer = CachedBuffers.partial((PartialModel)drawHat, (BlockState)blockState);
            if (modelTransform != null) {
                hatBuffer.transform(modelTransform);
            }
            hatBuffer.translate(0.0f, headY, 0.0f);
            if (blazeModel == AllPartialModels.BLAZE_INERT) {
                ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)hatBuffer.translateY(0.5f)).center()).scale(0.75f)).uncenter();
            } else {
                hatBuffer.translateY(0.75f);
            }
            VertexConsumer cutout = bufferSource.getBuffer(RenderType.cutoutMipped());
            ((SuperByteBuffer)((SuperByteBuffer)hatBuffer.rotateCentered(horizontalAngle + (float)Math.PI, Direction.UP)).translate(0.5f, 0.0f, 0.5f)).light(0xF000F0).renderInto(ms, cutout);
        }
        if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
            PartialModel rodsModel = heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING ? AllPartialModels.BLAZE_BURNER_SUPER_RODS : AllPartialModels.BLAZE_BURNER_RODS;
            PartialModel rodsModel2 = heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING ? AllPartialModels.BLAZE_BURNER_SUPER_RODS_2 : AllPartialModels.BLAZE_BURNER_RODS_2;
            SuperByteBuffer rodsBuffer = CachedBuffers.partial((PartialModel)rodsModel, (BlockState)blockState);
            if (modelTransform != null) {
                rodsBuffer.transform(modelTransform);
            }
            ((SuperByteBuffer)rodsBuffer.translate(0.0f, offset1 + animation + 0.125f, 0.0f)).light(0xF000F0).renderInto(ms, bufferSource.getBuffer(RenderType.solid()));
            SuperByteBuffer rodsBuffer2 = CachedBuffers.partial((PartialModel)rodsModel2, (BlockState)blockState);
            if (modelTransform != null) {
                rodsBuffer2.transform(modelTransform);
            }
            ((SuperByteBuffer)rodsBuffer2.translate(0.0f, offset2 + animation - 0.1875f, 0.0f)).light(0xF000F0).renderInto(ms, bufferSource.getBuffer(RenderType.solid()));
        }
        if (canDrawFlame && blockAbove) {
            SpriteShiftEntry spriteShift = heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING ? AllSpriteShifts.SUPER_BURNER_FLAME : AllSpriteShifts.BURNER_FLAME;
            float spriteWidth = spriteShift.getTarget().getU1() - spriteShift.getTarget().getU0();
            float spriteHeight = spriteShift.getTarget().getV1() - spriteShift.getTarget().getV0();
            float speed = 0.03125f + 0.015625f * (float)heatLevel.ordinal();
            double vScroll = speed * time;
            vScroll -= Math.floor(vScroll);
            vScroll = vScroll * (double)spriteHeight / 2.0;
            double uScroll = speed * time / 2.0f;
            uScroll -= Math.floor(uScroll);
            uScroll = uScroll * (double)spriteWidth / 2.0;
            SuperByteBuffer flameBuffer = CachedBuffers.partial((PartialModel)AllPartialModels.BLAZE_BURNER_FLAME, (BlockState)blockState);
            if (modelTransform != null) {
                flameBuffer.transform(modelTransform);
            }
            flameBuffer.shiftUVScrolling(spriteShift, (float)uScroll, (float)vScroll);
            VertexConsumer cutout = bufferSource.getBuffer(RenderType.cutoutMipped());
            BlazeBurnerRenderer.draw(flameBuffer, horizontalAngle, ms, cutout);
        }
        ms.popPose();
    }

    public static PartialModel getBlazeModel(BlazeBurnerBlock.HeatLevel heatLevel, boolean blockAbove) {
        if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)) {
            return blockAbove ? AllPartialModels.BLAZE_SUPER_ACTIVE : AllPartialModels.BLAZE_SUPER;
        }
        if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
            return blockAbove && heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.KINDLED) ? AllPartialModels.BLAZE_ACTIVE : AllPartialModels.BLAZE_IDLE;
        }
        return AllPartialModels.BLAZE_INERT;
    }

    private static void draw(SuperByteBuffer buffer, float horizontalAngle, PoseStack ms, VertexConsumer vc) {
        ((SuperByteBuffer)buffer.rotateCentered(horizontalAngle, Direction.UP)).light(0xF000F0).renderInto(ms, vc);
    }
}
