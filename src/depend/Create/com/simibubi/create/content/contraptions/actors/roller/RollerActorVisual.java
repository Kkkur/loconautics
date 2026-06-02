/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.actors.roller;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterActorVisual;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class RollerActorVisual
extends HarvesterActorVisual {
    TransformedInstance frame;

    public RollerActorVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        super(visualizationContext, simulationWorld, movementContext);
        this.frame = (TransformedInstance)this.instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.ROLLER_FRAME)).createInstance();
        this.frame.light(this.localBlockLight(), 0);
    }

    @Override
    public void beginFrame() {
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.harvester.setIdentityTransform().translate((Vec3i)this.context.localPos)).center()).rotateYDegrees(this.horizontalAngle)).uncenter()).translate(0.0, -0.25, 1.0625)).rotateXDegrees((float)this.getRotation())).translate(0.0, -0.5, 0.5)).rotateYDegrees(90.0f)).setChanged();
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.frame.setIdentityTransform().translate((Vec3i)this.context.localPos)).center()).rotateYDegrees(this.horizontalAngle + 180.0f)).uncenter()).setChanged();
    }

    @Override
    protected PartialModel getRollingPartial() {
        return AllPartialModels.ROLLER_WHEEL;
    }

    @Override
    protected Vec3 getRotationOffset() {
        return Vec3.ZERO;
    }

    @Override
    protected double getRadius() {
        return 16.5;
    }

    @Override
    protected void _delete() {
        super._delete();
        this.frame.delete();
    }
}
