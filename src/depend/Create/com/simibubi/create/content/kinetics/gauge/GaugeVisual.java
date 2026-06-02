/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.instance.Instancer
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 */
package com.simibubi.create.content.kinetics.gauge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.kinetics.gauge.GaugeBlock;
import com.simibubi.create.content.kinetics.gauge.GaugeBlockEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;

public abstract class GaugeVisual
extends ShaftVisual<GaugeBlockEntity>
implements SimpleDynamicVisual {
    protected final ArrayList<DialFace> faces = new ArrayList(2);
    protected final PoseStack ms = new PoseStack();

    protected GaugeVisual(VisualizationContext context, GaugeBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        GaugeBlock gaugeBlock = (GaugeBlock)this.blockState.getBlock();
        Instancer dialModel = this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.GAUGE_DIAL));
        Instancer<TransformedInstance> headModel = this.getHeadModel();
        PoseTransformStack msr = TransformStack.of((PoseStack)this.ms);
        msr.translate((Vec3i)this.getVisualPosition());
        float progress = Mth.lerp((float)AnimationTickHolder.getPartialTicks(), (float)blockEntity.prevDialState, (float)blockEntity.dialState);
        for (Direction facing : Iterate.directions) {
            if (!gaugeBlock.shouldRenderHeadOnFace(this.level, this.pos, this.blockState, facing)) continue;
            DialFace face = this.makeFace(facing, (Instancer<TransformedInstance>)dialModel, headModel);
            this.faces.add(face);
            face.setupTransform((TransformStack<?>)msr, progress);
        }
    }

    private DialFace makeFace(Direction face, Instancer<TransformedInstance> dialModel, Instancer<TransformedInstance> headModel) {
        return new DialFace(face, (TransformedInstance)dialModel.createInstance(), (TransformedInstance)headModel.createInstance());
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        if (Mth.equal((float)((GaugeBlockEntity)this.blockEntity).prevDialState, (float)((GaugeBlockEntity)this.blockEntity).dialState)) {
            return;
        }
        float progress = Mth.lerp((float)ctx.partialTick(), (float)((GaugeBlockEntity)this.blockEntity).prevDialState, (float)((GaugeBlockEntity)this.blockEntity).dialState);
        PoseTransformStack msr = TransformStack.of((PoseStack)this.ms);
        for (DialFace faceEntry : this.faces) {
            faceEntry.updateTransform((TransformStack<?>)msr, progress);
        }
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight((FlatLit[])this.faces.stream().flatMap(Couple::stream).toArray(FlatLit[]::new));
    }

    @Override
    protected void _delete() {
        super._delete();
        this.faces.forEach(DialFace::delete);
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        for (DialFace face : this.faces) {
            face.forEach(consumer);
        }
    }

    protected abstract Instancer<TransformedInstance> getHeadModel();

    protected class DialFace
    extends Couple<TransformedInstance> {
        Direction face;

        public DialFace(Direction face, TransformedInstance first, TransformedInstance second) {
            super((Object)first, (Object)second);
            this.face = face;
        }

        private void setupTransform(TransformStack<?> msr, float progress) {
            float dialPivot = 0.359375f;
            msr.pushPose();
            this.rotateToFace(msr);
            ((TransformedInstance)this.getSecond()).setTransform(GaugeVisual.this.ms).setChanged();
            ((TransformStack)((TransformStack)msr.translate(0.0f, dialPivot, dialPivot)).rotate((float)(1.5707963267948966 * (double)(-progress)), Direction.EAST)).translate(0.0f, -dialPivot, -dialPivot);
            ((TransformedInstance)this.getFirst()).setTransform(GaugeVisual.this.ms).setChanged();
            msr.popPose();
        }

        private void updateTransform(TransformStack<?> msr, float progress) {
            float dialPivot = 0.359375f;
            msr.pushPose();
            ((TransformStack)((TransformStack)this.rotateToFace(msr).translate(0.0f, dialPivot, dialPivot)).rotate((float)(1.5707963267948966 * (double)(-progress)), Direction.EAST)).translate(0.0f, -dialPivot, -dialPivot);
            ((TransformedInstance)this.getFirst()).setTransform(GaugeVisual.this.ms).setChanged();
            msr.popPose();
        }

        protected TransformStack<?> rotateToFace(TransformStack<?> msr) {
            return (TransformStack)((TransformStack)((TransformStack)msr.center()).rotate((float)((double)((-this.face.toYRot() - 90.0f) / 180.0f) * Math.PI), Direction.UP)).uncenter();
        }

        private void delete() {
            ((TransformedInstance)this.getFirst()).delete();
            ((TransformedInstance)this.getSecond()).delete();
        }
    }

    public static class Stress
    extends GaugeVisual {
        public Stress(VisualizationContext context, GaugeBlockEntity blockEntity, float partialTick) {
            super(context, blockEntity, partialTick);
        }

        @Override
        protected Instancer<TransformedInstance> getHeadModel() {
            return this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.GAUGE_HEAD_STRESS));
        }
    }

    public static class Speed
    extends GaugeVisual {
        public Speed(VisualizationContext context, GaugeBlockEntity blockEntity, float partialTick) {
            super(context, blockEntity, partialTick);
        }

        @Override
        protected Instancer<TransformedInstance> getHeadModel() {
            return this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.GAUGE_HEAD_SPEED));
        }
    }
}
