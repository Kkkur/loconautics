/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.AbstractInstance
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  dev.engine_room.flywheel.lib.util.RecyclingPoseStack
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmRenderer;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.engine_room.flywheel.lib.util.RecyclingPoseStack;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.Property;

public class ArmVisual
extends SingleAxisRotatingVisual<ArmBlockEntity>
implements SimpleDynamicVisual {
    final TransformedInstance base;
    final TransformedInstance lowerBody;
    final TransformedInstance upperBody;
    final TransformedInstance claw;
    private final ArrayList<TransformedInstance> clawGrips;
    private final ArrayList<TransformedInstance> models;
    private final boolean ceiling;
    private final RecyclingPoseStack poseStack = new RecyclingPoseStack();
    private boolean wasDancing = false;
    private float baseAngle = Float.NaN;
    private float lowerArmAngle = Float.NaN;
    private float upperArmAngle = Float.NaN;
    private float headAngle = Float.NaN;

    public ArmVisual(VisualizationContext context, ArmBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.partial((PartialModel)AllPartialModels.ARM_COG));
        this.base = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.ARM_BASE)).createInstance();
        this.lowerBody = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.ARM_LOWER_BODY)).createInstance();
        this.upperBody = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.ARM_UPPER_BODY)).createInstance();
        this.claw = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)(blockEntity.goggles ? AllPartialModels.ARM_CLAW_BASE_GOGGLES : AllPartialModels.ARM_CLAW_BASE))).createInstance();
        TransformedInstance clawGrip1 = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.ARM_CLAW_GRIP_UPPER)).createInstance();
        TransformedInstance clawGrip2 = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.ARM_CLAW_GRIP_LOWER)).createInstance();
        this.clawGrips = Lists.newArrayList((Object[])new TransformedInstance[]{clawGrip1, clawGrip2});
        this.models = Lists.newArrayList((Object[])new TransformedInstance[]{this.base, this.lowerBody, this.upperBody, this.claw, clawGrip1, clawGrip2});
        this.ceiling = (Boolean)this.blockState.getValue((Property)ArmBlock.CEILING);
        PoseTransformStack msr = TransformStack.of((PoseStack)this.poseStack);
        msr.translate((Vec3i)this.getVisualPosition());
        msr.center();
        if (this.ceiling) {
            msr.rotateXDegrees(180.0f);
        }
        this.animate(partialTick);
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.animate(ctx.partialTick());
    }

    private void animate(float pt) {
        if (((ArmBlockEntity)this.blockEntity).phase == ArmBlockEntity.Phase.DANCING && ((ArmBlockEntity)this.blockEntity).getSpeed() != 0.0f) {
            this.animateRave(pt);
            this.wasDancing = true;
            return;
        }
        float baseAngleNow = ((ArmBlockEntity)this.blockEntity).baseAngle.getValue(pt);
        float lowerArmAngleNow = ((ArmBlockEntity)this.blockEntity).lowerArmAngle.getValue(pt);
        float upperArmAngleNow = ((ArmBlockEntity)this.blockEntity).upperArmAngle.getValue(pt);
        float headAngleNow = ((ArmBlockEntity)this.blockEntity).headAngle.getValue(pt);
        boolean settled = Mth.equal((float)this.baseAngle, (float)baseAngleNow) && Mth.equal((float)this.lowerArmAngle, (float)lowerArmAngleNow) && Mth.equal((float)this.upperArmAngle, (float)upperArmAngleNow) && Mth.equal((float)this.headAngle, (float)headAngleNow);
        this.baseAngle = baseAngleNow;
        this.lowerArmAngle = lowerArmAngleNow;
        this.upperArmAngle = upperArmAngleNow;
        this.headAngle = headAngleNow;
        if (!settled || this.wasDancing) {
            this.animateArm();
        }
        this.wasDancing = false;
    }

    private void animateRave(float partialTick) {
        int ticks = AnimationTickHolder.getTicks((LevelAccessor)((ArmBlockEntity)this.blockEntity).getLevel());
        float renderTick = (float)ticks + partialTick + (float)(((ArmBlockEntity)this.blockEntity).hashCode() % 64);
        float baseAngle = renderTick * 10.0f % 360.0f;
        float lowerArmAngle = Mth.lerp((float)((Mth.sin((float)(renderTick / 4.0f)) + 1.0f) / 2.0f), (float)-45.0f, (float)15.0f);
        float upperArmAngle = Mth.lerp((float)((Mth.sin((float)(renderTick / 8.0f)) + 1.0f) / 4.0f), (float)-45.0f, (float)95.0f);
        float headAngle = -lowerArmAngle;
        int color = Color.rainbowColor((int)(ticks * 100)).getRGB();
        this.updateAngles(baseAngle, lowerArmAngle, upperArmAngle, headAngle, color);
    }

    private void animateArm() {
        this.updateAngles(this.baseAngle, this.lowerArmAngle - 135.0f, this.upperArmAngle - 90.0f, this.headAngle, 0xFFFFFF);
    }

    private void updateAngles(float baseAngle, float lowerArmAngle, float upperArmAngle, float headAngle, int color) {
        this.poseStack.pushPose();
        PoseTransformStack msr = TransformStack.of((PoseStack)this.poseStack);
        ArmRenderer.transformBase((TransformStack)msr, baseAngle);
        this.base.setTransform((PoseStack)this.poseStack).setChanged();
        ArmRenderer.transformLowerArm((TransformStack)msr, lowerArmAngle);
        this.lowerBody.setTransform((PoseStack)this.poseStack).colorRgb(color).setChanged();
        ArmRenderer.transformUpperArm((TransformStack)msr, upperArmAngle);
        this.upperBody.setTransform((PoseStack)this.poseStack).colorRgb(color).setChanged();
        ArmRenderer.transformHead((TransformStack)msr, headAngle);
        if (this.ceiling && ((ArmBlockEntity)this.blockEntity).goggles) {
            msr.rotateZDegrees(180.0f);
        }
        this.claw.setTransform((PoseStack)this.poseStack).setChanged();
        if (this.ceiling && ((ArmBlockEntity)this.blockEntity).goggles) {
            msr.rotateZDegrees(180.0f);
        }
        ItemStack item = ((ArmBlockEntity)this.blockEntity).heldItem;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        boolean hasItem = !item.isEmpty();
        boolean isBlockItem = hasItem && item.getItem() instanceof BlockItem && itemRenderer.getModel(item, (Level)Minecraft.getInstance().level, null, 0).isGui3d();
        for (int index : Iterate.zeroAndOne) {
            this.poseStack.pushPose();
            int flip = index * 2 - 1;
            ArmRenderer.transformClawHalf((TransformStack)msr, hasItem, isBlockItem, flip);
            this.clawGrips.get(index).setTransform((PoseStack)this.poseStack).setChanged();
            this.poseStack.popPose();
        }
        this.poseStack.popPose();
    }

    @Override
    public void update(float pt) {
        super.update(pt);
        this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)(((ArmBlockEntity)this.blockEntity).goggles ? AllPartialModels.ARM_CLAW_BASE_GOGGLES : AllPartialModels.ARM_CLAW_BASE))).stealInstance((Instance)this.claw);
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight((FlatLit[])this.models.toArray(FlatLit[]::new));
    }

    @Override
    protected void _delete() {
        super._delete();
        this.models.forEach(AbstractInstance::delete);
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        this.models.forEach((Consumer<TransformedInstance>)consumer);
    }
}
