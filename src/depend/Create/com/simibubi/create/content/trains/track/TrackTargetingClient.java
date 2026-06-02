/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.render.SuperRenderTypeBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.track;

import com.google.common.base.Objects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TrackTargetingClient {
    static BlockPos lastHovered;
    static boolean lastDirection;
    static EdgePointType<?> lastType;
    static BezierTrackPointLocation lastHoveredBezierSegment;
    static TrackTargetingBlockItem.OverlapResult lastResult;
    static TrackGraphLocation lastLocation;

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Vec3 lookAngle = player.getLookAngle();
        BlockPos hovered = null;
        boolean direction = false;
        EdgePointType<?> type = null;
        BezierTrackPointLocation hoveredBezier = null;
        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();
        if (item instanceof TrackTargetingBlockItem) {
            TrackTargetingBlockItem ttbi = (TrackTargetingBlockItem)item;
            type = ttbi.getType(stack);
        }
        if (type == EdgePointType.SIGNAL) {
            Create.RAILWAYS.sided(null).tickSignalOverlay();
        }
        boolean alreadySelected = stack.has(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_POS);
        if (type != null) {
            TrackBlockOutline.BezierPointSelection bezierSelection = TrackBlockOutline.result;
            if (alreadySelected) {
                hovered = (BlockPos)stack.get(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_POS);
                direction = (Boolean)stack.getOrDefault(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_DIRECTION, (Object)false);
                if (stack.has(AllDataComponents.TRACK_TARGETING_ITEM_BEZIER)) {
                    hoveredBezier = (BezierTrackPointLocation)stack.get(AllDataComponents.TRACK_TARGETING_ITEM_BEZIER);
                }
            } else if (bezierSelection != null) {
                hovered = bezierSelection.blockEntity().getBlockPos();
                hoveredBezier = bezierSelection.loc();
                direction = lookAngle.dot(bezierSelection.direction()) < 0.0;
            } else {
                BlockHitResult blockHitResult;
                BlockPos pos;
                BlockState blockState;
                Block block;
                HitResult hitResult = mc.hitResult;
                if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK && (block = (blockState = mc.level.getBlockState(pos = (blockHitResult = (BlockHitResult)hitResult).getBlockPos())).getBlock()) instanceof ITrackBlock) {
                    ITrackBlock track = (ITrackBlock)block;
                    direction = track.getNearestTrackAxis((BlockGetter)mc.level, pos, blockState, lookAngle).getSecond() == Direction.AxisDirection.POSITIVE;
                    hovered = pos;
                }
            }
        }
        if (hovered == null) {
            lastHovered = null;
            lastResult = null;
            lastLocation = null;
            lastHoveredBezierSegment = null;
            return;
        }
        if (Objects.equal(hovered, (Object)lastHovered) && Objects.equal(hoveredBezier, (Object)lastHoveredBezierSegment) && direction == lastDirection && type == lastType) {
            return;
        }
        lastType = type;
        lastHovered = hovered;
        lastDirection = direction;
        lastHoveredBezierSegment = hoveredBezier;
        TrackTargetingBlockItem.withGraphLocation((Level)mc.level, hovered, direction, hoveredBezier, type, (result, location) -> {
            lastResult = result;
            lastLocation = location;
        });
    }

    public static void render(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera) {
        Direction.AxisDirection direction;
        if (lastLocation == null || TrackTargetingClient.lastResult.feedback != null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        BlockPos pos = lastHovered;
        int light = LevelRenderer.getLightColor((BlockAndTintGetter)mc.level, (BlockPos)pos);
        Direction.AxisDirection axisDirection = direction = lastDirection ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        TrackTargetingBehaviour.RenderedTrackOverlayType type = lastType == EdgePointType.SIGNAL ? TrackTargetingBehaviour.RenderedTrackOverlayType.SIGNAL : (lastType == EdgePointType.OBSERVER ? TrackTargetingBehaviour.RenderedTrackOverlayType.OBSERVER : TrackTargetingBehaviour.RenderedTrackOverlayType.STATION);
        ms.pushPose();
        TransformStack.of((PoseStack)ms).translate(Vec3.atLowerCornerOf((Vec3i)pos).subtract(camera));
        TrackTargetingBehaviour.render((LevelAccessor)mc.level, pos, direction, lastHoveredBezierSegment, ms, (MultiBufferSource)buffer, light, OverlayTexture.NO_OVERLAY, type, 1.0625f);
        ms.popPose();
    }
}
