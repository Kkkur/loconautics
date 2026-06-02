/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.OrientedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package com.simibubi.create.content.contraptions.bearing;

import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
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
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class BearingVisual<B extends KineticBlockEntity>
extends OrientedRotatingVisual<B>
implements SimpleDynamicVisual {
    final OrientedInstance topInstance;
    final Axis rotationAxis;
    final Quaternionf blockOrientation;

    public BearingVisual(VisualizationContext context, B blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Direction.SOUTH, ((Direction)blockEntity.getBlockState().getValue((Property)BlockStateProperties.FACING)).getOpposite(), Models.partial((PartialModel)AllPartialModels.SHAFT_HALF));
        Direction facing = (Direction)this.blockState.getValue((Property)BlockStateProperties.FACING);
        this.rotationAxis = Axis.of((Vector3f)Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)this.rotationAxis()).step());
        this.blockOrientation = BearingVisual.getBlockStateOrientation(facing);
        PartialModel top = ((IBearingBlockEntity)blockEntity).isWoodenTop() ? AllPartialModels.BEARING_TOP_WOODEN : AllPartialModels.BEARING_TOP;
        this.topInstance = (OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)top)).createInstance();
        this.topInstance.position((Vec3i)this.getVisualPosition()).rotation((Quaternionfc)this.blockOrientation).setChanged();
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        float interpolatedAngle = ((IBearingBlockEntity)((Object)((KineticBlockEntity)this.blockEntity))).getInterpolatedAngle(ctx.partialTick() - 1.0f);
        Quaternionf rot = this.rotationAxis.rotationDegrees(interpolatedAngle);
        rot.mul((Quaternionfc)this.blockOrientation);
        this.topInstance.rotation((Quaternionfc)rot).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight(new FlatLit[]{this.topInstance});
    }

    @Override
    protected void _delete() {
        super._delete();
        this.topInstance.delete();
    }

    static Quaternionf getBlockStateOrientation(Direction facing) {
        Quaternionf orientation = facing.getAxis().isHorizontal() ? Axis.YP.rotationDegrees(AngleHelper.horizontalAngle((Direction)facing.getOpposite())) : new Quaternionf();
        orientation.mul((Quaternionfc)Axis.XP.rotationDegrees(-90.0f - AngleHelper.verticalAngle((Direction)facing)));
        return orientation;
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.topInstance);
    }
}
