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
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.track;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackMaterial;
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
import org.jetbrains.annotations.Nullable;

private class TrackVisual.BezierTrackVisual {
    private final TransformedInstance[] ties;
    private final TransformedInstance[] left;
    private final TransformedInstance[] right;
    @Nullable
    private GirderVisual girder;

    private TrackVisual.BezierTrackVisual(BezierConnection bc) {
        this.girder = bc.hasGirder ? new GirderVisual(bc) : null;
        PoseStack pose = new PoseStack();
        TransformStack.of((PoseStack)pose).translate((Vec3i)TrackVisual.this.visualPos);
        int segCount = bc.getSegmentCount();
        this.ties = new TransformedInstance[segCount];
        this.left = new TransformedInstance[segCount];
        this.right = new TransformedInstance[segCount];
        TrackMaterial.TrackModelHolder modelHolder = bc.getMaterial().getModelHolder();
        TrackVisual.this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.flatChunk(modelHolder.tie())).createInstances((Instance[])this.ties);
        TrackVisual.this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.flatChunk(modelHolder.leftSegment())).createInstances((Instance[])this.left);
        TrackVisual.this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.flatChunk(modelHolder.rightSegment())).createInstances((Instance[])this.right);
        BezierConnection.SegmentAngles segment = bc.getBakedSegments();
        for (int i = 1; i < segment.length; ++i) {
            int modelIndex = i - 1;
            this.ties[modelIndex].setTransform(pose).mul(segment.tieTransform[i]).setChanged();
            for (boolean first : Iterate.trueAndFalse) {
                PoseStack.Pose transform = (PoseStack.Pose)segment.railTransforms[i].get(first);
                (first ? this.left : this.right)[modelIndex].setTransform(pose).mul(transform).setChanged();
            }
        }
    }

    void delete() {
        for (TransformedInstance d : this.ties) {
            d.delete();
        }
        for (TransformedInstance d : this.left) {
            d.delete();
        }
        for (TransformedInstance d : this.right) {
            d.delete();
        }
        if (this.girder != null) {
            this.girder.delete();
        }
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        for (TransformedInstance d : this.ties) {
            consumer.accept((Instance)d);
        }
        for (TransformedInstance d : this.left) {
            consumer.accept((Instance)d);
        }
        for (TransformedInstance d : this.right) {
            consumer.accept((Instance)d);
        }
        if (this.girder != null) {
            this.girder.collectCrumblingInstances(consumer);
        }
    }

    private class GirderVisual {
        private final Couple<TransformedInstance[]> beams;
        private final Couple<Couple<TransformedInstance[]>> beamCaps;

        private GirderVisual(BezierConnection bc) {
            PoseStack pose = new PoseStack();
            ((PoseTransformStack)TransformStack.of((PoseStack)pose).translate((Vec3i)TrackVisual.this.visualPos)).nudge((int)((BlockPos)bc.bePositions.getFirst()).asLong());
            int segCount = bc.getSegmentCount();
            this.beams = Couple.create(() -> new TransformedInstance[segCount]);
            this.beamCaps = Couple.create(() -> Couple.create(() -> new TransformedInstance[segCount]));
            this.beams.forEach(arg_0 -> ((Instancer)TrackVisual.this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.flatChunk(AllPartialModels.GIRDER_SEGMENT_MIDDLE))).createInstances(arg_0));
            this.beamCaps.forEachWithContext((c, top) -> {
                Model partialModel = SpecialModels.flatChunk(top != false ? AllPartialModels.GIRDER_SEGMENT_TOP : AllPartialModels.GIRDER_SEGMENT_BOTTOM);
                c.forEach(arg_0 -> ((Instancer)TrackVisual.this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, partialModel)).createInstances(arg_0));
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
}
