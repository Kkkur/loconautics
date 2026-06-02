/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.instance.Instancer
 *  dev.engine_room.flywheel.api.model.Model
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.item.DyeColor
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package com.simibubi.create.content.kinetics.belt;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltRenderer;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.processing.burner.ScrollInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.function.Consumer;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class BeltVisual
extends KineticBlockEntityVisual<BeltBlockEntity> {
    public static final float MAGIC_SCROLL_MULTIPLIER = 0.001984127f;
    public static final float SCROLL_FACTOR_DIAGONAL = 0.375f;
    public static final float SCROLL_FACTOR_OTHERWISE = 0.5f;
    public static final float SCROLL_OFFSET_BOTTOM = 0.5f;
    public static final float SCROLL_OFFSET_OTHERWISE = 0.0f;
    protected final ScrollInstance[] belts;
    @Nullable
    protected final RotatingInstance pulley;

    public BeltVisual(VisualizationContext context, BeltBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        BeltPart part = (BeltPart)((Object)this.blockState.getValue(BeltBlock.PART));
        boolean start = part == BeltPart.START;
        boolean end = part == BeltPart.END;
        DyeColor color = blockEntity.color.orElse(null);
        boolean diagonal = ((BeltSlope)((Object)this.blockState.getValue(BeltBlock.SLOPE))).isDiagonal();
        this.belts = new ScrollInstance[diagonal ? 1 : 2];
        for (boolean bottom : Iterate.trueAndFalse) {
            PartialModel beltPartial = BeltRenderer.getBeltPartial(diagonal, start, end, bottom);
            SpriteShiftEntry spriteShift = BeltRenderer.getSpriteShiftEntry(color, diagonal, bottom);
            Instancer beltModel = this.instancerProvider().instancer(AllInstanceTypes.SCROLLING, Models.partial((PartialModel)beltPartial));
            this.belts[bottom ? 0 : 1] = this.setup((ScrollInstance)beltModel.createInstance(), bottom, spriteShift);
            if (diagonal) break;
        }
        if (blockEntity.hasPulley()) {
            this.pulley = (RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, this.getPulleyModel()).createInstance();
            this.pulley.setup((KineticBlockEntity)this.blockEntity).setPosition((Vec3i)this.getVisualPosition()).setChanged();
        } else {
            this.pulley = null;
        }
    }

    public void update(float pt) {
        DyeColor color = ((BeltBlockEntity)this.blockEntity).color.orElse(null);
        boolean diagonal = ((BeltSlope)((Object)this.blockState.getValue(BeltBlock.SLOPE))).isDiagonal();
        boolean bottom = true;
        for (ScrollInstance key : this.belts) {
            this.setup(key, bottom, BeltRenderer.getSpriteShiftEntry(color, diagonal, bottom));
            bottom = false;
        }
        if (this.pulley != null) {
            this.pulley.setup((KineticBlockEntity)this.blockEntity).setChanged();
        }
    }

    public void updateLight(float partialTick) {
        this.relight((FlatLit[])this.belts);
        if (this.pulley != null) {
            this.relight(new FlatLit[]{this.pulley});
        }
    }

    protected void _delete() {
        for (ScrollInstance key : this.belts) {
            key.delete();
        }
        if (this.pulley != null) {
            this.pulley.delete();
        }
    }

    private Model getPulleyModel() {
        Direction dir = this.getOrientation();
        return Models.partial((PartialModel)AllPartialModels.BELT_PULLEY, (Object)dir.getAxis(), (axis11, modelTransform1) -> {
            PoseTransformStack msr = TransformStack.of((PoseStack)modelTransform1);
            msr.center();
            if (axis11 == Direction.Axis.X) {
                msr.rotateYDegrees(90.0f);
            }
            if (axis11 == Direction.Axis.Y) {
                msr.rotateXDegrees(90.0f);
            }
            msr.rotateXDegrees(90.0f);
            msr.uncenter();
        });
    }

    private Direction getOrientation() {
        Direction dir = ((Direction)this.blockState.getValue(BeltBlock.HORIZONTAL_FACING)).getClockWise();
        if (this.blockState.getValue(BeltBlock.SLOPE) == BeltSlope.SIDEWAYS) {
            dir = Direction.UP;
        }
        return dir;
    }

    private ScrollInstance setup(ScrollInstance key, boolean bottom, SpriteShiftEntry spriteShift) {
        BeltSlope beltSlope = (BeltSlope)((Object)this.blockState.getValue(BeltBlock.SLOPE));
        Direction facing = (Direction)this.blockState.getValue(BeltBlock.HORIZONTAL_FACING);
        boolean diagonal = beltSlope.isDiagonal();
        boolean sideways = beltSlope == BeltSlope.SIDEWAYS;
        boolean vertical = beltSlope == BeltSlope.VERTICAL;
        boolean upward = beltSlope == BeltSlope.UPWARD;
        boolean alongX = facing.getAxis() == Direction.Axis.X;
        boolean alongZ = facing.getAxis() == Direction.Axis.Z;
        boolean downward = beltSlope == BeltSlope.DOWNWARD;
        float speed = ((BeltBlockEntity)this.blockEntity).getSpeed();
        if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE ^ upward ^ (alongX && !diagonal || alongZ && diagonal)) {
            speed = -speed;
        }
        if (sideways && (facing == Direction.SOUTH || facing == Direction.WEST) || vertical && facing == Direction.EAST) {
            speed = -speed;
        }
        float rotX = (!diagonal && beltSlope != BeltSlope.HORIZONTAL ? 90 : 0) + (downward ? 180 : 0) + (sideways ? 90 : 0) + (vertical && alongZ ? 180 : 0);
        float rotY = facing.toYRot() + (float)(diagonal ^ alongX && !downward ? 180 : 0) + (float)(sideways && alongZ ? 180 : 0) + (float)(vertical && alongX ? 90 : 0);
        float rotZ = (sideways ? 90 : 0) + (vertical && alongX ? 90 : 0);
        Quaternionf q = new Quaternionf().rotationXYZ(rotX * ((float)Math.PI / 180), rotY * ((float)Math.PI / 180), rotZ * ((float)Math.PI / 180));
        key.setSpriteShift(spriteShift, 1.0f, diagonal ? 0.375f : 0.5f).position((Vec3i)this.getVisualPosition()).rotation((Quaternionfc)q).speed(0.0f, speed * 0.001984127f).offset(0.0f, bottom ? 0.5f : 0.0f).colorRgb(RotatingInstance.colorFromBE((KineticBlockEntity)this.blockEntity)).setChanged();
        return key;
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        if (this.pulley != null) {
            consumer.accept((Instance)this.pulley);
        }
        for (ScrollInstance key : this.belts) {
            consumer.accept((Instance)key);
        }
    }
}
