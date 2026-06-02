/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.AbstractInstance
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.content.kinetics.transmission;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;

public class SplitShaftVisual
extends KineticBlockEntityVisual<SplitShaftBlockEntity> {
    protected final ArrayList<RotatingInstance> keys = new ArrayList(2);

    public SplitShaftVisual(VisualizationContext modelManager, SplitShaftBlockEntity blockEntity, float partialTick) {
        super(modelManager, blockEntity, partialTick);
        float speed = blockEntity.getSpeed();
        for (Direction dir : Iterate.directionsInAxis((Direction.Axis)this.rotationAxis())) {
            float splitSpeed = speed * blockEntity.getRotationSpeedModifier(dir);
            RotatingInstance instance = (RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.SHAFT_HALF)).createInstance();
            instance.setup((KineticBlockEntity)blockEntity, splitSpeed).setPosition((Vec3i)this.getVisualPosition()).rotateToFace(Direction.SOUTH, dir).setChanged();
            this.keys.add(instance);
        }
    }

    public void update(float pt) {
        Block block = this.blockState.getBlock();
        Direction.Axis boxAxis = ((IRotate)block).getRotationAxis(this.blockState);
        Direction[] directions = Iterate.directionsInAxis((Direction.Axis)boxAxis);
        for (int i : Iterate.zeroAndOne) {
            this.keys.get(i).setup((KineticBlockEntity)this.blockEntity, ((SplitShaftBlockEntity)this.blockEntity).getSpeed() * ((SplitShaftBlockEntity)this.blockEntity).getRotationSpeedModifier(directions[i])).setChanged();
        }
    }

    public void updateLight(float partialTick) {
        this.relight((FlatLit[])this.keys.toArray(FlatLit[]::new));
    }

    protected void _delete() {
        this.keys.forEach(AbstractInstance::delete);
        this.keys.clear();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        this.keys.forEach((Consumer<RotatingInstance>)consumer);
    }
}
