/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.fan;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class FanVisual
extends KineticBlockEntityVisual<EncasedFanBlockEntity> {
    protected final RotatingInstance shaft;
    protected final RotatingInstance fan;
    final Direction direction;
    private final Direction opposite;

    public FanVisual(VisualizationContext context, EncasedFanBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        this.direction = (Direction)this.blockState.getValue((Property)BlockStateProperties.FACING);
        this.opposite = this.direction.getOpposite();
        this.shaft = (RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.SHAFT_HALF)).createInstance();
        this.fan = (RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.ENCASED_FAN_INNER)).createInstance();
        this.shaft.setup(blockEntity).setPosition((Vec3i)this.getVisualPosition()).rotateToFace(Direction.SOUTH, this.opposite).setChanged();
        this.fan.setup((KineticBlockEntity)blockEntity, this.getFanSpeed()).setPosition((Vec3i)this.getVisualPosition()).rotateToFace(Direction.SOUTH, this.opposite).setChanged();
    }

    private float getFanSpeed() {
        float speed = ((EncasedFanBlockEntity)this.blockEntity).getSpeed() * 5.0f;
        if (speed > 0.0f) {
            speed = Mth.clamp((float)speed, (float)80.0f, (float)1280.0f);
        }
        if (speed < 0.0f) {
            speed = Mth.clamp((float)speed, (float)-1280.0f, (float)-80.0f);
        }
        return speed;
    }

    public void update(float pt) {
        this.shaft.setup((KineticBlockEntity)this.blockEntity).setChanged();
        this.fan.setup((KineticBlockEntity)this.blockEntity, this.getFanSpeed()).setChanged();
    }

    public void updateLight(float partialTick) {
        BlockPos behind = this.pos.relative(this.opposite);
        this.relight(behind, new FlatLit[]{this.shaft});
        BlockPos inFront = this.pos.relative(this.direction);
        this.relight(inFront, new FlatLit[]{this.fan});
    }

    protected void _delete() {
        this.shaft.delete();
        this.fan.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept((Instance)this.shaft);
        consumer.accept((Instance)this.fan);
    }
}
