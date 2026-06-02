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
 *  net.minecraft.nbt.CompoundTag
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.bogey;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.bogey.StandardBogeyVisual;
import com.simibubi.create.foundation.render.SpecialModels;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public static class StandardBogeyVisual.Small
extends StandardBogeyVisual {
    private final TransformedInstance frame;
    private final TransformedInstance wheel1;
    private final TransformedInstance wheel2;

    public StandardBogeyVisual.Small(VisualizationContext ctx, float partialTick, boolean inContraption) {
        super(ctx, partialTick, inContraption);
        Instancer wheelInstancer = ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.smoothLit(AllPartialModels.SMALL_BOGEY_WHEELS));
        this.frame = (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.smoothLit(AllPartialModels.BOGEY_FRAME)).createInstance();
        this.wheel1 = (TransformedInstance)wheelInstancer.createInstance();
        this.wheel2 = (TransformedInstance)wheelInstancer.createInstance();
    }

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle, PoseStack poseStack) {
        super.update(bogeyData, wheelAngle, poseStack);
        ((TransformedInstance)this.wheel1.setTransform(poseStack).translate(0.0f, 0.75f, -1.0f).rotateXDegrees(wheelAngle)).setChanged();
        ((TransformedInstance)this.wheel2.setTransform(poseStack).translate(0.0f, 0.75f, 1.0f).rotateXDegrees(wheelAngle)).setChanged();
        ((TransformedInstance)this.frame.setTransform(poseStack).scale(0.9980469f)).setChanged();
    }

    @Override
    public void hide() {
        super.hide();
        this.frame.setZeroTransform().setChanged();
        this.wheel1.setZeroTransform().setChanged();
        this.wheel2.setZeroTransform().setChanged();
    }

    @Override
    public void updateLight(int packedLight) {
        super.updateLight(packedLight);
        this.frame.light(packedLight).setChanged();
        this.wheel1.light(packedLight).setChanged();
        this.wheel2.light(packedLight).setChanged();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.frame);
        consumer.accept((Instance)this.wheel1);
        consumer.accept((Instance)this.wheel2);
    }

    @Override
    public void delete() {
        super.delete();
        this.frame.delete();
        this.wheel1.delete();
        this.wheel2.delete();
    }
}
