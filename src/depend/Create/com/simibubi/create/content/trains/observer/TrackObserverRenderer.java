/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.trains.observer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.observer.TrackObserver;
import com.simibubi.create.content.trains.observer.TrackObserverBlockEntity;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TrackObserverRenderer
extends SmartBlockEntityRenderer<TrackObserverBlockEntity> {
    public TrackObserverRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TrackObserverBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        BlockPos pos = be.getBlockPos();
        TrackTargetingBehaviour<TrackObserver> target = be.edgePoint;
        BlockPos targetPosition = target.getGlobalPosition();
        Level level = be.getLevel();
        BlockState trackState = level.getBlockState(targetPosition);
        Block block = trackState.getBlock();
        if (!(block instanceof ITrackBlock)) {
            return;
        }
        ms.pushPose();
        TransformStack.of((PoseStack)ms).translate((Vec3i)targetPosition.subtract((Vec3i)pos));
        TrackTargetingBehaviour.RenderedTrackOverlayType type = TrackTargetingBehaviour.RenderedTrackOverlayType.OBSERVER;
        TrackTargetingBehaviour.render((LevelAccessor)level, targetPosition, target.getTargetDirection(), target.getTargetBezier(), ms, buffer, light, overlay, type, 1.0f);
        ms.popPose();
    }
}
