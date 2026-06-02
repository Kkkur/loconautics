/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.OrientedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 */
package com.simibubi.create.content.kinetics.mixer;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public class MixerVisual
extends SingleAxisRotatingVisual<MechanicalMixerBlockEntity>
implements SimpleDynamicVisual {
    private final RotatingInstance mixerHead;
    private final OrientedInstance mixerPole;
    private final MechanicalMixerBlockEntity mixer;

    public MixerVisual(VisualizationContext context, MechanicalMixerBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.partial((PartialModel)AllPartialModels.SHAFTLESS_COGWHEEL));
        this.mixer = blockEntity;
        this.mixerHead = (RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.MECHANICAL_MIXER_HEAD)).createInstance();
        this.mixerHead.setRotationAxis(Direction.Axis.Y);
        this.mixerPole = (OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)AllPartialModels.MECHANICAL_MIXER_POLE)).createInstance();
        this.animate(partialTick);
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.animate(ctx.partialTick());
    }

    private void animate(float pt) {
        float renderedHeadOffset = this.mixer.getRenderedHeadOffset(pt);
        this.transformPole(renderedHeadOffset);
        this.transformHead(renderedHeadOffset, pt);
    }

    private void transformHead(float renderedHeadOffset, float pt) {
        float speed = this.mixer.getRenderedHeadRotationSpeed(pt);
        this.mixerHead.setPosition((Vec3i)this.getVisualPosition()).nudge(0.0f, -renderedHeadOffset, 0.0f).setRotationalSpeed(speed * 2.0f * 6.0f).setChanged();
    }

    private void transformPole(float renderedHeadOffset) {
        this.mixerPole.position((Vec3i)this.getVisualPosition()).translatePosition(0.0f, -renderedHeadOffset, 0.0f).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight(this.pos.below(), new FlatLit[]{this.mixerHead});
        this.relight(new FlatLit[]{this.mixerPole});
    }

    @Override
    protected void _delete() {
        super._delete();
        this.mixerHead.delete();
        this.mixerPole.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.mixerHead);
        consumer.accept((Instance)this.mixerPole);
    }
}
