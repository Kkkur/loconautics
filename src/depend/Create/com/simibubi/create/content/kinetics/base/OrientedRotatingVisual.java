/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.model.Model
 *  dev.engine_room.flywheel.api.visual.BlockEntityVisual
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer$Factory
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlock;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlockEntity;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class OrientedRotatingVisual<T extends KineticBlockEntity>
extends KineticBlockEntityVisual<T> {
    protected final RotatingInstance rotatingModel;

    public OrientedRotatingVisual(VisualizationContext context, T blockEntity, float partialTick, Direction from, Direction to, Model model) {
        super(context, blockEntity, partialTick);
        this.rotatingModel = ((RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, model).createInstance()).rotateToFace(from, to).setup((KineticBlockEntity)blockEntity).setPosition((Vec3i)this.getVisualPosition());
        this.rotatingModel.setChanged();
    }

    public static <T extends KineticBlockEntity> SimpleBlockEntityVisualizer.Factory<T> of(PartialModel partial) {
        return (context, blockEntity, partialTick) -> {
            Direction facing = (Direction)blockEntity.getBlockState().getValue((Property)BlockStateProperties.FACING);
            return new OrientedRotatingVisual<KineticBlockEntity>(context, (KineticBlockEntity)blockEntity, partialTick, Direction.SOUTH, facing, Models.partial((PartialModel)partial));
        };
    }

    public static <T extends KineticBlockEntity> SimpleBlockEntityVisualizer.Factory<T> backHorizontal(PartialModel partial) {
        return (context, blockEntity, partialTick) -> {
            Direction facing = ((Direction)blockEntity.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_FACING)).getOpposite();
            return new OrientedRotatingVisual<KineticBlockEntity>(context, (KineticBlockEntity)blockEntity, partialTick, Direction.SOUTH, facing, Models.partial((PartialModel)partial));
        };
    }

    public static BlockEntityVisual<? super GantryShaftBlockEntity> gantryShaft(VisualizationContext visualizationContext, GantryShaftBlockEntity gantryShaftBlockEntity, float partialTick) {
        BlockState blockState = gantryShaftBlockEntity.getBlockState();
        GantryShaftBlock.Part part = (GantryShaftBlock.Part)((Object)blockState.getValue(GantryShaftBlock.PART));
        boolean isPowered = (Boolean)blockState.getValue((Property)GantryShaftBlock.POWERED);
        boolean isFlipped = ((Direction)blockState.getValue((Property)GantryShaftBlock.FACING)).getAxisDirection() == Direction.AxisDirection.NEGATIVE;
        Model model = Models.partial((PartialModel)AllPartialModels.GANTRY_SHAFTS.get(new AllPartialModels.GantryShaftKey(part, isPowered, isFlipped)));
        return new OrientedRotatingVisual<GantryShaftBlockEntity>(visualizationContext, gantryShaftBlockEntity, partialTick, Direction.UP, (Direction)blockState.getValue((Property)GantryShaftBlock.FACING), model);
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
