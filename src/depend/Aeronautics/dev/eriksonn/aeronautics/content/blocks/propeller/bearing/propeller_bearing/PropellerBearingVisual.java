/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  com.simibubi.create.AllPartialModels
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.OrientedRotatingVisual
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.OrientedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  dev.simulated_team.simulated.util.SimMathUtils
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
package dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing;

import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
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
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.PropellerBearingBlockEntity;
import dev.eriksonn.aeronautics.index.AeroPartialModels;
import dev.simulated_team.simulated.util.SimMathUtils;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class PropellerBearingVisual
extends OrientedRotatingVisual<PropellerBearingBlockEntity>
implements SimpleDynamicVisual {
    private final OrientedInstance topInstance;
    private final Axis rotationAxis;
    private final Quaternionf blockOrientation;

    public PropellerBearingVisual(VisualizationContext context, PropellerBearingBlockEntity blockEntity, float partialTick) {
        super(context, (KineticBlockEntity)blockEntity, partialTick, Direction.SOUTH, ((Direction)blockEntity.getBlockState().getValue((Property)BlockStateProperties.FACING)).getOpposite(), Models.partial((PartialModel)AllPartialModels.SHAFT_HALF));
        Direction facing = (Direction)this.blockState.getValue((Property)BlockStateProperties.FACING);
        this.rotationAxis = Axis.of((Vector3f)Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)this.rotationAxis()).step());
        this.blockOrientation = SimMathUtils.getBlockStateOrientation((Direction)facing);
        PartialModel top = AeroPartialModels.BEARING_PLATE;
        this.topInstance = (OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)top)).createInstance();
        this.topInstance.position((Vec3i)this.getVisualPosition()).rotation((Quaternionfc)this.blockOrientation).setChanged();
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        float interpolatedAngle = ((PropellerBearingBlockEntity)this.blockEntity).getInterpolatedAngle(ctx.partialTick() - 1.0f);
        Quaternionf rot = this.rotationAxis.rotationDegrees(interpolatedAngle);
        rot.mul((Quaternionfc)this.blockOrientation);
        this.topInstance.rotation((Quaternionfc)rot).setChanged();
    }

    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight(new FlatLit[]{this.topInstance});
    }

    protected void _delete() {
        super._delete();
        this.topInstance.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.topInstance);
    }
}
