/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package com.simibubi.create.content.kinetics.flywheel;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import java.util.function.Consumer;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class FlywheelVisual
extends KineticBlockEntityVisual<FlywheelBlockEntity>
implements SimpleDynamicVisual {
    protected final RotatingInstance shaft;
    protected final TransformedInstance wheel;
    protected float lastAngle = Float.NaN;
    protected final Matrix4f baseTransform = new Matrix4f();

    public FlywheelVisual(VisualizationContext context, FlywheelBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        Direction.Axis axis = this.rotationAxis();
        this.shaft = (RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.SHAFT)).createInstance();
        this.shaft.setup((KineticBlockEntity)this.blockEntity).setPosition((Vec3i)this.getVisualPosition()).rotateToFace(axis).setChanged();
        this.wheel = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.FLYWHEEL)).createInstance();
        Direction align = Direction.fromAxisAndDirection((Direction.Axis)axis, (Direction.AxisDirection)Direction.AxisDirection.POSITIVE);
        ((TransformedInstance)((TransformedInstance)this.wheel.translate((Vec3i)this.getVisualPosition())).center()).rotate((Quaternionfc)new Quaternionf().rotateTo(0.0f, 1.0f, 0.0f, (float)align.getStepX(), (float)align.getStepY(), (float)align.getStepZ()));
        this.baseTransform.set((Matrix4fc)this.wheel.pose);
        this.animate(blockEntity.angle);
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        float partialTicks = ctx.partialTick();
        float speed = ((FlywheelBlockEntity)this.blockEntity).visualSpeed.getValue(partialTicks) * 3.0f / 10.0f;
        float angle = ((FlywheelBlockEntity)this.blockEntity).angle + speed * partialTicks;
        if ((double)Math.abs(angle - this.lastAngle) < 0.001) {
            return;
        }
        this.animate(angle);
        this.lastAngle = angle;
    }

    private void animate(float angle) {
        ((TransformedInstance)this.wheel.setTransform((Matrix4fc)this.baseTransform).rotateY(AngleHelper.rad((double)angle)).uncenter()).setChanged();
    }

    public void update(float pt) {
        this.shaft.setup((KineticBlockEntity)this.blockEntity).setChanged();
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.shaft, this.wheel});
    }

    protected void _delete() {
        this.shaft.delete();
        this.wheel.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept((Instance)this.shaft);
        consumer.accept((Instance)this.wheel);
    }
}
