package com.lycoris.loconautics.content.steelcable;

import com.lycoris.loconautics.client.LoconauticsPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.ryanhcode.sable.Sable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ClientRopePoint;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ClientRopeStrand;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ZiplineClientManager;
import dev.simulated_team.simulated.util.SimMathUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.List;
import java.util.Objects;

/**
 * Renderer for the Steel Cable strand.
 * Identical to Simulated's RopeStrandRenderer except:
 *  - uses loconautics partial models (steel_cable/rope, steel_cable/knot)
 *  - scales segment length by 2× so the cable can span twice the normal distance
 */
public class SteelCableStrandRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SteelCableStrandRenderer.class);
    private static boolean loggedOnce = false;

    /** 2× multiplier applied to each segment's rendered length. */
    private static final float LENGTH_SCALE = 2.0f;

    private static final float RAD = 0.1875f;

    public static void render(SmartBlockEntity be, RopeStrandHolderBehavior ropeHolder,
                              float partialTick, PoseStack ps, MultiBufferSource buffer) {
        Level level = be.getLevel();
        assert level != null;
        BlockPos ownerPos = be.getBlockPos();

        BlockState neutralState = Blocks.STONE.defaultBlockState();

        SuperByteBuffer middle = CachedBuffers.partial(
                LoconauticsPartialModels.STEEL_CABLE_ROPE,
                neutralState);
        SuperByteBuffer knot = CachedBuffers.partial(
                LoconauticsPartialModels.STEEL_CABLE_KNOT,
                neutralState);

        if (!loggedOnce) {
            LOGGER.info("[SteelCable] STEEL_CABLE_ROPE partial model: {}", LoconauticsPartialModels.STEEL_CABLE_ROPE);
            LOGGER.info("[SteelCable] STEEL_CABLE_KNOT partial model: {}", LoconauticsPartialModels.STEEL_CABLE_KNOT);
            LOGGER.info("[SteelCable] middle buffer: {}", middle);
            LOGGER.info("[SteelCable] knot buffer: {}", knot);
            loggedOnce = true;
        }

        VertexConsumer vb = buffer.getBuffer(RenderType.solid());

        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity) be);
        Pose3dc containingPose = null;
        if (subLevel instanceof ClientSubLevel clientSubLevel) {
            containingPose = clientSubLevel.renderPose();
        }

        ClientRopeStrand rope = ropeHolder.getClientStrand();
        if (!ropeHolder.ownsRope() || rope == null || ropeHolder.getClientStrand() == null)
            return;

        ClientRopeStrand clientStrand = ropeHolder.getClientStrand();
        ObjectArrayList<ClientRopePoint> points = clientStrand.getPoints();
        if (points.size() <= 1) return;

        ObjectArrayList<RopeRenderPoint> ropeRenderPoints = buildRenderPoints(partialTick, points);
        if (ropeRenderPoints.isEmpty()) return;

        ps.pushPose();
        for (int i = 1; i < ropeRenderPoints.size(); ++i) {
            RopeRenderPoint rp0 = ropeRenderPoints.get(i - 1);
            RopeRenderPoint rp1 = ropeRenderPoints.get(i);

            Vector3d globalRenderPos = new Vector3d((Vector3dc) rp0.position());
            Vector3d renderPos = rp0.position();
            Quaternionf orientation = rp0.orientation();

            // 2× the actual point-to-point distance
            double length = rp1.position().distance((Vector3dc) rp0.position()) * LENGTH_SCALE;

            if (containingPose != null) {
                containingPose.transformPositionInverse(renderPos);
                orientation.premul((Quaternionfc) new Quaternionf(containingPose.orientation()).conjugate());
            }

            ps.pushPose();
            ps.translate(
                    renderPos.x - ownerPos.getX(),
                    renderPos.y - ownerPos.getY(),
                    renderPos.z - ownerPos.getZ());
            ps.mulPose(orientation);
            ps.translate(-0.5, -0.5, -0.5);

            BlockPos pos = BlockPos.containing(globalRenderPos.x, globalRenderPos.y, globalRenderPos.z);
            int worldLight = LevelRenderer.getLightColor((BlockAndTintGetter) level, pos);

            if (i > 1) {
                knot.light(worldLight).renderInto(ps, vb);
            }
            ps.translate(0.0, 0.5, 0.0);
            ps.scale(1.0f, (float) length, 1.0f);
            middle.light(worldLight).renderInto(ps, vb);
            ps.popPose();
        }
        ps.popPose();

        RopeRenderPoint last = ropeRenderPoints.getLast();
        if (containingPose != null) {
            Vector3d renderPos = last.position();
            Quaternionf orientation = last.orientation();
            containingPose.transformPositionInverse(renderPos);
            orientation.premul((Quaternionfc) new Quaternionf(containingPose.orientation()).conjugate());
        }

        if (Objects.equals(ZiplineClientManager.hoveringRope, clientStrand.getUuid())) {
            renderOutline(ps, buffer, RAD, ropeRenderPoints, ownerPos);
        }
    }

    private static void renderOutline(PoseStack ps, MultiBufferSource buffer, float rad,
                                      ObjectArrayList<RopeRenderPoint> points, BlockPos ownerPos) {
        Vector3d previousCorner = new Vector3d();
        Vector3d currentCorner = new Vector3d();
        Vector3d cornerDiff = new Vector3d();
        Vector3d[] corners = {
                new Vector3d(-rad, 0, -rad), new Vector3d(-rad, 0, rad),
                new Vector3d(rad,  0,  rad), new Vector3d(rad,  0, -rad)
        };
        VertexConsumer linesVB = buffer.getBuffer(RenderType.lines());
        Matrix4f pose = ps.last().pose();
        for (int i = 0; i < points.size() + 1; ++i) {
            RopeRenderPoint rp0 = points.get(Math.max(0, i - 1));
            RopeRenderPoint rp1 = points.get(Math.min(points.size() - 1, i));
            boolean start = i == 0;
            boolean end   = i == points.size();
            for (Vector3d corner : corners) {
                rp0.orientation().transform((Vector3dc)(start ? corner.rotateY(Math.PI / 2, previousCorner) : corner), previousCorner)
                        .add((Vector3dc) rp0.position()).sub(ownerPos.getX(), ownerPos.getY(), ownerPos.getZ());
                rp1.orientation().transform((Vector3dc)(end ? corner.rotateY(Math.PI / 2, corner) : corner), currentCorner)
                        .add((Vector3dc) rp1.position()).sub(ownerPos.getX(), ownerPos.getY(), ownerPos.getZ());
                currentCorner.sub((Vector3dc) previousCorner, cornerDiff).normalize();
                linesVB.addVertex(pose, (float) previousCorner.x, (float) previousCorner.y, (float) previousCorner.z)
                        .setColor(0f, 0f, 0f, 0.4f)
                        .setNormal(ps.last(), (float) cornerDiff.x, (float) cornerDiff.y, (float) cornerDiff.z);
                linesVB.addVertex(pose, (float) currentCorner.x, (float) currentCorner.y, (float) currentCorner.z)
                        .setColor(0f, 0f, 0f, 0.4f)
                        .setNormal(ps.last(), (float) cornerDiff.x, (float) cornerDiff.y, (float) cornerDiff.z);
            }
        }
    }

    @NotNull
    private static ObjectArrayList<RopeRenderPoint> buildRenderPoints(float partialTick,
                                                                      List<ClientRopePoint> inputPoints) {
        ObjectArrayList<RopeRenderPoint> result = new ObjectArrayList<>();
        ObjectArrayList<ClientRopePoint> points = new ObjectArrayList<>(inputPoints);

        while (points.size() >= 2
                && points.getFirst().position().distanceSquared((Vector3dc) points.get(1).position()) < 0.001) {
            points.removeFirst();
        }
        if (points.size() <= 1) return new ObjectArrayList<>();

        Vector3d p0 = points.get(0).renderPos(partialTick, new Vector3d());
        Vector3d p1 = points.get(1).renderPos(partialTick, new Vector3d());
        Vector3d normal = p1.sub((Vector3dc) p0, new Vector3d()).normalize();

        Quaternionf running;
        if (normal.dot(OrientedBoundingBox3d.UP) < 0.0) {
            running = SimMathUtils.getQuaternionfFromVectorRotation(new Vector3d(0, -1, 0), (Vector3dc) normal);
            running.rotateZ((float) Math.PI);
        } else {
            running = SimMathUtils.getQuaternionfFromVectorRotation(new Vector3d(0, 1, 0), (Vector3dc) normal);
        }
        result.add(new RopeRenderPoint(new Quaternionf((Quaternionfc) running), new Vector3d((Vector3dc) p0)));

        Vector3d runningNormal = new Vector3d();
        Vector3d bPos = new Vector3d();
        Vector3d aPos = new Vector3d();
        for (int i = 2; i < points.size(); ++i) {
            ClientRopePoint pointA = points.get(i - 1);
            ClientRopePoint pointB = points.get(i);
            runningNormal.set((Vector3dc) pointB.renderPos(partialTick, bPos))
                    .sub((Vector3dc) pointA.renderPos(partialTick, aPos)).normalize();
            if (runningNormal.dot(OrientedBoundingBox3d.UP) < -0.15) {
                running.set((Quaternionfc) SimMathUtils.getQuaternionfFromVectorRotation(new Vector3d(0, -1, 0), (Vector3dc) runningNormal));
                running.rotateZ((float) Math.PI);
            } else {
                running.set((Quaternionfc) SimMathUtils.getQuaternionfFromVectorRotation(new Vector3d(0, 1, 0), (Vector3dc) runningNormal));
            }
            result.add(new RopeRenderPoint(new Quaternionf((Quaternionfc) running), pointA.renderPos(partialTick, new Vector3d())));
            normal.set((Vector3dc) runningNormal);
        }
        result.add(new RopeRenderPoint(new Quaternionf((Quaternionfc) running),
                points.getLast().renderPos(partialTick, new Vector3d())));
        return result;
    }

    public record RopeRenderPoint(Quaternionf orientation, Vector3d position) {}
}