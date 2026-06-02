/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.render.CachedBuffers
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.trains.signal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SignalRenderer
extends SafeBlockEntityRenderer<SignalBlockEntity> {
    public SignalRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(SignalBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        BlockState blockState = be.getBlockState();
        SignalBlockEntity.SignalState signalState = be.getState();
        SignalBlockEntity.OverlayState overlayState = be.getOverlay();
        float renderTime = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel());
        if (signalState.isRedLight(renderTime)) {
            CachedBuffers.partial((PartialModel)AllPartialModels.SIGNAL_ON, (BlockState)blockState).light(240).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        } else {
            CachedBuffers.partial((PartialModel)AllPartialModels.SIGNAL_OFF, (BlockState)blockState).light(light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
        BlockPos pos = be.getBlockPos();
        TrackTargetingBehaviour<SignalBoundary> target = be.edgePoint;
        BlockPos targetPosition = target.getGlobalPosition();
        Level level = be.getLevel();
        BlockState trackState = level.getBlockState(targetPosition);
        Block block = trackState.getBlock();
        if (!(block instanceof ITrackBlock)) {
            return;
        }
        if (overlayState == SignalBlockEntity.OverlayState.SKIP) {
            return;
        }
        ms.pushPose();
        TransformStack.of((PoseStack)ms).translate((Vec3i)targetPosition.subtract((Vec3i)pos));
        TrackTargetingBehaviour.RenderedTrackOverlayType type = overlayState == SignalBlockEntity.OverlayState.DUAL ? TrackTargetingBehaviour.RenderedTrackOverlayType.DUAL_SIGNAL : TrackTargetingBehaviour.RenderedTrackOverlayType.SIGNAL;
        TrackTargetingBehaviour.render((LevelAccessor)level, targetPosition, target.getTargetDirection(), target.getTargetBezier(), ms, buffer, light, overlay, type, 1.0f);
        ms.popPose();
    }
}
