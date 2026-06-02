/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.Transform
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.trains.station;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.depot.DepotRenderer;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.Transform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class StationRenderer
extends SafeBlockEntityRenderer<StationBlockEntity> {
    public StationRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(StationBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockPos pos = be.getBlockPos();
        TrackTargetingBehaviour<GlobalStation> target = be.edgePoint;
        BlockPos targetPosition = target.getGlobalPosition();
        Level level = be.getLevel();
        DepotRenderer.renderItemsOf(be, partialTicks, ms, buffer, light, overlay, be.depotBehaviour);
        BlockState trackState = level.getBlockState(targetPosition);
        Block block = trackState.getBlock();
        if (!(block instanceof ITrackBlock)) {
            return;
        }
        ITrackBlock track = (ITrackBlock)block;
        GlobalStation station = be.getStation();
        boolean isAssembling = (Boolean)be.getBlockState().getValue((Property)StationBlock.ASSEMBLING);
        if (!isAssembling || (station == null || station.getPresentTrain() != null) && !be.isVirtual()) {
            StationRenderer.renderFlag(be.flag.getValue(partialTicks) > 0.75f ? AllPartialModels.STATION_ON : AllPartialModels.STATION_OFF, be, partialTicks, ms, buffer, light, overlay);
            ms.pushPose();
            TransformStack.of((PoseStack)ms).translate((Vec3i)targetPosition.subtract((Vec3i)pos));
            TrackTargetingBehaviour.render((LevelAccessor)level, targetPosition, target.getTargetDirection(), target.getTargetBezier(), ms, buffer, light, overlay, TrackTargetingBehaviour.RenderedTrackOverlayType.STATION, 1.0f);
            ms.popPose();
            return;
        }
        StationRenderer.renderFlag(AllPartialModels.STATION_ASSEMBLE, be, partialTicks, ms, buffer, light, overlay);
        Direction direction = be.assemblyDirection;
        if (be.isVirtual() && be.bogeyLocations == null) {
            be.refreshAssemblyInfo();
        }
        if (direction == null || be.assemblyLength == 0 || be.bogeyLocations == null) {
            return;
        }
        ms.pushPose();
        BlockPos offset = targetPosition.subtract((Vec3i)pos);
        ms.translate((float)offset.getX(), (float)offset.getY(), (float)offset.getZ());
        BlockPos.MutableBlockPos currentPos = targetPosition.mutable();
        PartialModel assemblyOverlay = track.prepareAssemblyOverlay((BlockGetter)level, targetPosition, trackState, direction, ms);
        int colorWhenValid = 9876991;
        int colorWhenCarriage = 13303702;
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
        currentPos.move(direction, 1);
        ms.translate(0.0f, 0.0f, 1.0f);
        for (int i = 0; i < be.assemblyLength; ++i) {
            int valid = be.isValidBogeyOffset(i) ? colorWhenValid : -1;
            for (int j : be.bogeyLocations) {
                if (i != j) continue;
                valid = colorWhenCarriage;
                break;
            }
            if (valid != -1) {
                int lightColor = LevelRenderer.getLightColor((BlockAndTintGetter)level, (BlockPos)currentPos);
                SuperByteBuffer sbb = CachedBuffers.partial((PartialModel)assemblyOverlay, (BlockState)trackState);
                sbb.color(valid);
                sbb.light(lightColor);
                sbb.renderInto(ms, vb);
            }
            ms.translate(0.0f, 0.0f, 1.0f);
            currentPos.move(direction);
        }
        ms.popPose();
    }

    public static void renderFlag(PartialModel flag, StationBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (!be.resolveFlagAngle()) {
            return;
        }
        SuperByteBuffer flagBB = CachedBuffers.partial((PartialModel)flag, (BlockState)be.getBlockState());
        StationRenderer.transformFlag(flagBB, be, partialTicks, be.flagYRot, be.flagFlipped);
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)flagBB.translate(0.03125f, 0.0f, 0.0f)).rotateYDegrees(be.flagFlipped ? 0.0f : 180.0f)).translate(-0.03125f, 0.0f, 0.0f)).light(light).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
    }

    public static void transformFlag(Transform<?> flag, StationBlockEntity be, float partialTicks, int yRot, boolean flipped) {
        float value = be.flag.getValue(partialTicks);
        float progress = (float)Math.pow(Math.min(value * 5.0f, 1.0f), 2.0);
        if (be.flag.getChaseTarget() > 0.0f && !be.flag.settled() && progress == 1.0f) {
            float wiggleProgress = (value - 0.2f) / 0.8f;
            progress = (float)((double)progress + Math.sin(wiggleProgress * ((float)Math.PI * 2) * 4.0f) / 8.0 / (double)Math.max(1.0f, 8.0f * wiggleProgress));
        }
        float nudge = 0.001953125f;
        ((Transform)((Transform)((Transform)((Transform)flag.center()).rotateYDegrees((float)yRot)).translate(nudge, 0.59375f, flipped ? 0.875f - nudge : 0.125f + nudge)).uncenter()).rotateXDegrees((float)(flipped ? 1 : -1) * (progress * 90.0f + 270.0f));
    }

    public boolean shouldRenderOffScreen(StationBlockEntity pBlockEntity) {
        return true;
    }

    public int getViewDistance() {
        return 192;
    }
}
