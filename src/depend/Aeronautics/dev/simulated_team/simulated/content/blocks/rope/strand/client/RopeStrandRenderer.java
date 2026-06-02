/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.math.OrientedBoundingBox3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.rope.strand.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ClientRopePoint;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ClientRopeStrand;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ZiplineClientManager;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.util.SimMathUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class RopeStrandRenderer {
    public static void render(SmartBlockEntity be, RopeStrandHolderBehavior ropeHolder, float partialTick, PoseStack ps, MultiBufferSource buffer) {
        Level level = be.getLevel();
        assert (level != null);
        BlockPos ownerPos = be.getBlockPos();
        SuperByteBuffer middle = CachedBuffers.partialFacing((PartialModel)SimPartialModels.ROPE, (BlockState)AllBlocks.ROPE.getDefaultState(), (Direction)Direction.NORTH);
        SuperByteBuffer knot = CachedBuffers.partialFacing((PartialModel)SimPartialModels.ROPE_KNOT, (BlockState)AllBlocks.ROPE.getDefaultState(), (Direction)Direction.NORTH);
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)be);
        Pose3dc containingPose = null;
        if (subLevel instanceof ClientSubLevel) {
            ClientSubLevel clientSubLevel = (ClientSubLevel)subLevel;
            containingPose = clientSubLevel.renderPose();
        }
        ClientRopeStrand rope = ropeHolder.getClientStrand();
        float rad = 0.1875f;
        if (ropeHolder.ownsRope() && rope != null && ropeHolder.getClientStrand() != null) {
            ClientRopeStrand clientStrand = ropeHolder.getClientStrand();
            ObjectArrayList<ClientRopePoint> points = clientStrand.getPoints();
            if (points.size() <= 1) {
                return;
            }
            ObjectArrayList<RopeRenderPoint> ropeRenderPoints = RopeStrandRenderer.buildRenderPoints(partialTick, points);
            if (ropeRenderPoints.isEmpty()) {
                return;
            }
            ps.pushPose();
            for (int i = 1; i < ropeRenderPoints.size(); ++i) {
                RopeRenderPoint renderPoint0 = (RopeRenderPoint)ropeRenderPoints.get(i - 1);
                RopeRenderPoint renderPoint1 = (RopeRenderPoint)ropeRenderPoints.get(i);
                Vector3d globalRenderPos = new Vector3d((Vector3dc)renderPoint0.position());
                Vector3d renderPos = renderPoint0.position();
                Quaternionf orientation = renderPoint0.orientation();
                double length = renderPoint1.position().distance((Vector3dc)renderPoint0.position());
                if (containingPose != null) {
                    containingPose.transformPositionInverse(renderPos);
                    orientation.premul((Quaternionfc)new Quaternionf(containingPose.orientation()).conjugate());
                }
                ps.pushPose();
                ps.translate(renderPos.x - (double)ownerPos.getX(), renderPos.y - (double)ownerPos.getY(), renderPos.z - (double)ownerPos.getZ());
                ps.mulPose(orientation);
                ps.translate(-0.5, -0.5, -0.5);
                BlockPos pos = BlockPos.containing((double)globalRenderPos.x, (double)globalRenderPos.y, (double)globalRenderPos.z);
                int worldLight = LevelRenderer.getLightColor((BlockAndTintGetter)level, (BlockPos)pos);
                if (i > 1) {
                    knot.light(worldLight).renderInto(ps, vb);
                }
                ps.translate(0.0, 0.5, 0.0);
                ps.scale(1.0f, (float)length, 1.0f);
                middle.light(worldLight).renderInto(ps, vb);
                ps.popPose();
            }
            ps.popPose();
            RopeRenderPoint last = (RopeRenderPoint)ropeRenderPoints.getLast();
            if (containingPose != null) {
                Vector3d renderPos = last.position();
                Quaternionf orientation = last.orientation();
                containingPose.transformPositionInverse(renderPos);
                orientation.premul((Quaternionfc)new Quaternionf(containingPose.orientation()).conjugate());
            }
            if (Objects.equals(ZiplineClientManager.hoveringRope, clientStrand.getUuid())) {
                RopeStrandRenderer.renderOutline(ps, buffer, 0.1875f, ropeRenderPoints, ownerPos);
            }
        }
    }

    private static void renderOutline(PoseStack ps, MultiBufferSource buffer, float rad, ObjectArrayList<RopeRenderPoint> ropeRenderPoints, BlockPos ownerPos) {
        Vector3d previousCorner = new Vector3d();
        Vector3d currentCorner = new Vector3d();
        Vector3d cornerDiff = new Vector3d();
        Vector3d[] ropeCorners = new Vector3d[]{new Vector3d((double)(-rad), 0.0, (double)(-rad)), new Vector3d((double)(-rad), 0.0, (double)rad), new Vector3d((double)rad, 0.0, (double)rad), new Vector3d((double)rad, 0.0, (double)(-rad))};
        VertexConsumer linesVB = buffer.getBuffer(RenderType.lines());
        Matrix4f pose = ps.last().pose();
        for (int i = 0; i < ropeRenderPoints.size() + 1; ++i) {
            RopeRenderPoint renderPoint0 = (RopeRenderPoint)ropeRenderPoints.get(Math.max(0, i - 1));
            RopeRenderPoint renderPoint1 = (RopeRenderPoint)ropeRenderPoints.get(Math.min(ropeRenderPoints.size() - 1, i));
            boolean start = i == 0;
            boolean end = i == ropeRenderPoints.size();
            for (Vector3d ropeCorner : ropeCorners) {
                renderPoint0.orientation().transform((Vector3dc)(start ? ropeCorner.rotateY(1.5707963267948966, previousCorner) : ropeCorner), previousCorner).add((Vector3dc)renderPoint0.position()).sub((double)ownerPos.getX(), (double)ownerPos.getY(), (double)ownerPos.getZ());
                renderPoint1.orientation().transform((Vector3dc)(end ? ropeCorner.rotateY(1.5707963267948966, ropeCorner) : ropeCorner), currentCorner).add((Vector3dc)renderPoint1.position()).sub((double)ownerPos.getX(), (double)ownerPos.getY(), (double)ownerPos.getZ());
                currentCorner.sub((Vector3dc)previousCorner, cornerDiff).normalize();
                linesVB.addVertex(pose, (float)previousCorner.x, (float)previousCorner.y, (float)previousCorner.z).setColor(0.0f, 0.0f, 0.0f, 0.4f).setNormal(ps.last(), (float)cornerDiff.x, (float)cornerDiff.y, (float)cornerDiff.z);
                linesVB.addVertex(pose, (float)currentCorner.x, (float)currentCorner.y, (float)currentCorner.z).setColor(0.0f, 0.0f, 0.0f, 0.4f).setNormal(ps.last(), (float)cornerDiff.x, (float)cornerDiff.y, (float)cornerDiff.z);
            }
        }
    }

    @NotNull
    private static ObjectArrayList<RopeRenderPoint> buildRenderPoints(float partialTick, List<ClientRopePoint> inputPoints) {
        Quaternionf runningRotation;
        ObjectArrayList ropeRenderPoints = new ObjectArrayList();
        ObjectArrayList points = new ObjectArrayList(inputPoints);
        while (points.size() >= 2 && ((ClientRopePoint)points.getFirst()).position().distanceSquared((Vector3dc)((ClientRopePoint)points.get(1)).position()) < 0.001) {
            points.removeFirst();
        }
        if (points.size() <= 1) {
            return new ObjectArrayList();
        }
        Vector3d pointZeroPosition = ((ClientRopePoint)points.get(0)).renderPos(partialTick, new Vector3d());
        Vector3d pointOnePosition = ((ClientRopePoint)points.get(1)).renderPos(partialTick, new Vector3d());
        Vector3d normal = pointOnePosition.sub((Vector3dc)pointZeroPosition, new Vector3d()).normalize();
        if (normal.dot(OrientedBoundingBox3d.UP) < 0.0) {
            runningRotation = SimMathUtils.getQuaternionfFromVectorRotation((Vector3dc)new Vector3d(0.0, -1.0, 0.0), (Vector3dc)normal);
            runningRotation.rotateZ((float)Math.PI);
        } else {
            runningRotation = SimMathUtils.getQuaternionfFromVectorRotation((Vector3dc)new Vector3d(0.0, 1.0, 0.0), (Vector3dc)normal);
        }
        ropeRenderPoints.add((Object)new RopeRenderPoint(new Quaternionf((Quaternionfc)runningRotation), new Vector3d((Vector3dc)pointZeroPosition)));
        Vector3d runningNormal = new Vector3d();
        Vector3d bPos = new Vector3d();
        Vector3d aPos = new Vector3d();
        for (int i = 2; i < points.size(); ++i) {
            ClientRopePoint pointA = (ClientRopePoint)points.get(i - 1);
            ClientRopePoint pointB = (ClientRopePoint)points.get(i);
            runningNormal.set((Vector3dc)pointB.renderPos(partialTick, bPos)).sub((Vector3dc)pointA.renderPos(partialTick, aPos)).normalize();
            if (runningNormal.dot(OrientedBoundingBox3d.UP) < -0.15) {
                runningRotation.set((Quaternionfc)SimMathUtils.getQuaternionfFromVectorRotation((Vector3dc)new Vector3d(0.0, -1.0, 0.0), (Vector3dc)runningNormal));
                runningRotation.rotateZ((float)Math.PI);
            } else {
                runningRotation.set((Quaternionfc)SimMathUtils.getQuaternionfFromVectorRotation((Vector3dc)new Vector3d(0.0, 1.0, 0.0), (Vector3dc)runningNormal));
            }
            ropeRenderPoints.add((Object)new RopeRenderPoint(new Quaternionf((Quaternionfc)runningRotation), pointA.renderPos(partialTick, new Vector3d())));
            normal.set((Vector3dc)runningNormal);
        }
        ropeRenderPoints.add((Object)new RopeRenderPoint(new Quaternionf((Quaternionfc)runningRotation), ((ClientRopePoint)points.getLast()).renderPos(partialTick, new Vector3d())));
        return ropeRenderPoints;
    }

    public record RopeRenderPoint(Quaternionf orientation, Vector3d position) {
    }
}
