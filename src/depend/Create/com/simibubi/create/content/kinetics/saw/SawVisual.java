/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.instance.InstancerProvider
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.saw;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class SawVisual
extends KineticBlockEntityVisual<SawBlockEntity> {
    protected final RotatingInstance rotatingModel;

    public SawVisual(VisualizationContext context, SawBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        this.rotatingModel = SawVisual.shaft(this.instancerProvider(), this.blockState).setup(blockEntity).setPosition((Vec3i)this.getVisualPosition());
        this.rotatingModel.setChanged();
    }

    public static RotatingInstance shaft(InstancerProvider instancerProvider, BlockState state) {
        Direction facing = (Direction)state.getValue((Property)BlockStateProperties.FACING);
        Direction.Axis axis = facing.getAxis();
        if (axis.isHorizontal()) {
            Direction align = facing.getOpposite();
            return ((RotatingInstance)instancerProvider.instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.SHAFT_HALF)).createInstance()).rotateTo(0.0f, 0.0f, 1.0f, align.getStepX(), align.getStepY(), align.getStepZ());
        }
        return ((RotatingInstance)instancerProvider.instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.SHAFT)).createInstance()).rotateToFace((Boolean)state.getValue((Property)SawBlock.AXIS_ALONG_FIRST_COORDINATE) != false ? Direction.Axis.X : Direction.Axis.Z);
    }

    public void update(float pt) {
        this.rotatingModel.setup((KineticBlockEntity)this.blockEntity).setChanged();
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.rotatingModel});
    }

    protected void _delete() {
        this.rotatingModel.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept((Instance)this.rotatingModel);
    }
}
