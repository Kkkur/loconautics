/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.WorldAttached
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.client.event.RenderHighlightEvent$Block
 */
package com.simibubi.create.content.trains.track;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackRenderer;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.content.trains.track.TrackVoxelShapes;
import com.simibubi.create.foundation.utility.RaycastHelper;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.WorldAttached;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

@EventBusSubscriber(value={Dist.CLIENT})
public class TrackBlockOutline {
    public static WorldAttached<Map<BlockPos, TrackBlockEntity>> TRACKS_WITH_TURNS = new WorldAttached(w -> new HashMap());
    public static BezierPointSelection result;
    private static final VoxelShape LONG_CROSS;
    private static final VoxelShape LONG_ORTHO;
    private static final VoxelShape LONG_ORTHO_OFFSET;

    public static void pickCurves() {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.cameraEntity;
        if (!(entity instanceof LocalPlayer)) {
            return;
        }
        LocalPlayer player = (LocalPlayer)entity;
        if (mc.level == null) {
            return;
        }
        Vec3 origin = player.getEyePosition(AnimationTickHolder.getPartialTicks((LevelAccessor)mc.level));
        double maxRange = mc.hitResult == null ? Double.MAX_VALUE : mc.hitResult.getLocation().distanceToSqr(origin);
        result = null;
        double range = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        Vec3 target = RaycastHelper.getTraceTarget((Player)player, Math.min(maxRange, range) + 1.0, origin);
        Map turns = (Map)TRACKS_WITH_TURNS.get((LevelAccessor)mc.level);
        for (TrackBlockEntity be : turns.values()) {
            for (BezierConnection bc : be.connections.values()) {
                AABB bounds;
                if (!bc.isPrimary() || !(bounds = bc.getBounds()).contains(origin) && bounds.clip(origin, target).isEmpty()) continue;
                float[] stepLUT = bc.getStepLUT();
                int segments = (int)(bc.getLength() * 2.0);
                AABB segmentBounds = AllShapes.TRACK_ORTHO.get(Direction.SOUTH).bounds();
                segmentBounds = segmentBounds.move(-0.5, segmentBounds.getYsize() / -2.0, -0.5);
                int bestSegment = -1;
                double bestDistance = Double.MAX_VALUE;
                double newMaxRange = maxRange;
                for (int i = 0; i < stepLUT.length - 2; ++i) {
                    double distanceToSqr;
                    float t = stepLUT[i] * (float)i / (float)segments;
                    float t1 = stepLUT[i + 1] * (float)(i + 1) / (float)segments;
                    float t2 = stepLUT[i + 2] * (float)(i + 2) / (float)segments;
                    Vec3 v1 = bc.getPosition(t);
                    Vec3 v2 = bc.getPosition(t2);
                    Vec3 diff = v2.subtract(v1);
                    Vec3 angles = TrackRenderer.getModelAngles(bc.getNormal(t1), diff);
                    Vec3 anchor = v1.add(diff.scale(0.5));
                    Vec3 localOrigin = origin.subtract(anchor);
                    Vec3 localDirection = target.subtract(origin);
                    localOrigin = VecHelper.rotate((Vec3)localOrigin, (double)AngleHelper.deg((double)(-angles.x)), (Direction.Axis)Direction.Axis.X);
                    localOrigin = VecHelper.rotate((Vec3)localOrigin, (double)AngleHelper.deg((double)(-angles.y)), (Direction.Axis)Direction.Axis.Y);
                    localDirection = VecHelper.rotate((Vec3)localDirection, (double)AngleHelper.deg((double)(-angles.x)), (Direction.Axis)Direction.Axis.X);
                    Optional clip = segmentBounds.clip(localOrigin, localOrigin.add(localDirection = VecHelper.rotate((Vec3)localDirection, (double)AngleHelper.deg((double)(-angles.y)), (Direction.Axis)Direction.Axis.Y)));
                    if (clip.isEmpty() || bestSegment != -1 && bestDistance < ((Vec3)clip.get()).distanceToSqr(0.0, 0.25, 0.0) || (distanceToSqr = ((Vec3)clip.get()).distanceToSqr(localOrigin)) > maxRange) continue;
                    bestSegment = i;
                    newMaxRange = distanceToSqr;
                    bestDistance = ((Vec3)clip.get()).distanceToSqr(0.0, 0.25, 0.0);
                    BezierTrackPointLocation location = new BezierTrackPointLocation(bc.getKey(), i);
                    result = new BezierPointSelection(be, location, anchor, angles, diff.normalize());
                }
                if (bestSegment == -1) continue;
                maxRange = newMaxRange;
            }
        }
        if (result == null) {
            return;
        }
        if (mc.hitResult != null && mc.hitResult.getType() != HitResult.Type.MISS) {
            Vec3 priorLoc = mc.hitResult.getLocation();
            mc.hitResult = BlockHitResult.miss((Vec3)priorLoc, (Direction)Direction.UP, (BlockPos)BlockPos.containing((Position)priorLoc));
        }
    }

