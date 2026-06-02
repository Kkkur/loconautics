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
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package com.simibubi.create.content.kinetics.crank;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class ValveHandleVisual
extends KineticBlockEntityVisual<HandCrankBlockEntity>
implements SimpleDynamicVisual {
    private final TransformedInstance crank;

    public ValveHandleVisual(VisualizationContext modelManager, HandCrankBlockEntity blockEntity, float partialTick) {
        super(modelManager, blockEntity, partialTick);
        Block block;
        BlockState state = blockEntity.getBlockState();
        DyeColor color = null;
        if (state != null && (block = state.getBlock()) instanceof ValveHandleBlock) {
            ValveHandleBlock vhb = (ValveHandleBlock)block;
            color = vhb.color;
        }
        this.crank = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)(color == null ? AllPartialModels.VALVE_HANDLE : AllPartialModels.DYED_VALVE_HANDLES.get(color)))).createInstance();
        this.rotateCrank(partialTick);
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.rotateCrank(ctx.partialTick());
    }

    private void rotateCrank(float pt) {
        Direction facing = (Direction)this.blockState.getValue((Property)BlockStateProperties.FACING);
        float angle = AngleHelper.rad((double)((HandCrankBlockEntity)this.blockEntity).getIndependentAngle(pt));
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.crank.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).center()).rotate(angle, Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)facing.getAxis()))).rotate((Quaternionfc)new Quaternionf().rotateTo(0.0f, 1.0f, 0.0f, (float)facing.getStepX(), (float)facing.getStepY(), (float)facing.getStepZ())).uncenter()).setChanged();
    }

    protected void _delete() {
        this.crank.delete();
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.crank});
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept((Instance)this.crank);
    }
}
