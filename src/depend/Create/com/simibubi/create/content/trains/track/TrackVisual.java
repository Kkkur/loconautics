/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.instance.Instancer
 *  dev.engine_room.flywheel.api.model.Model
 *  dev.engine_room.flywheel.api.visual.BlockEntityVisual
 *  dev.engine_room.flywheel.api.visual.SectionTrackedVisual$SectionCollector
 *  dev.engine_room.flywheel.api.visual.ShaderLightVisual
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  dev.engine_room.flywheel.lib.visual.AbstractVisual
 *  it.unimi.dsi.fastutil.longs.LongArraySet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.AABB
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.UnknownNullability
 */
package com.simibubi.create.content.trains.track;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.render.SpecialModels;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visual.SectionTrackedVisual;
import dev.engine_room.flywheel.api.visual.ShaderLightVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.engine_room.flywheel.lib.visual.AbstractVisual;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public class TrackVisual
extends AbstractVisual
implements BlockEntityVisual<TrackBlockEntity>,
ShaderLightVisual {
    private final List<BezierTrackVisual> visuals = new ArrayList<BezierTrackVisual>();
    protected final TrackBlockEntity blockEntity;
    protected final BlockPos pos;
    protected final BlockPos visualPos;
    protected // Could not load outer class - annotation placement on inner may be incorrect
     @UnknownNullability SectionTrackedVisual.SectionCollector lightSections;

    public TrackVisual(VisualizationContext context, TrackBlockEntity track, float partialTick) {
        super(context, track.getLevel(), partialTick);
        this.blockEntity = track;
        this.pos = this.blockEntity.getBlockPos();
        this.visualPos = this.pos.subtract(context.renderOrigin());
        this.collectConnections();
    }

    public void setSectionCollector(SectionTrackedVisual.SectionCollector sectionCollector) {
        this.lightSections = sectionCollector;
        this.lightSections.sections(this.collectLightSections());
    }

    public void update(float pt) {
        if (this.blockEntity.connections.isEmpty()) {
            return;
        }
        this._delete();
        this.collectConnections();
        this.lightSections.sections(this.collectLightSections());
    }

    private void collectConnections() {
        this.blockEntity.connections.values().stream().map(this::createInstance).filter(Objects::nonNull).forEach(this.visuals::add);
    }

    @Nullable
    private BezierTrackVisual createInstance(BezierConnection bc) {
        if (!bc.isPrimary()) {
            return null;
        }
        return new BezierTrackVisual(bc);
    }

    public void _delete() {
        this.visuals.forEach(BezierTrackVisual::delete);
        this.visuals.clear();
    }

    public LongSet collectLightSections() {
        if (this.blockEntity.connections.isEmpty()) {
            return LongSet.of();
        }
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (BezierConnection connection : this.blockEntity.connections.values()) {
            AABB bounds = connection.getBounds();
            minX = Math.min(minX, Mth.floor((double)bounds.minX) - 1);
            minY = Math.min(minY, Mth.floor((double)bounds.minY) - 1);
            minZ = Math.min(minZ, Mth.floor((double)bounds.minZ) - 1);
            maxX = Math.max(maxX, Mth.ceil((double)bounds.maxX) + 1);
            maxY = Math.max(maxY, Mth.ceil((double)bounds.maxY) + 1);
            maxZ = Math.max(maxZ, Mth.ceil((double)bounds.maxZ) + 1);
        }
        int minSectionX = SectionPos.blockToSectionCoord((int)minX);
        int minSectionY = SectionPos.blockToSectionCoord((int)minY);
        int minSectionZ = SectionPos.blockToSectionCoord((int)minZ);
        int maxSectionX = SectionPos.blockToSectionCoord((int)maxX);
        int maxSectionY = SectionPos.blockToSectionCoord((int)maxY);
        int maxSectionZ = SectionPos.blockToSectionCoord((int)maxZ);
        LongArraySet out = new LongArraySet();
        for (int x = minSectionX; x <= maxSectionX; ++x) {
            for (int y = minSectionY; y <= maxSectionY; ++y) {
                for (int z = minSectionZ; z <= maxSectionZ; ++z) {
                    out.add(SectionPos.asLong((int)x, (int)y, (int)z));
                }
            }
        }
        return out;
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        for (BezierTrackVisual instance : this.visuals) {
            instance.collectCrumblingInstances(consumer);
        }
    }

    private class BezierTrackVisual {
        private final TransformedInstance[] ties;
        private final TransformedInstance[] left;
        private final TransformedInstance[] right;
        @Nullable
        private GirderVisual girder;

        private BezierTrackVisual(BezierConnection bc) {
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
}
