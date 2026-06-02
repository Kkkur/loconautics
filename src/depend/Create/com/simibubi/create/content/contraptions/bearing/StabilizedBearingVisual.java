/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.OrientedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package com.simibubi.create.content.contraptions.bearing;

import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.bearing.BearingVisual;
import com.simibubi.create.content.contraptions.bearing.StabilizedBearingMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class StabilizedBearingVisual
extends ActorVisual {
    final OrientedInstance topInstance;
    final RotatingInstance shaft;
    final Direction facing;
    final Axis rotationAxis;
    final Quaternionf blockOrientation;

    public StabilizedBearingVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        super(visualizationContext, (BlockAndTintGetter)simulationWorld, movementContext);
        BlockState blockState = movementContext.state;
        this.facing = (Direction)blockState.getValue((Property)BlockStateProperties.FACING);
        this.rotationAxis = Axis.of((Vector3f)Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)this.facing.getAxis()).step());
        this.blockOrientation = BearingVisual.getBlockStateOrientation(this.facing);
        this.topInstance = (OrientedInstance)this.instancerProvider.instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)AllPartialModels.BEARING_TOP)).createInstance();
        int blockLight = this.localBlockLight();
        this.topInstance.position((Vec3i)movementContext.localPos).rotation((Quaternionfc)this.blockOrientation).light(blockLight, 0).setChanged();
        this.shaft = (RotatingInstance)this.instancerProvider.instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.SHAFT_HALF)).createInstance();
        Direction.Axis axis = KineticBlockEntityVisual.rotationAxis(blockState);
        this.shaft.setRotationAxis(axis).setRotationOffset(KineticBlockEntityVisual.rotationOffset(blockState, axis, (Vec3i)movementContext.localPos)).setPosition((Vec3i)movementContext.localPos).rotateToFace(Direction.SOUTH, ((Direction)blockState.getValue((Property)BlockStateProperties.FACING)).getOpposite()).light(blockLight, 0).setChanged();
    }

    @Override
    public void beginFrame() {
        float counterRotationAngle = StabilizedBearingMovementBehaviour.getCounterRotationAngle(this.context, this.facing, AnimationTickHolder.getPartialTicks());
        Quaternionf rotation = this.rotationAxis.rotationDegrees(counterRotationAngle);
        rotation.mul((Quaternionfc)this.blockOrientation);
        this.topInstance.rotation((Quaternionfc)rotation).setChanged();
    }

    @Override
    protected void _delete() {
        this.topInstance.delete();
        this.shaft.delete();
    }
}
