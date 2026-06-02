/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  com.simibubi.create.AllPartialModels
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.OrientedRotatingVisual
 *  com.simibubi.create.content.kinetics.base.RotatingInstance
 *  com.simibubi.create.foundation.render.AllInstanceTypes
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.OrientedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
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
package dev.simulated_team.simulated.content.blocks.torsion_spring;

import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
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
import dev.simulated_team.simulated.content.blocks.torsion_spring.TorsionSpringBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.util.SimMathUtils;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class TorsionSpringVisual
extends OrientedRotatingVisual<TorsionSpringBlockEntity>
implements SimpleDynamicVisual {
    private final Axis rotationAxis;
    private final Quaternionf blockOrientation;
    private final RotatingInstance topInstance;
    private final OrientedInstance springInstance;
    private boolean wasSpringStatic;

    public TorsionSpringVisual(VisualizationContext context, TorsionSpringBlockEntity blockEntity, float partialTick) {
        super(context, (KineticBlockEntity)blockEntity, partialTick, Direction.SOUTH, ((Direction)blockEntity.getBlockState().getValue((Property)BlockStateProperties.FACING)).getOpposite(), Models.partial((PartialModel)AllPartialModels.SHAFT_HALF));
        Direction facing = (Direction)this.blockState.getValue((Property)BlockStateProperties.FACING);
        this.rotationAxis = Axis.of((Vector3f)Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)this.rotationAxis()).step());
        this.blockOrientation = SimMathUtils.getBlockStateOrientation(facing);
        this.topInstance = ((RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.SHAFT_HALF)).createInstance()).rotateToFace(Direction.SOUTH, (Direction)blockEntity.getBlockState().getValue((Property)BlockStateProperties.FACING)).setup(blockEntity.getExtraKinetics()).setPosition((Vec3i)this.getVisualPosition());
        this.springInstance = ((OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)SimPartialModels.TORSION_SPRING)).createInstance()).rotation((Quaternionfc)this.blockOrientation).position((Vec3i)this.getVisualPosition());
        this.topInstance.setChanged();
        this.springInstance.setChanged();
        this.wasSpringStatic = false;
    }

    public void update(float pt) {
        super.update(pt);
        this.topInstance.setup(((TorsionSpringBlockEntity)this.blockEntity).getExtraKinetics()).setChanged();
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        if (this.wasSpringStatic && ((TorsionSpringBlockEntity)this.blockEntity).isSpringStatic()) {
            return;
        }
        this.wasSpringStatic = ((TorsionSpringBlockEntity)this.blockEntity).isSpringStatic();
        float interpolatedAngle = ((TorsionSpringBlockEntity)this.blockEntity).interpolatedSpring(ctx.partialTick());
        Quaternionf rot = this.rotationAxis.rotationDegrees(interpolatedAngle);
        rot.mul((Quaternionfc)this.blockOrientation);
        this.springInstance.rotation((Quaternionfc)rot).setChanged();
    }

    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight(new FlatLit[]{this.topInstance});
        this.relight(new FlatLit[]{this.springInstance});
    }

    protected void _delete() {
        super._delete();
        this.topInstance.delete();
        this.springInstance.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.topInstance);
        consumer.accept((Instance)this.springInstance);
    }
}
