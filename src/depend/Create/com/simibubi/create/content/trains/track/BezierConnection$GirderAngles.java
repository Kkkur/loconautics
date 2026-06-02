/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.track;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackRenderer;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.phys.Vec3;

public static class BezierConnection.GirderAngles {
    public final int length;
    public final Couple<PoseStack.Pose>[] beams;
    public final Couple<Couple<PoseStack.Pose>>[] beamCaps;
    public final BlockPos[] lightPosition;

    private BezierConnection.GirderAngles(BezierConnection bc) {
        int segmentCount = bc.getSegmentCount();
        this.length = segmentCount + 1;
        this.beams = new Couple[this.length];
        this.beamCaps = new Couple[this.length];
        this.lightPosition = new BlockPos[this.length];
        Couple previousOffsets = null;
        for (BezierConnection.Segment segment : bc) {
            int i = segment.index;
            boolean end = i == 0 || i == segmentCount;
            Vec3 leftGirder = segment.position.add(segment.normal.scale((double)0.965f));
            Vec3 rightGirder = segment.position.subtract(segment.normal.scale((double)0.965f));
            Vec3 upNormal = segment.derivative.normalize().cross(segment.normal);
            Vec3 firstGirderOffset = upNormal.scale(-0.5);
            Vec3 secondGirderOffset = upNormal.scale(-0.625);
            Vec3 leftTop = segment.position.add(segment.normal.scale(1.0)).add(firstGirderOffset);
            Vec3 rightTop = segment.position.subtract(segment.normal.scale(1.0)).add(firstGirderOffset);
            Vec3 leftBottom = leftTop.add(secondGirderOffset);
            Vec3 rightBottom = rightTop.add(secondGirderOffset);
            this.lightPosition[i] = BlockPos.containing((Position)leftGirder.add(rightGirder).scale(0.5));
            Couple offsets = Couple.create((Object)Couple.create((Object)leftTop, (Object)rightTop), (Object)Couple.create((Object)leftBottom, (Object)rightBottom));
            if (previousOffsets == null) {
                previousOffsets = offsets;
                continue;
            }
            this.beams[i] = Couple.create(null, null);
            this.beamCaps[i] = Couple.create((Object)Couple.create(null, null), (Object)Couple.create(null, null));
            float scale = end ? 2.3f : 2.2f;
            for (boolean first : Iterate.trueAndFalse) {
                Vec3 currentBeam = ((Vec3)((Couple)offsets.getFirst()).get(first)).add((Vec3)((Couple)offsets.getSecond()).get(first)).scale(0.5);
                Vec3 previousBeam = ((Vec3)((Couple)previousOffsets.getFirst()).get(first)).add((Vec3)((Couple)previousOffsets.getSecond()).get(first)).scale(0.5);
                Vec3 beamDiff = currentBeam.subtract(previousBeam);
                Vec3 beamAngles = TrackRenderer.getModelAngles(segment.normal, beamDiff);
                PoseStack poseStack = new PoseStack();
                ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)poseStack).translate(previousBeam)).rotateY((float)beamAngles.y)).rotateX((float)beamAngles.x)).rotateZ((float)beamAngles.z)).translate(0.0f, 0.125f + (float)(segment.index % 2 == 0 ? 1 : -1) / 2048.0f - 9.765625E-4f, -0.03125f).scale(1.0f, 1.0f, (float)beamDiff.length() * scale);
                this.beams[i].set(first, (Object)poseStack.last());
                for (boolean top : Iterate.trueAndFalse) {
                    Vec3 current = (Vec3)((Couple)offsets.get(top)).get(first);
                    Vec3 previous = (Vec3)((Couple)previousOffsets.get(top)).get(first);
                    Vec3 diff = current.subtract(previous);
                    Vec3 capAngles = TrackRenderer.getModelAngles(segment.normal, diff);
                    poseStack = new PoseStack();
                    ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)poseStack).translate(previous)).rotateY((float)capAngles.y)).rotateX((float)capAngles.x)).rotateZ((float)capAngles.z)).translate(0.0f, 0.125f + (float)(segment.index % 2 == 0 ? 1 : -1) / 2048.0f - 9.765625E-4f, -0.03125f).rotateZ(top ? 0.0f : 0.0f)).scale(1.0f, 1.0f, (float)diff.length() * scale);
                    ((Couple)this.beamCaps[i].get(top)).set(first, (Object)poseStack.last());
                }
            }
            previousOffsets = offsets;
        }
    }
}
