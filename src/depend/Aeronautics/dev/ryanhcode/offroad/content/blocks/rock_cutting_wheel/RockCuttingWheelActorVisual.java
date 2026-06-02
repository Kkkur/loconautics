/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.behaviour.MovementContext
 *  com.simibubi.create.content.contraptions.render.ActorVisual
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Quaternionfc
 */
package dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel.RockCuttingWheelBlock;
import dev.ryanhcode.offroad.index.OffroadPartialModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionfc;

public class RockCuttingWheelActorVisual
extends ActorVisual {
    private final TransformedInstance wheel;
    private final Direction facing;
    private final boolean axisFirst;

    public RockCuttingWheelActorVisual(VisualizationContext visualizationContext, BlockAndTintGetter world, MovementContext context) {
        super(visualizationContext, world, context);
        this.facing = (Direction)context.state.getValue((Property)BlockStateProperties.FACING);
        this.axisFirst = (Boolean)context.state.getValue((Property)RockCuttingWheelBlock.AXIS_ALONG_FIRST_COORDINATE);
        this.wheel = (TransformedInstance)visualizationContext.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)OffroadPartialModels.ROCK_CUTTING_WHEEL_WHEEL)).createInstance();
        this.wheel.light(this.localBlockLight(), 0);
        this.wheel.setChanged();
    }

    public void beginFrame() {
        this.wheel.setIdentityTransform().translate((Vec3i)this.context.localPos);
        if ((this.facing.getAxis() == Direction.Axis.Z || this.facing.getAxis() == Direction.Axis.Y) ^ this.axisFirst) {
            ((TransformedInstance)((TransformedInstance)((TransformedInstance)this.wheel.rotateCentered((Quaternionfc)this.facing.getRotation())).rotateZCenteredDegrees(90.0f)).rotateXCenteredDegrees(0.0f)).translate(0.625, 0.5, 0.0);
        } else {
            ((TransformedInstance)((TransformedInstance)((TransformedInstance)this.wheel.rotateCentered((Quaternionfc)this.facing.getRotation())).rotateZCenteredDegrees(0.0f)).rotateXCenteredDegrees(90.0f)).translate(0.0, 0.5, -0.625);
        }
        this.wheel.rotateYCenteredDegrees(((LerpedFloat)this.context.temporaryData).getValue(AnimationTickHolder.getPartialTicks()));
        this.wheel.setChanged();
    }

    protected void _delete() {
        this.wheel.delete();
    }
}
