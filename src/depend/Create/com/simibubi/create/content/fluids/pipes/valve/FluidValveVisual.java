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
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.fluids.pipes.valve;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlock;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
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
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.Property;

public class FluidValveVisual
extends ShaftVisual<FluidValveBlockEntity>
implements SimpleDynamicVisual {
    protected TransformedInstance pointer;
    protected boolean settled;
    protected final double xRot;
    protected final double yRot;
    protected final int pointerRotationOffset;

    public FluidValveVisual(VisualizationContext dispatcher, FluidValveBlockEntity blockEntity, float partialTick) {
        super(dispatcher, blockEntity, partialTick);
        Direction facing = (Direction)this.blockState.getValue((Property)FluidValveBlock.FACING);
        this.yRot = AngleHelper.horizontalAngle((Direction)facing);
        this.xRot = facing == Direction.UP ? 0.0 : (facing == Direction.DOWN ? 180.0 : 90.0);
        Direction.Axis pipeAxis = FluidValveBlock.getPipeAxis(this.blockState);
        Direction.Axis shaftAxis = KineticBlockEntityRenderer.getRotationAxisOf(blockEntity);
        boolean twist = pipeAxis.isHorizontal() && shaftAxis == Direction.Axis.X || pipeAxis.isVertical();
        this.pointerRotationOffset = twist ? 90 : 0;
        this.settled = false;
        this.pointer = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.FLUID_VALVE_POINTER)).createInstance();
        this.transformPointer(partialTick);
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        if (((FluidValveBlockEntity)this.blockEntity).pointer.settled() && this.settled) {
            return;
        }
        this.transformPointer(ctx.partialTick());
    }

    private void transformPointer(float partialTick) {
        float value = ((FluidValveBlockEntity)this.blockEntity).pointer.getValue(partialTick);
        float pointerRotation = Mth.lerp((float)value, (float)0.0f, (float)-90.0f);
        this.settled = (value == 0.0f || value == 1.0f) && ((FluidValveBlockEntity)this.blockEntity).pointer.settled();
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.pointer.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).center()).rotateYDegrees((float)this.yRot)).rotateXDegrees((float)this.xRot)).rotateYDegrees((float)this.pointerRotationOffset + pointerRotation)).uncenter()).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight(new FlatLit[]{this.pointer});
    }

    @Override
    protected void _delete() {
        super._delete();
        this.pointer.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.pointer);
    }
}
