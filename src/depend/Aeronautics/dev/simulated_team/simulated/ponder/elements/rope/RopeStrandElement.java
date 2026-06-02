/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.AllBlocks
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.ryanhcode.sable.api.math.OrientedBoundingBox3d
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.ponder.api.element.AnimatedSceneElement
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.element.AnimatedSceneElementBase
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.ponder.elements.rope;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.RopeStrandRenderer;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.ponder.elements.rope.PonderRopePose;
import dev.simulated_team.simulated.ponder.instructions.ModifyRopeInstruction;
import dev.simulated_team.simulated.util.SimMathUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.ponder.api.element.AnimatedSceneElement;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.element.AnimatedSceneElementBase;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class RopeStrandElement
extends AnimatedSceneElementBase
implements AnimatedSceneElement {
    public final PonderRopePose pose;
    public final PonderRopePose lastPose;
    public final PonderRopePose startPose;
    public final PonderRopePose scenePose;

    public RopeStrandElement(Vec3 from, Vec3 to, double length, double sog, float floorHeight) {
        this.pose = new PonderRopePose(JOMLConversion.toJOML((Position)from), JOMLConversion.toJOML((Position)to), length, sog, floorHeight);
        this.lastPose = new PonderRopePose(JOMLConversion.toJOML((Position)from), JOMLConversion.toJOML((Position)to), length, sog, floorHeight);
        this.startPose = new PonderRopePose(JOMLConversion.toJOML((Position)from), JOMLConversion.toJOML((Position)to), length, sog, floorHeight);
        this.scenePose = new PonderRopePose(JOMLConversion.toJOML((Position)from), JOMLConversion.toJOML((Position)to), length, sog, floorHeight);
    }

    public RopeStrandElement(Vec3 from, Vec3 to, double length, double sog) {
        this.pose = new PonderRopePose(JOMLConversion.toJOML((Position)from), JOMLConversion.toJOML((Position)to), length, sog, -3.4028234663852886E38);
        this.lastPose = new PonderRopePose(JOMLConversion.toJOML((Position)from), JOMLConversion.toJOML((Position)to), length, sog, -3.4028234663852886E38);
        this.startPose = new PonderRopePose(JOMLConversion.toJOML((Position)from), JOMLConversion.toJOML((Position)to), length, sog, -3.4028234663852886E38);
        this.scenePose = new PonderRopePose(JOMLConversion.toJOML((Position)from), JOMLConversion.toJOML((Position)to), length, sog, -3.4028234663852886E38);
    }

    public void reset(PonderScene scene) {
        super.reset(scene);
        this.pose.set(this.startPose);
        this.lastPose.set(this.startPose);
    }

    public void lerp(Vec3 from, Vec3 to, double length, double sog, double t) {
        this.lastPose.set(this.pose);
        this.pose.lerp(from, to, length, sog, t);
    }

    public void set(PonderRopePose pose) {
        this.lastPose.set(this.pose);
        this.pose.set(pose);
    }

    public ModifyRopeInstruction modify(int duration) {
        return new ModifyRopeInstruction(duration, this);
    }

    protected void renderLast(PonderLevel world, MultiBufferSource buffer, GuiGraphics graphics, float fade, float pt) {
        SuperByteBuffer middle = CachedBuffers.partialFacing((PartialModel)SimPartialModels.ROPE, (BlockState)AllBlocks.ROPE.getDefaultState(), (Direction)Direction.NORTH);
        SuperByteBuffer knot = CachedBuffers.partialFacing((PartialModel)SimPartialModels.ROPE_KNOT, (BlockState)AllBlocks.ROPE.getDefaultState(), (Direction)Direction.NORTH);
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        PoseStack ps = graphics.pose();
        PonderRopePose currentPose = new PonderRopePose();
        currentPose.set(this.lastPose);
        currentPose.lerp(this.pose, pt);
        ObjectArrayList points = new ObjectArrayList();
        Vector3d currentPos = new Vector3d();
        int knots = (int)Math.ceil(currentPose.length) + 1;
        double extra = currentPose.length - (double)knots;
        for (int i = 0; i < knots; ++i) {
            double t = 1.0 - (double)i / ((double)knots + extra);
            Vector3d pos = currentPose.start.lerp((Vector3dc)currentPose.end, Math.max(0.0, t), currentPos);
            double y = Math.pow(t - 0.5, 2.0) * 4.0;
            pos.sub(0.0, Mth.clamp((double)(1.0 - y), (double)0.0, (double)1.0) * currentPose.sog, 0.0);
            pos.set(pos.x, Math.max(pos.y, currentPose.floorHeight), pos.z);
            points.add(new Vector3d((Vector3dc)pos));
        }
        ObjectArrayList<RopeStrandRenderer.RopeRenderPoint> renderPoints = RopeStrandElement.buildRenderPoints(pt, (List<Vector3d>)points);
        ps.pushPose();
        this.applyFade(ps, pt);
        ps.translate(currentPose.start.x, currentPose.start.y, currentPose.start.z);
        for (int i = 1; i < renderPoints.size(); ++i) {
            RopeStrandRenderer.RopeRenderPoint renderPoint0 = (RopeStrandRenderer.RopeRenderPoint)renderPoints.get(i - 1);
            RopeStrandRenderer.RopeRenderPoint renderPoint1 = (RopeStrandRenderer.RopeRenderPoint)renderPoints.get(i);
            Vector3d globalRenderPos = new Vector3d((Vector3dc)renderPoint0.position());
            Vector3d renderPos = renderPoint0.position();
            Quaternionf orientation = renderPoint0.orientation();
            double length = renderPoint1.position().distance((Vector3dc)renderPoint0.position());
            ps.pushPose();
            ps.translate(renderPos.x - currentPose.start.x, renderPos.y - currentPose.start.y, renderPos.z - currentPose.start.z);
            ps.mulPose(orientation);
            ps.translate(-0.5, -0.5, -0.5);
            BlockPos pos = BlockPos.containing((double)globalRenderPos.x, (double)globalRenderPos.y, (double)globalRenderPos.z);
            int worldLight = 240;
            knot.light(240).renderInto(ps, vb);
            ps.pushPose();
            ps.translate(0.0, 0.5, 0.0);
            ps.scale(1.0f, (float)length, 1.0f);
            middle.light(240).renderInto(ps, vb);
            ps.popPose();
            if (renderPoint1 == renderPoints.getLast()) {
                ps.translate(0.0, length, 0.0);
                knot.light(240).renderInto(ps, vb);
            }
            ps.popPose();
        }
        ps.popPose();
    }

    @NotNull
    private static ObjectArrayList<RopeStrandRenderer.RopeRenderPoint> buildRenderPoints(float partialTick, List<Vector3d> inputPoints) {
        Quaternionf runningRotation;
        ObjectArrayList ropeRenderPoints = new ObjectArrayList();
        ObjectArrayList points = new ObjectArrayList(inputPoints);
        while (points.size() >= 2 && ((Vector3d)points.getFirst()).distanceSquared((Vector3dc)points.get(1)) < 1.0E-6) {
            points.removeFirst();
        }
        if (points.size() <= 1) {
            return new ObjectArrayList();
        }
        Vector3dc pointZeroPosition = (Vector3dc)points.get(0);
        Vector3dc pointOnePosition = (Vector3dc)points.get(1);
        Vector3d normal = pointOnePosition.sub(pointZeroPosition, new Vector3d()).normalize();
        if (normal.dot(OrientedBoundingBox3d.UP) < 0.0) {
            runningRotation = SimMathUtils.getQuaternionfFromVectorRotation((Vector3dc)new Vector3d(0.0, -1.0, 0.0), (Vector3dc)normal);
            runningRotation.rotateZ((float)Math.PI);
        } else {
            runningRotation = SimMathUtils.getQuaternionfFromVectorRotation((Vector3dc)new Vector3d(0.0, 1.0, 0.0), (Vector3dc)normal);
        }
        ropeRenderPoints.add((Object)new RopeStrandRenderer.RopeRenderPoint(new Quaternionf((Quaternionfc)runningRotation), new Vector3d(pointZeroPosition)));
        Vector3d runningNormal = new Vector3d();
        for (int i = 2; i < points.size(); ++i) {
            Vector3d pointA = (Vector3d)points.get(i - 1);
            Vector3d pointB = (Vector3d)points.get(i);
            runningNormal.set((Vector3dc)pointB).sub((Vector3dc)pointA).normalize();
            if (runningNormal.dot(OrientedBoundingBox3d.UP) < -0.15) {
                runningRotation.set((Quaternionfc)SimMathUtils.getQuaternionfFromVectorRotation((Vector3dc)new Vector3d(0.0, -1.0, 0.0), (Vector3dc)runningNormal));
                runningRotation.rotateZ((float)Math.PI);
            } else {
                runningRotation.set((Quaternionfc)SimMathUtils.getQuaternionfFromVectorRotation((Vector3dc)new Vector3d(0.0, 1.0, 0.0), (Vector3dc)runningNormal));
            }
            ropeRenderPoints.add((Object)new RopeStrandRenderer.RopeRenderPoint(new Quaternionf((Quaternionfc)runningRotation), pointA));
            normal.set((Vector3dc)runningNormal);
        }
        ropeRenderPoints.add((Object)new RopeStrandRenderer.RopeRenderPoint(new Quaternionf((Quaternionfc)runningRotation), (Vector3d)points.getLast()));
        return ropeRenderPoints;
    }
}
