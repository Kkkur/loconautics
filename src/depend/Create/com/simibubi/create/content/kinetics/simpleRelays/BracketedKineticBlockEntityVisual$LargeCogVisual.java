/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 */
package com.simibubi.create.content.kinetics.simpleRelays;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public static class BracketedKineticBlockEntityVisual.LargeCogVisual
extends SingleAxisRotatingVisual<BracketedKineticBlockEntity> {
    protected final RotatingInstance additionalShaft;

    private BracketedKineticBlockEntityVisual.LargeCogVisual(VisualizationContext context, BracketedKineticBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.partial((PartialModel)AllPartialModels.SHAFTLESS_LARGE_COGWHEEL));
        Direction.Axis axis = KineticBlockEntityRenderer.getRotationAxisOf(blockEntity);
        this.additionalShaft = (RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.COGWHEEL_SHAFT)).createInstance();
        this.additionalShaft.rotateToFace(axis).setup(blockEntity).setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, this.pos)).setPosition((Vec3i)this.getVisualPosition()).setChanged();
    }

    @Override
    public void update(float pt) {
        super.update(pt);
        this.additionalShaft.setup((KineticBlockEntity)this.blockEntity).setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(this.rotationAxis(), this.pos)).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight(new FlatLit[]{this.additionalShaft});
    }

    @Override
    protected void _delete() {
        super._delete();
        this.additionalShaft.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.additionalShaft);
    }
}
