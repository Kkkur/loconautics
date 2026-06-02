/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.instance.Instancer
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.bogey;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.processing.burner.ScrollTransformedInstance;
import com.simibubi.create.content.trains.bogey.StandardBogeyVisual;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import com.simibubi.create.foundation.render.SpecialModels;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import java.util.function.Consumer;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public static class StandardBogeyVisual.Large
extends StandardBogeyVisual {
    private final TransformedInstance secondaryShaft1;
    private final TransformedInstance secondaryShaft2;
    private final TransformedInstance drive;
    private final ScrollTransformedInstance belt;
    private final TransformedInstance piston;
    private final TransformedInstance wheels;
    private final TransformedInstance pin;

    public StandardBogeyVisual.Large(VisualizationContext ctx, float partialTick, boolean inContraption) {
        super(ctx, partialTick, inContraption);
        Instancer secondaryShaftInstancer = ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.smoothLit(AllPartialModels.SHAFT));
        this.secondaryShaft1 = (TransformedInstance)secondaryShaftInstancer.createInstance();
        this.secondaryShaft2 = (TransformedInstance)secondaryShaftInstancer.createInstance();
        this.drive = (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.smoothLit(AllPartialModels.BOGEY_DRIVE)).createInstance();
        this.belt = (ScrollTransformedInstance)ctx.instancerProvider().instancer(AllInstanceTypes.SCROLLING_TRANSFORMED, SpecialModels.smoothLit(AllPartialModels.BOGEY_DRIVE_BELT)).createInstance();
        this.piston = (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.smoothLit(AllPartialModels.BOGEY_PISTON)).createInstance();
        this.wheels = (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.smoothLit(AllPartialModels.LARGE_BOGEY_WHEELS)).createInstance();
        this.pin = (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.smoothLit(AllPartialModels.BOGEY_PIN)).createInstance();
        this.belt.setSpriteShift(AllSpriteShifts.BOGEY_BELT);
    }

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle, PoseStack poseStack) {
        super.update(bogeyData, wheelAngle, poseStack);
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.secondaryShaft1.setTransform(poseStack).translate(-0.5f, 0.25f, 0.5f).center()).rotateTo(Direction.UP, Direction.EAST)).rotateYDegrees(wheelAngle)).uncenter()).setChanged();
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.secondaryShaft2.setTransform(poseStack).translate(-0.5f, 0.25f, -1.5f).center()).rotateTo(Direction.UP, Direction.EAST)).rotateYDegrees(wheelAngle)).uncenter()).setChanged();
        ((TransformedInstance)this.drive.setTransform(poseStack).scale(0.9980469f)).setChanged();
        ((TransformedInstance)this.belt.offset(0.0f, 0.0054541538f * wheelAngle).setTransform(poseStack).scale(0.9980469f)).setChanged();
        ((TransformedInstance)this.piston.setTransform(poseStack).translate(0.0, 0.0, 0.25 * Math.sin(AngleHelper.rad((double)wheelAngle)))).setChanged();
        ((TransformedInstance)this.wheels.setTransform(poseStack).translate(0.0f, 1.0f, 0.0f).rotateXDegrees(wheelAngle)).setChanged();
        ((TransformedInstance)((TransformedInstance)this.pin.setTransform(poseStack).translate(0.0f, 1.0f, 0.0f).rotateXDegrees(wheelAngle)).translate(0.0f, 0.25f, 0.0f).rotateXDegrees(-wheelAngle)).setChanged();
    }

    @Override
    public void hide() {
        super.hide();
        this.secondaryShaft1.setZeroTransform().setChanged();
        this.secondaryShaft2.setZeroTransform().setChanged();
        this.wheels.setZeroTransform().setChanged();
        this.drive.setZeroTransform().setChanged();
        this.belt.setZeroTransform().setChanged();
        this.piston.setZeroTransform().setChanged();
        this.pin.setZeroTransform().setChanged();
    }

    @Override
    public void updateLight(int packedLight) {
        super.updateLight(packedLight);
        this.secondaryShaft1.light(packedLight).setChanged();
        this.secondaryShaft2.light(packedLight).setChanged();
        this.wheels.light(packedLight).setChanged();
        this.drive.light(packedLight).setChanged();
        this.belt.light(packedLight).setChanged();
        this.piston.light(packedLight).setChanged();
        this.pin.light(packedLight).setChanged();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.secondaryShaft1);
        consumer.accept((Instance)this.secondaryShaft2);
        consumer.accept((Instance)this.wheels);
        consumer.accept((Instance)this.drive);
        consumer.accept((Instance)this.belt);
        consumer.accept((Instance)this.piston);
        consumer.accept((Instance)this.pin);
    }

    @Override
    public void delete() {
        super.delete();
        this.secondaryShaft1.delete();
        this.secondaryShaft2.delete();
        this.wheels.delete();
        this.drive.delete();
        this.belt.delete();
        this.piston.delete();
        this.pin.delete();
    }
}
