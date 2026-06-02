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
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package com.simibubi.create.content.kinetics.deployer;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerRenderer;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class DeployerActorVisual
extends ActorVisual {
    Direction facing;
    boolean stationaryTimer;
    TransformedInstance pole;
    TransformedInstance hand;
    RotatingInstance shaft;
    Matrix4fc baseHandTransform;
    Matrix4fc basePoleTransform;

    public DeployerActorVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext context) {
        super(visualizationContext, (BlockAndTintGetter)simulationWorld, context);
        BlockState state = context.state;
        DeployerBlockEntity.Mode mode = (DeployerBlockEntity.Mode)NBTHelper.readEnum((CompoundTag)context.blockEntityData, (String)"Mode", DeployerBlockEntity.Mode.class);
        PartialModel handPose = DeployerRenderer.getHandPose(mode);
        this.stationaryTimer = context.data.contains("StationaryTimer");
        this.facing = (Direction)state.getValue((Property)DirectionalKineticBlock.FACING);
        boolean rotatePole = (Boolean)state.getValue((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE) ^ this.facing.getAxis() == Direction.Axis.Z;
        float yRot = AngleHelper.horizontalAngle((Direction)this.facing);
        float xRot = this.facing == Direction.UP ? 270.0f : (this.facing == Direction.DOWN ? 90.0f : 0.0f);
        float zRot = rotatePole ? 90.0f : 0.0f;
        this.pole = (TransformedInstance)this.instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.DEPLOYER_POLE)).createInstance();
        this.hand = (TransformedInstance)this.instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)handPose)).createInstance();
        Direction.Axis axis = KineticBlockEntityVisual.rotationAxis(state);
        this.shaft = ((RotatingInstance)this.instancerProvider.instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.SHAFT)).createInstance()).rotateToFace(axis);
        int blockLight = this.localBlockLight();
        this.shaft.setRotationAxis(axis).setRotationOffset(KineticBlockEntityVisual.rotationOffset(state, axis, (Vec3i)context.localPos)).setPosition((Vec3i)context.localPos).light(blockLight, 0).setChanged();
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.pole.translate((Vec3i)context.localPos)).center()).rotate(yRot * ((float)Math.PI / 180), Direction.UP)).rotate(xRot * ((float)Math.PI / 180), Direction.EAST)).rotate(zRot * ((float)Math.PI / 180), Direction.SOUTH)).uncenter()).light(blockLight, 0).setChanged();
        this.basePoleTransform = new Matrix4f((Matrix4fc)this.pole.pose);
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.hand.translate((Vec3i)context.localPos)).center()).rotate(yRot * ((float)Math.PI / 180), Direction.UP)).rotate(xRot * ((float)Math.PI / 180), Direction.EAST)).uncenter()).light(blockLight, 0).setChanged();
        this.baseHandTransform = new Matrix4f((Matrix4fc)this.hand.pose);
    }

    @Override
    public void beginFrame() {
        float distance = this.deploymentDistance();
        ((TransformedInstance)this.pole.setTransform(this.basePoleTransform).translateZ(distance)).setChanged();
        ((TransformedInstance)this.hand.setTransform(this.baseHandTransform).translateZ(distance)).setChanged();
    }

    private float deploymentDistance() {
        double factor;
        if (this.context.disabled) {
            factor = 0.0;
        } else if (this.context.contraption.stalled || this.context.position == null || this.context.data.contains("StationaryTimer")) {
            factor = Mth.sin((float)(AnimationTickHolder.getRenderTime() * 0.5f)) * 0.25f + 0.25f;
        } else {
            Vec3 center = VecHelper.getCenterOf((Vec3i)BlockPos.containing((Position)this.context.position));
            double distance = this.context.position.distanceTo(center);
            double nextDistance = this.context.position.add(this.context.motion).distanceTo(center);
            factor = 0.5 - Mth.clamp((double)Mth.lerp((double)AnimationTickHolder.getPartialTicks(), (double)distance, (double)nextDistance), (double)0.0, (double)1.0);
        }
        return (float)factor;
    }

    @Override
    protected void _delete() {
        this.pole.delete();
        this.hand.delete();
        this.shaft.delete();
    }
}
