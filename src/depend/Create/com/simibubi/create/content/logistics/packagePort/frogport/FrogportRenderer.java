/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics.packagePort.frogport;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class FrogportRenderer
extends SmartBlockEntityRenderer<FrogportBlockEntity> {
    public FrogportRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(FrogportBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        SuperByteBuffer body = CachedBuffers.partial((PartialModel)AllPartialModels.FROGPORT_BODY, (BlockState)blockEntity.getBlockState());
        float yaw = blockEntity.getYaw();
        float headPitch = 80.0f;
        float tonguePitch = 0.0f;
        float tongueLength = 0.0f;
        float headPitchModifier = 1.0f;
        boolean hasTarget = blockEntity.target != null;
        boolean animating = blockEntity.isAnimationInProgress();
        boolean depositing = blockEntity.currentlyDepositing;
        Vec3 diff = Vec3.ZERO;
        if (blockEntity.addressFilter != null && !blockEntity.addressFilter.isBlank()) {
            this.renderNameplateOnHover(blockEntity, (Component)Component.literal((String)blockEntity.addressFilter), 1.0f, ms, buffer, light);
        }
        if (VisualizationManager.supportsVisualization((LevelAccessor)blockEntity.getLevel())) {
            return;
        }
        if (hasTarget) {
            diff = blockEntity.target.getExactTargetLocation(blockEntity, (LevelAccessor)blockEntity.getLevel(), blockEntity.getBlockPos()).subtract(0.0, animating && depositing ? 0.0 : 0.75, 0.0).subtract(Vec3.atCenterOf((Vec3i)blockEntity.getBlockPos()));
            tonguePitch = (float)Mth.atan2((double)diff.y, (double)(diff.multiply(1.0, 0.0, 1.0).length() + 0.1875)) * 57.295776f;
            tongueLength = Math.max((float)diff.length(), 1.0f);
            headPitch = Mth.clamp((float)(tonguePitch * 2.0f), (float)60.0f, (float)100.0f);
        }
        if (animating) {
            float progress = blockEntity.animationProgress.getValue(partialTicks);
            float scale = 1.0f;
            float itemDistance = 0.0f;
            if (depositing) {
                double modifier = Math.max(0.0, 1.0 - Math.pow(((double)progress - 0.25) * 4.0 - 1.0, 4.0));
                itemDistance = (float)Math.max((double)tongueLength * Math.min(1.0, ((double)progress - 0.25) * 3.0), (double)tongueLength * modifier);
                tongueLength = (float)((double)tongueLength * Math.max(0.0, 1.0 - Math.pow(((double)progress * 1.25 - 0.25) * 4.0 - 1.0, 4.0)));
                headPitchModifier = (float)Math.max(0.0, 1.0 - Math.pow((double)progress * 1.25 * 2.0 - 1.0, 4.0));
                scale = 0.25f + progress * 3.0f / 4.0f;
            } else {
                tongueLength = (float)((double)tongueLength * Math.pow(Math.max(0.0, 1.0 - (double)progress * 1.25), 5.0));
                headPitchModifier = 1.0f - (float)Math.min(1.0, Math.max(0.0, (Math.pow((double)progress * 1.5, 2.0) - 0.5) * 2.0));
                scale = (float)Math.max(0.5, 1.0 - (double)progress * 1.25);
                itemDistance = tongueLength;
            }
            this.renderPackage(blockEntity, ms, buffer, light, overlay, diff, scale, itemDistance);
        } else {
            tongueLength = 0.0f;
            float anticipation = blockEntity.anticipationProgress.getValue(partialTicks);
            headPitchModifier = anticipation > 0.0f ? (float)Math.max(0.0, 1.0 - Math.pow((double)anticipation * 1.25 * 2.0 - 1.0, 4.0)) : 0.0f;
        }
        headPitch *= headPitchModifier;
        headPitch = Math.max(headPitch, blockEntity.manualOpenAnimationProgress.getValue(partialTicks) * 60.0f);
        tongueLength = Math.max(tongueLength, blockEntity.manualOpenAnimationProgress.getValue(partialTicks) * 0.25f);
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)body.center()).rotateYDegrees(yaw)).uncenter()).light(light).overlay(overlay).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
        SuperByteBuffer head = CachedBuffers.partial((PartialModel)(blockEntity.goggles ? AllPartialModels.FROGPORT_HEAD_GOGGLES : AllPartialModels.FROGPORT_HEAD), (BlockState)blockEntity.getBlockState());
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)head.center()).rotateYDegrees(yaw)).uncenter()).translate(0.5f, 0.625f, 0.6875f)).rotateXDegrees(headPitch)).translateBack(0.5f, 0.625f, 0.6875f);
        head.light(light).overlay(overlay).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
        SuperByteBuffer tongue = CachedBuffers.partial((PartialModel)AllPartialModels.FROGPORT_TONGUE, (BlockState)blockEntity.getBlockState());
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)tongue.center()).rotateYDegrees(yaw)).uncenter()).translate(0.5f, 0.625f, 0.6875f)).rotateXDegrees(tonguePitch)).scale(1.0f, 1.0f, tongueLength / 0.4375f)).translateBack(0.5f, 0.625f, 0.6875f);
        tongue.light(light).overlay(overlay).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
    }

    private void renderPackage(FrogportBlockEntity blockEntity, PoseStack ms, MultiBufferSource buffer, int light, int overlay, Vec3 diff, float scale, float itemDistance) {
        if (blockEntity.animatedPackage == null) {
            return;
        }
        if ((double)scale < 0.45) {
            return;
        }
        ResourceLocation key = BuiltInRegistries.ITEM.getKey((Object)blockEntity.animatedPackage.getItem());
        if (key == BuiltInRegistries.ITEM.getDefaultKey()) {
            return;
        }
        SuperByteBuffer rigBuffer = CachedBuffers.partial((PartialModel)AllPartialModels.PACKAGE_RIGGING.get(key), (BlockState)blockEntity.getBlockState());
        SuperByteBuffer boxBuffer = CachedBuffers.partial((PartialModel)AllPartialModels.PACKAGES.get(key), (BlockState)blockEntity.getBlockState());
        boolean animating = blockEntity.isAnimationInProgress();
        boolean depositing = blockEntity.currentlyDepositing;
        for (SuperByteBuffer buf : new SuperByteBuffer[]{boxBuffer, rigBuffer}) {
            ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)buf.translate(0.0f, 0.1875f, 0.0f)).translate(diff.normalize().scale((double)itemDistance).subtract(0.0, animating && depositing ? 0.75 : 0.0, 0.0))).center()).scale(scale)).uncenter()).light(light).overlay(overlay).renderInto(ms, buffer.getBuffer(RenderType.cutout()));
            if (!blockEntity.currentlyDepositing) break;
        }
    }
}
