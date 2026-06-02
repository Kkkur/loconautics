/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.ShaftVisual
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
package dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon;

import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon.MountedPotatoCannonBlockEntity;
import dev.eriksonn.aeronautics.index.AeroPartialModels;
import java.util.function.Consumer;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class MountedPotatoCannonVisual
extends ShaftVisual<MountedPotatoCannonBlockEntity>
implements SimpleDynamicVisual {
    private final OrientedInstance cogInstance;
    final Axis rotationAxis;
    final Quaternionf blockOrientation;

    public MountedPotatoCannonVisual(VisualizationContext context, MountedPotatoCannonBlockEntity blockEntity, float partialTick) {
        super(context, (KineticBlockEntity)blockEntity, partialTick);
        Direction facing = (Direction)this.blockState.getValue((Property)BlockStateProperties.FACING);
        this.rotationAxis = Axis.of((Vector3f)Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)this.rotationAxis()).step());
        this.blockOrientation = MountedPotatoCannonVisual.getBlockStateOrientation(facing);
        this.cogInstance = (OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)AeroPartialModels.CANNON_COG)).createInstance();
        this.cogInstance.position((Vec3i)this.getVisualPosition()).rotation((Quaternionfc)this.blockOrientation).setChanged();
    }

    public void update(float pt) {
        super.update(pt);
    }

    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight(new FlatLit[]{this.cogInstance});
    }

    protected void _delete() {
        super._delete();
        this.cogInstance.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.cogInstance);
    }

    static Quaternionf getBlockStateOrientation(Direction facing) {
        Quaternionf orientation = facing.getAxis().isHorizontal() ? Axis.YP.rotationDegrees(AngleHelper.horizontalAngle((Direction)facing)) : new Quaternionf();
        orientation.mul((Quaternionfc)Axis.XP.rotationDegrees(AngleHelper.verticalAngle((Direction)facing)));
        return orientation;
    }

    public void beginFrame(DynamicVisual.Context context) {
        float angle = ((MountedPotatoCannonBlockEntity)this.blockEntity).getCogwheelAngle(context.partialTick());
        Quaternionf rot = Axis.ZP.rotationDegrees(angle);
        rot.premul((Quaternionfc)this.blockOrientation);
        this.cogInstance.rotation((Quaternionfc)rot).setChanged();
    }
}
