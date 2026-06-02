/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllPartialModels
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
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.small;

import com.simibubi.create.AllPartialModels;
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
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlockEntity;
import dev.simulated_team.simulated.util.SimMathUtils;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public abstract class SimplePropellerVisual<T extends BasePropellerBlockEntity>
extends OrientedRotatingVisual<T>
implements SimpleDynamicVisual {
    protected final OrientedInstance propeller;
    protected final Vector3f rotationAxis;
    protected final Quaternionf blockOrientation;
    private float lastRotation;

    public SimplePropellerVisual(VisualizationContext context, T blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Direction.SOUTH, ((Direction)blockEntity.getBlockState().getValue((Property)BlockStateProperties.FACING)).getOpposite(), Models.partial((PartialModel)AllPartialModels.SHAFT_HALF));
        Direction facing = (Direction)this.blockState.getValue((Property)BlockStateProperties.FACING);
        Vec3i normal = facing.getNormal();
        Vec3 normalPos = new Vec3((double)normal.getX(), (double)normal.getY(), (double)normal.getZ());
        Vector3f pos = Vec3.atLowerCornerOf((Vec3i)this.getVisualPosition()).add(normalPos.scale(0.1875)).toVector3f();
        this.rotationAxis = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)this.rotationAxis()).step();
        this.blockOrientation = SimMathUtils.getBlockStateOrientation((Direction)facing);
        this.propeller = (OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)this.getModel(blockEntity.getBlockState()))).createInstance();
        this.propeller.position((Vector3fc)pos).rotation((Quaternionfc)this.blockOrientation).setChanged();
    }

    public abstract PartialModel getModel(BlockState var1);

    public void beginFrame(DynamicVisual.Context context) {
        float angle = this.getAngle(context.partialTick());
        if (this.lastRotation == angle) {
            return;
        }
        this.lastRotation = angle;
        ((OrientedInstance)this.propeller.identityRotation().rotate((float)Math.PI / 180 * angle, this.rotationAxis.x, this.rotationAxis.y, this.rotationAxis.z)).rotate((Quaternionfc)this.blockOrientation).setChanged();
    }

    public float getAngle(float partialTicks) {
        return 2.0f * (((BasePropellerBlockEntity)this.blockEntity).getPreviousAngle() * (1.0f - partialTicks) + ((BasePropellerBlockEntity)this.blockEntity).getAngle() * partialTicks);
    }

    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight(this.pos, new FlatLit[]{this.propeller});
    }

    protected void _delete() {
        super._delete();
        this.propeller.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.propeller);
    }
}
