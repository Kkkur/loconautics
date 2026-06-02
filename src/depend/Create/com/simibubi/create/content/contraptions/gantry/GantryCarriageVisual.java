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
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.gantry;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.gantry.GantryCarriageBlock;
import com.simibubi.create.content.contraptions.gantry.GantryCarriageBlockEntity;
import com.simibubi.create.content.contraptions.gantry.GantryCarriageRenderer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
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
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.Property;

public class GantryCarriageVisual
extends ShaftVisual<GantryCarriageBlockEntity>
implements SimpleDynamicVisual {
    private final TransformedInstance gantryCogs;
    final Direction facing;
    final Boolean alongFirst;
    final Direction.Axis rotationAxis;
    final float rotationMult;
    final BlockPos visualPos;
    private float lastAngle = Float.NaN;

    public GantryCarriageVisual(VisualizationContext context, GantryCarriageBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        this.gantryCogs = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.GANTRY_COGS)).createInstance();
        this.facing = (Direction)this.blockState.getValue((Property)GantryCarriageBlock.FACING);
        this.alongFirst = (Boolean)this.blockState.getValue((Property)GantryCarriageBlock.AXIS_ALONG_FIRST_COORDINATE);
        this.rotationAxis = KineticBlockEntityRenderer.getRotationAxisOf(blockEntity);
        this.rotationMult = GantryCarriageVisual.getRotationMultiplier(this.getGantryAxis(), this.facing);
        this.visualPos = this.facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? blockEntity.getBlockPos() : blockEntity.getBlockPos().relative(this.facing.getOpposite());
        this.animateCogs(this.getCogAngle());
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        float cogAngle = this.getCogAngle();
        if (Mth.equal((float)cogAngle, (float)this.lastAngle)) {
            return;
        }
        this.animateCogs(cogAngle);
    }

    private float getCogAngle() {
        return GantryCarriageRenderer.getAngleForBE((KineticBlockEntity)this.blockEntity, this.visualPos, this.rotationAxis) * this.rotationMult;
    }

    private void animateCogs(float cogAngle) {
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.gantryCogs.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)this.facing))).rotateXDegrees(this.facing == Direction.UP ? 0.0f : (this.facing == Direction.DOWN ? 180.0f : 90.0f))).rotateYDegrees(this.alongFirst ^ this.facing.getAxis() == Direction.Axis.X ? 0.0f : 90.0f)).translate(0.0f, -0.5625f, 0.0f).rotateXDegrees(-cogAngle)).translate(0.0f, 0.5625f, 0.0f).uncenter()).setChanged();
    }

    static float getRotationMultiplier(Direction.Axis gantryAxis, Direction facing) {
        float multiplier = 1.0f;
        if (gantryAxis == Direction.Axis.X && facing == Direction.UP) {
            multiplier *= -1.0f;
        }
        if (gantryAxis == Direction.Axis.Y && (facing == Direction.NORTH || facing == Direction.EAST)) {
            multiplier *= -1.0f;
        }
        return multiplier;
    }

    private Direction.Axis getGantryAxis() {
        Direction.Axis gantryAxis = Direction.Axis.X;
        for (Direction.Axis axis : Iterate.axes) {
            if (axis == this.rotationAxis || axis == this.facing.getAxis()) continue;
            gantryAxis = axis;
        }
        return gantryAxis;
    }

    @Override
    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.gantryCogs, this.rotatingModel});
    }

    @Override
    protected void _delete() {
        super._delete();
        this.gantryCogs.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.gantryCogs);
    }
}
