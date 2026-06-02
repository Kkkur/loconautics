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
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package com.simibubi.create.content.kinetics.crank;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import com.simibubi.create.foundation.render.AllInstanceTypes;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class HandCrankVisual
extends KineticBlockEntityVisual<HandCrankBlockEntity>
implements SimpleDynamicVisual {
    private final RotatingInstance rotatingModel;
    private final TransformedInstance crank = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.HAND_CRANK_HANDLE)).createInstance();

    public HandCrankVisual(VisualizationContext modelManager, HandCrankBlockEntity blockEntity, float partialTick) {
        super(modelManager, blockEntity, partialTick);
        this.rotateCrank(partialTick);
        this.rotatingModel = (RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.HAND_CRANK_BASE)).createInstance();
        this.rotatingModel.setup((KineticBlockEntity)this.blockEntity).setPosition((Vec3i)this.getVisualPosition()).rotateToFace((Direction)this.blockState.getValue((Property)BlockStateProperties.FACING)).setChanged();
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.rotateCrank(ctx.partialTick());
    }

    private void rotateCrank(float pt) {
        Direction facing = (Direction)this.blockState.getValue((Property)BlockStateProperties.FACING);
        float angle = AngleHelper.rad((double)((HandCrankBlockEntity)this.blockEntity).getIndependentAngle(pt));
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.crank.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).center()).rotate(angle, Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)facing.getAxis()))).rotate((Quaternionfc)new Quaternionf().rotateTo(0.0f, 0.0f, -1.0f, (float)facing.getStepX(), (float)facing.getStepY(), (float)facing.getStepZ())).uncenter()).setChanged();
    }

    protected void _delete() {
        this.crank.delete();
        this.rotatingModel.delete();
    }

    public void update(float pt) {
        this.rotatingModel.setup((KineticBlockEntity)this.blockEntity).setChanged();
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.crank, this.rotatingModel});
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept((Instance)this.crank);
        consumer.accept((Instance)this.rotatingModel);
    }
}
