/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.instance.Instancer
 *  dev.engine_room.flywheel.api.model.Model
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 */
package com.simibubi.create.content.trains.track;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.foundation.render.SpecialModels;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.function.Consumer;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

private class TrackVisual.BezierTrackVisual.GirderVisual {
    private final Couple<TransformedInstance[]> beams;
    private final Couple<Couple<TransformedInstance[]>> beamCaps;

    private TrackVisual.BezierTrackVisual.GirderVisual(BezierConnection bc) {
        PoseStack pose = new PoseStack();
        ((PoseTransformStack)TransformStack.of((PoseStack)pose).translate((Vec3i)BezierTrackVisual.this.this$0.visualPos)).nudge((int)((BlockPos)bc.bePositions.getFirst()).asLong());
        int segCount = bc.getSegmentCount();
        this.beams = Couple.create(() -> new TransformedInstance[segCount]);
        this.beamCaps = Couple.create(() -> Couple.create(() -> new TransformedInstance[segCount]));
        this.beams.forEach(arg_0 -> ((Instancer)BezierTrackVisual.this.this$0.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.flatChunk(AllPartialModels.GIRDER_SEGMENT_MIDDLE))).createInstances(arg_0));
        this.beamCaps.forEachWithContext((c, top) -> {
            Model partialModel = SpecialModels.flatChunk(top != false ? AllPartialModels.GIRDER_SEGMENT_TOP : AllPartialModels.GIRDER_SEGMENT_BOTTOM);
            c.forEach(arg_0 -> ((Instancer)BezierTrackVisual.this.this$0.instancerProvider().instancer(InstanceTypes.TRANSFORMED, partialModel)).createInstances(arg_0));
        });
        BezierConnection.GirderAngles segment = bc.getBakedGirders();
        for (int i = 1; i < segment.length; ++i) {
            int modelIndex = i - 1;
            for (boolean first : Iterate.trueAndFalse) {
                PoseStack.Pose beamTransform = (PoseStack.Pose)segment.beams[i].get(first);
                ((TransformedInstance[])this.beams.get(first))[modelIndex].setTransform(pose).mul(beamTransform).setChanged();
                for (boolean top2 : Iterate.trueAndFalse) {
                    PoseStack.Pose beamCapTransform = (PoseStack.Pose)((Couple)segment.beamCaps[i].get(top2)).get(first);
                    ((TransformedInstance[])((Couple)this.beamCaps.get(top2)).get(first))[modelIndex].setTransform(pose).mul(beamCapTransform).setChanged();
                }
            }
        }
    }

    void delete() {
        this.beams.forEach(arr -> {
            for (TransformedInstance d : arr) {
                d.delete();
            }
        });
        this.beamCaps.forEach(c -> c.forEach(arr -> {
            for (TransformedInstance d : arr) {
                d.delete();
            }
        }));
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        this.beams.forEach(arr -> {
            for (TransformedInstance d : arr) {
                consumer.accept((Instance)d);
            }
        });
        this.beamCaps.forEach(c -> c.forEach(arr -> {
            for (TransformedInstance d : arr) {
                consumer.accept((Instance)d);
            }
        }));
    }
}
