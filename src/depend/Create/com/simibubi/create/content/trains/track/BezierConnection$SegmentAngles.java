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
 *  org.jetbrains.annotations.NotNull
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
import org.jetbrains.annotations.NotNull;

public static class BezierConnection.SegmentAngles {
    public final int length;
    @NotNull
    public final PoseStack.Pose[] tieTransform;
    @NotNull
    public final Couple<PoseStack.Pose>[] railTransforms;
    @NotNull
    public final BlockPos[] lightPosition;

    private BezierConnection.SegmentAngles(BezierConnection bc) {
        int segmentCount = bc.getSegmentCount();
        this.length = segmentCount + 1;
        this.tieTransform = new PoseStack.Pose[segmentCount + 1];
        this.railTransforms = new Couple[segmentCount + 1];
        this.lightPosition = new BlockPos[segmentCount + 1];
        Couple previousOffsets = null;
        for (BezierConnection.Segment segment : bc) {
            int i = segment.index;
            boolean end = i == 0 || i == segmentCount;
            Couple railOffsets = Couple.create((Object)segment.position.add(segment.normal.scale((double)0.965f)), (Object)segment.position.subtract(segment.normal.scale((double)0.965f)));
            Vec3 railMiddle = ((Vec3)railOffsets.getFirst()).add((Vec3)railOffsets.getSecond()).scale(0.5);
            if (previousOffsets == null) {
                previousOffsets = railOffsets;
                continue;
            }
            Vec3 prevMiddle = ((Vec3)previousOffsets.getFirst()).add((Vec3)previousOffsets.getSecond()).scale(0.5);
            Vec3 tieAngles = TrackRenderer.getModelAngles(segment.normal, railMiddle.subtract(prevMiddle));
            this.lightPosition[i] = BlockPos.containing((Position)railMiddle);
            this.railTransforms[i] = Couple.create(null, null);
            PoseStack poseStack = new PoseStack();
            ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)poseStack).translate(prevMiddle)).rotateY((float)tieAngles.y)).rotateX((float)tieAngles.x)).rotateZ((float)tieAngles.z)).translate(-0.5f, -0.12890625f, 0.0f);
            this.tieTransform[i] = poseStack.last();
            float scale = end ? 2.2f : 2.1f;
            for (boolean first : Iterate.trueAndFalse) {
                Vec3 railI = (Vec3)railOffsets.get(first);
                Vec3 prevI = (Vec3)previousOffsets.get(first);
                Vec3 diff = railI.subtract(prevI);
                Vec3 anglesI = TrackRenderer.getModelAngles(segment.normal, diff);
                poseStack = new PoseStack();
                ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)poseStack).translate(prevI)).rotateY((float)anglesI.y)).rotateX((float)anglesI.x)).rotateZ((float)anglesI.z)).translate(0.0f, -0.12890625f, -0.03125f).scale(1.0f, 1.0f, (float)diff.length() * scale);
                this.railTransforms[i].set(first, (Object)poseStack.last());
            }
            previousOffsets = railOffsets;
        }
    }
}
