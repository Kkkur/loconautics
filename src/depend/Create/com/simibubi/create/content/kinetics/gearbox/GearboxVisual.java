/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.instance.Instancer
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.AbstractInstance
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.gearbox;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class GearboxVisual
extends KineticBlockEntityVisual<GearboxBlockEntity> {
    protected final EnumMap<Direction, RotatingInstance> keys = new EnumMap(Direction.class);
    protected Direction sourceFacing;

    public GearboxVisual(VisualizationContext context, GearboxBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        Direction.Axis boxAxis = (Direction.Axis)this.blockState.getValue((Property)BlockStateProperties.AXIS);
        this.updateSourceFacing();
        Instancer instancer = this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.SHAFT_HALF));
        for (Direction direction : Iterate.directions) {
            Direction.Axis axis = direction.getAxis();
            if (boxAxis == axis) continue;
            RotatingInstance instance = (RotatingInstance)instancer.createInstance();
            instance.setup(blockEntity, axis, this.getSpeed(direction)).setPosition((Vec3i)this.getVisualPosition()).rotateToFace(Direction.SOUTH, direction).setChanged();
            this.keys.put(direction, instance);
        }
    }

    private float getSpeed(Direction direction) {
        float speed = ((GearboxBlockEntity)this.blockEntity).getSpeed();
        if (speed != 0.0f && this.sourceFacing != null) {
            if (this.sourceFacing.getAxis() == direction.getAxis()) {
                speed *= this.sourceFacing == direction ? 1.0f : -1.0f;
            } else if (this.sourceFacing.getAxisDirection() == direction.getAxisDirection()) {
                speed *= -1.0f;
            }
        }
        return speed;
    }

    protected void updateSourceFacing() {
        if (((GearboxBlockEntity)this.blockEntity).hasSource()) {
            BlockPos source = ((GearboxBlockEntity)this.blockEntity).source.subtract((Vec3i)this.pos);
            this.sourceFacing = Direction.getNearest((float)source.getX(), (float)source.getY(), (float)source.getZ());
        } else {
            this.sourceFacing = null;
        }
    }

    public void update(float pt) {
        this.updateSourceFacing();
        for (Map.Entry<Direction, RotatingInstance> key : this.keys.entrySet()) {
            Direction direction = key.getKey();
            Direction.Axis axis = direction.getAxis();
            key.getValue().setup((KineticBlockEntity)this.blockEntity, axis, this.getSpeed(direction)).setChanged();
        }
    }

    public void updateLight(float partialTick) {
        this.relight((FlatLit[])this.keys.values().toArray(FlatLit[]::new));
    }

    protected void _delete() {
        this.keys.values().forEach(AbstractInstance::delete);
        this.keys.clear();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        this.keys.values().forEach(consumer);
    }
}
