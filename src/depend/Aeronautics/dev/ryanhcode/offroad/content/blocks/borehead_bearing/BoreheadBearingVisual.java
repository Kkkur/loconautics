/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllPartialModels
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual
 *  com.simibubi.create.content.kinetics.base.RotatingInstance
 *  com.simibubi.create.foundation.render.AllInstanceTypes
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.instance.Instancer
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.AbstractInstance
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.OrientedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.offroad.content.blocks.borehead_bearing;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlock;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import java.util.EnumMap;
import java.util.function.Consumer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class BoreheadBearingVisual
extends KineticBlockEntityVisual<BoreheadBearingBlockEntity>
implements SimpleDynamicVisual {
    private final EnumMap<Direction, RotatingInstance> instanceMap = new EnumMap(Direction.class);
    private final OrientedInstance topInstance;

    public BoreheadBearingVisual(VisualizationContext context, BoreheadBearingBlockEntity be, float partialTick) {
        super(context, (KineticBlockEntity)be, partialTick);
        Instancer halfShaftInstancer = this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.SHAFT_HALF));
        BlockState state = be.getBlockState();
        Direction.Axis axis = ((BoreheadBearingBlock)state.getBlock()).getRotationAxis(state);
        for (Direction dir : Iterate.directionsInAxis((Direction.Axis)axis)) {
            RotatingInstance rotInstance = (RotatingInstance)halfShaftInstancer.createInstance();
            rotInstance.setup((KineticBlockEntity)be, axis, be.getSpeed() * (float)dir.getAxisDirection().getStep()).setPosition((Vec3i)this.getVisualPosition()).rotateToFace(Direction.SOUTH, dir).setChanged();
            this.instanceMap.put(dir, rotInstance);
        }
        this.topInstance = (OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)AllPartialModels.BEARING_TOP)).createInstance();
        ((OrientedInstance)this.topInstance.position((Vec3i)this.getVisualPosition()).rotateTo(Direction.UP, (Direction)this.blockState.getValue((Property)BlockStateProperties.FACING))).setChanged();
    }

    public void update(float partialTick) {
        this.instanceMap.forEach((dir, instance) -> instance.setup((KineticBlockEntity)this.blockEntity, dir.getAxis(), ((BoreheadBearingBlockEntity)this.blockEntity).getSpeed() * (float)dir.getAxisDirection().getStep()).setChanged());
    }

    public void beginFrame(DynamicVisual.Context context) {
        ((OrientedInstance)((OrientedInstance)this.topInstance.identityRotation().rotateTo(Direction.UP, (Direction)this.blockState.getValue((Property)BlockStateProperties.FACING))).rotateDegrees((float)((Direction)this.blockState.getValue((Property)BlockStateProperties.FACING)).getAxisDirection().getStep() * ((BoreheadBearingBlockEntity)this.blockEntity).getInterpolatedAngle(context.partialTick() - 1.0f), Direction.Axis.Y)).setChanged();
    }

    public void updateLight(float v) {
        this.relight((FlatLit[])this.instanceMap.values().toArray(FlatLit[]::new));
        this.relight(new FlatLit[]{this.topInstance});
    }

    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        this.instanceMap.values().forEach(consumer);
        consumer.accept((Instance)this.topInstance);
    }

    protected void _delete() {
        this.instanceMap.values().forEach(AbstractInstance::delete);
        this.instanceMap.clear();
        this.topInstance.delete();
    }
}
