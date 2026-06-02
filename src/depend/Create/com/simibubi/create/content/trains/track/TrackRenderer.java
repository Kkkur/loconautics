/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4fc
 */
package com.simibubi.create.content.trains.track;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3fc;
import org.joml.Matrix4fc;

public class TrackRenderer
extends SafeBlockEntityRenderer<TrackBlockEntity> {
    public TrackRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(TrackBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        Level level = be.getLevel();
        if (VisualizationManager.supportsVisualization((LevelAccessor)level)) {
            return;
        }
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
        be.connections.values().forEach(bc -> TrackRenderer.renderBezierTurn(level, bc, ms, vb));
    }

    public static void renderBezierTurn(Level level, BezierConnection bc, PoseStack ms, VertexConsumer vb) {
        if (!bc.isPrimary()) {
            return;
        }
        ms.pushPose();
        BlockPos bePosition = (BlockPos)bc.bePositions.getFirst();
        BlockState air = Blocks.AIR.defaultBlockState();
        BezierConnection.SegmentAngles segment = bc.getBakedSegments();
        TrackRenderer.renderGirder(level, bc, ms, vb, bePosition);
        for (int i = 1; i < segment.length; ++i) {
            int light = LevelRenderer.getLightColor((BlockAndTintGetter)level, (BlockPos)segment.lightPosition[i].offset((Vec3i)bePosition));
            TrackMaterial.TrackModelHolder modelHolder = bc.getMaterial().getModelHolder();
            ((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)modelHolder.tie(), (BlockState)air).mulPose((Matrix4fc)segment.tieTransform[i].pose())).mulNormal((Matrix3fc)segment.tieTransform[i].normal())).light(light).renderInto(ms, vb);
            for (boolean first : Iterate.trueAndFalse) {
                PoseStack.Pose transform = (PoseStack.Pose)segment.railTransforms[i].get(first);
                ((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)(first ? modelHolder.leftSegment() : modelHolder.rightSegment()), (BlockState)air).mulPose((Matrix4fc)transform.pose())).mulNormal((Matrix3fc)transform.normal())).light(light).renderInto(ms, vb);
            }
        }
        ms.popPose();
    }

    private static void renderGirder(Level level, BezierConnection bc, PoseStack ms, VertexConsumer vb, BlockPos tePosition) {
        if (!bc.hasGirder) {
            return;
        }
        BlockState air = Blocks.AIR.defaultBlockState();
        BezierConnection.GirderAngles segment = bc.getBakedGirders();
        for (int i = 1; i < segment.length; ++i) {
            int light = LevelRenderer.getLightColor((BlockAndTintGetter)level, (BlockPos)segment.lightPosition[i].offset((Vec3i)tePosition));
            for (boolean first : Iterate.trueAndFalse) {
                PoseStack.Pose beamTransform = (PoseStack.Pose)segment.beams[i].get(first);
                ((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.GIRDER_SEGMENT_MIDDLE, (BlockState)air).mulPose((Matrix4fc)beamTransform.pose())).mulNormal((Matrix3fc)beamTransform.normal())).light(light).renderInto(ms, vb);
                for (boolean top : Iterate.trueAndFalse) {
                    PoseStack.Pose beamCapTransform = (PoseStack.Pose)((Couple)segment.beamCaps[i].get(top)).get(first);
                    ((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)(top ? AllPartialModels.GIRDER_SEGMENT_TOP : AllPartialModels.GIRDER_SEGMENT_BOTTOM), (BlockState)air).mulPose((Matrix4fc)beamCapTransform.pose())).mulNormal((Matrix3fc)beamCapTransform.normal())).light(light).renderInto(ms, vb);
                }
            }
        }
    }

    public static Vec3 getModelAngles(Vec3 normal, Vec3 diff) {
        double diffX = diff.x();
        double diffY = diff.y();
        double diffZ = diff.z();
        double len = Mth.sqrt((float)((float)(diffX * diffX + diffZ * diffZ)));
        double yaw = Mth.atan2((double)diffX, (double)diffZ);
        double pitch = Mth.atan2((double)len, (double)diffY) - 1.5707963267948966;
        Vec3 yawPitchNormal = VecHelper.rotate((Vec3)VecHelper.rotate((Vec3)new Vec3(0.0, 1.0, 0.0), (double)AngleHelper.deg((double)pitch), (Direction.Axis)Direction.Axis.X), (double)AngleHelper.deg((double)yaw), (Direction.Axis)Direction.Axis.Y);
        double signum = Math.signum(yawPitchNormal.dot(normal));
        if (Math.abs(signum) < 0.5) {
            signum = yawPitchNormal.distanceToSqr(normal) < 0.5 ? -1.0 : 1.0;
        }
        double dot = diff.cross(normal).normalize().dot(yawPitchNormal);
        double roll = Math.acos(Mth.clamp((double)dot, (double)-1.0, (double)1.0)) * signum;
        return new Vec3(pitch, yaw, roll);
    }

    public boolean shouldRenderOffScreen(TrackBlockEntity pBlockEntity) {
        return true;
    }

    public int getViewDistance() {
        return 192;
    }
}
