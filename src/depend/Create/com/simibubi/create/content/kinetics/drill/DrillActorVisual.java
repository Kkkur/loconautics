/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.drill;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.kinetics.drill.DrillBlock;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class DrillActorVisual
extends ActorVisual {
    TransformedInstance drillHead;
    private final Direction facing;
    private double rotation;
    private double previousRotation;

    public DrillActorVisual(VisualizationContext visualizationContext, VirtualRenderWorld contraption, MovementContext context) {
        super(visualizationContext, (BlockAndTintGetter)contraption, context);
        BlockState state = context.state;
        this.facing = (Direction)state.getValue((Property)DrillBlock.FACING);
        this.drillHead = (TransformedInstance)this.instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.DRILL_HEAD)).createInstance();
    }

    @Override
    public void tick() {
        this.previousRotation = this.rotation;
        if (this.context.disabled || VecHelper.isVecPointingTowards((Vec3)this.context.relativeMotion, (Direction)this.facing.getOpposite())) {
            return;
        }
        float deg = this.context.getAnimationSpeed();
        this.rotation += (double)(deg / 20.0f);
        this.rotation %= 360.0;
    }

    @Override
    public void beginFrame() {
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.drillHead.setIdentityTransform().translate((Vec3i)this.context.localPos)).center()).rotateToFace(this.facing.getOpposite())).rotateZDegrees((float)this.getRotation())).uncenter()).setChanged();
    }

    protected double getRotation() {
        return AngleHelper.angleLerp((double)AnimationTickHolder.getPartialTicks(), (double)this.previousRotation, (double)this.rotation);
    }

    @Override
    protected void _delete() {
        this.drillHead.delete();
    }
}