    public static void drawCurveSelection(PoseStack ms, MultiBufferSource buffer, Vec3 camera) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        BezierPointSelection result = TrackBlockOutline.result;
        if (result == null) {
            return;
        }
        VertexConsumer vb = buffer.getBuffer(RenderType.lines());
        Vec3 vec = result.vec().subtract(camera);
        Vec3 angles = result.angles();
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).pushPose().translate(vec.x, vec.y + 0.125, vec.z)).rotateY((float)angles.y)).rotateX((float)angles.x)).translate(-0.5, -0.125, -0.5);
        boolean holdingTrack = AllTags.AllBlockTags.TRACKS.matches(Minecraft.getInstance().player.getMainHandItem());
        TrackBlockOutline.renderShape(AllShapes.TRACK_ORTHO.get(Direction.SOUTH), ms, vb, holdingTrack ? Boolean.valueOf(false) : null);
        ms.popPose();
    }

    @SubscribeEvent
    public static void drawCustomBlockSelection(RenderHighlightEvent.Block event) {
        TrackBlockEntity tbe;
        BlockEntity blockEntity;
        Minecraft mc = Minecraft.getInstance();
        BlockHitResult target = event.getTarget();
        BlockPos pos = target.getBlockPos();
        BlockState blockstate = mc.level.getBlockState(pos);
        if (!(blockstate.getBlock() instanceof TrackBlock)) {
            return;
        }
        if (!mc.level.getWorldBorder().isWithinBounds(pos)) {
            return;
        }
        VertexConsumer vb = event.getMultiBufferSource().getBuffer(RenderType.lines());
        Vec3 camPos = event.getCamera().getPosition();
        PoseStack ms = event.getPoseStack();
        ms.pushPose();
        ms.translate((double)pos.getX() - camPos.x, (double)pos.getY() - camPos.y, (double)pos.getZ() - camPos.z);
        boolean holdingTrack = AllTags.AllBlockTags.TRACKS.matches(Minecraft.getInstance().player.getMainHandItem());
        TrackShape shape = (TrackShape)((Object)blockstate.getValue(TrackBlock.SHAPE));
        boolean canConnectFrom = !shape.isJunction() && (!((blockEntity = mc.level.getBlockEntity(pos)) instanceof TrackBlockEntity) || !(tbe = (TrackBlockEntity)blockEntity).isTilted());
        TrackBlockOutline.walkShapes(shape, TransformStack.of((PoseStack)ms), s -> {
            TrackBlockOutline.renderShape(s, ms, vb, holdingTrack ? Boolean.valueOf(canConnectFrom) : null);
            event.setCanceled(true);
        });
        ms.popPose();
    }

    public static void renderShape(VoxelShape s, PoseStack ms, VertexConsumer vb, Boolean valid) {
        PoseStack.Pose transform = ms.last();
        s.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
            float xDiff = (float)(x2 - x1);
            float yDiff = (float)(y2 - y1);
            float zDiff = (float)(z2 - z1);
            float length = Mth.sqrt((float)(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff));
            xDiff /= length;
            yDiff /= length;
            zDiff /= length;
            float r = 0.0f;
            float g = 0.0f;
            float b = 0.0f;
            if (valid != null && valid.booleanValue()) {
                g = 1.0f;
                b = 1.0f;
                r = 1.0f;
            }
            if (valid != null && !valid.booleanValue()) {
                r = 1.0f;
                b = 0.125f;
                g = 0.25f;
            }
            vb.addVertex(transform.pose(), (float)x1, (float)y1, (float)z1).setColor(r, g, b, 0.4f).setNormal(transform.copy(), xDiff, yDiff, zDiff);
            vb.addVertex(transform.pose(), (float)x2, (float)y2, (float)z2).setColor(r, g, b, 0.4f).setNormal(transform.copy(), xDiff, yDiff, zDiff);
        });
    }

    private static void walkShapes(TrackShape shape, TransformStack<?> msr, Consumer<VoxelShape> renderer) {
        float angle45 = 0.7853982f;
        if (shape == TrackShape.XO || shape == TrackShape.CR_NDX || shape == TrackShape.CR_PDX) {
            renderer.accept(AllShapes.TRACK_ORTHO.get(Direction.EAST));
        } else if (shape == TrackShape.ZO || shape == TrackShape.CR_NDZ || shape == TrackShape.CR_PDZ) {
            renderer.accept(AllShapes.TRACK_ORTHO.get(Direction.SOUTH));
        }
        if (shape.isPortal()) {
            for (Direction d : Iterate.horizontalDirections) {
                if (TrackShape.asPortal(d) != shape) continue;
                msr.rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)d)), Direction.UP);
                renderer.accept(LONG_ORTHO_OFFSET);
                return;
            }
        }
        if (shape == TrackShape.PD || shape == TrackShape.CR_PDX || shape == TrackShape.CR_PDZ) {
            msr.rotateCentered(angle45, Direction.UP);
            renderer.accept(LONG_ORTHO);
        } else if (shape == TrackShape.ND || shape == TrackShape.CR_NDX || shape == TrackShape.CR_NDZ) {
            msr.rotateCentered(-0.7853982f, Direction.UP);
            renderer.accept(LONG_ORTHO);
        }
        if (shape == TrackShape.CR_O) {
            renderer.accept(AllShapes.TRACK_CROSS);
        } else if (shape == TrackShape.CR_D) {
            msr.rotateCentered(angle45, Direction.UP);
            renderer.accept(LONG_CROSS);
        }
        if (shape != TrackShape.AE && shape != TrackShape.AN && shape != TrackShape.AW && shape != TrackShape.AS) {
            return;
        }
        msr.translate(0.0f, 1.0f, 0.0f);
        msr.rotateCentered((float)Math.PI - AngleHelper.rad((double)shape.getModelRotation()), Direction.UP);
        msr.rotateX(angle45);
        msr.translate(0.0f, -0.1875f, 0.0625f);
        renderer.accept(LONG_ORTHO);
    }

    static {
        LONG_CROSS = Shapes.or((VoxelShape)TrackVoxelShapes.longOrthogonalZ(), (VoxelShape)TrackVoxelShapes.longOrthogonalX());
        LONG_ORTHO = TrackVoxelShapes.longOrthogonalZ();
        LONG_ORTHO_OFFSET = TrackVoxelShapes.longOrthogonalZOffset();
    }

    public record BezierPointSelection(TrackBlockEntity blockEntity, BezierTrackPointLocation loc, Vec3 vec, Vec3 angles, Vec3 direction) {
    }
}
