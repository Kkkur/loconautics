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
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package com.simibubi.create.content.logistics.packagePort.frogport;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import java.util.function.Consumer;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class FrogportVisual
extends AbstractBlockEntityVisual<FrogportBlockEntity>
implements SimpleDynamicVisual {
    private final TransformedInstance body;
    private TransformedInstance head;
    private final TransformedInstance tongue;
    private final TransformedInstance rig;
    private final TransformedInstance box;
    private final Matrix4f basePose = new Matrix4f();
    private float lastYaw = Float.NaN;
    private float lastHeadPitch = Float.NaN;
    private float lastTonguePitch = Float.NaN;
    private float lastTongueLength = Float.NaN;
    private boolean lastGoggles = false;

    public FrogportVisual(VisualizationContext ctx, FrogportBlockEntity blockEntity, float partialTick) {
        super(ctx, (BlockEntity)blockEntity, partialTick);
        this.body = (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.FROGPORT_BODY)).createInstance();
        this.head = (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.FROGPORT_HEAD)).createInstance();
        this.tongue = (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.FROGPORT_TONGUE)).createInstance();
        this.rig = (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.block((BlockState)Blocks.AIR.defaultBlockState())).createInstance();
        this.box = (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.block((BlockState)Blocks.AIR.defaultBlockState())).createInstance();
        this.rig.handle().setVisible(false);
        this.box.handle().setVisible(false);
        this.animate(partialTick);
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.animate(ctx.partialTick());
    }

    private void animate(float partialTicks) {
        this.updateGoggles();
        float yaw = ((FrogportBlockEntity)this.blockEntity).getYaw();
        float headPitch = 80.0f;
        float tonguePitch = 0.0f;
        float tongueLength = 0.0f;
        float headPitchModifier = 1.0f;
        boolean hasTarget = ((FrogportBlockEntity)this.blockEntity).target != null;
        boolean animating = ((FrogportBlockEntity)this.blockEntity).isAnimationInProgress();
        boolean depositing = ((FrogportBlockEntity)this.blockEntity).currentlyDepositing;
        Vec3 diff = Vec3.ZERO;
        if (hasTarget) {
            diff = ((FrogportBlockEntity)this.blockEntity).target.getExactTargetLocation((PackagePortBlockEntity)this.blockEntity, (LevelAccessor)((FrogportBlockEntity)this.blockEntity).getLevel(), ((FrogportBlockEntity)this.blockEntity).getBlockPos()).subtract(0.0, animating && depositing ? 0.0 : 0.75, 0.0).subtract(Vec3.atCenterOf((Vec3i)((FrogportBlockEntity)this.blockEntity).getBlockPos()));
            tonguePitch = (float)Mth.atan2((double)diff.y, (double)(diff.multiply(1.0, 0.0, 1.0).length() + 0.1875)) * 57.295776f;
            tongueLength = Math.max((float)diff.length(), 1.0f);
            headPitch = Mth.clamp((float)(tonguePitch * 2.0f), (float)60.0f, (float)100.0f);
        }
        if (animating) {
            float progress = ((FrogportBlockEntity)this.blockEntity).animationProgress.getValue(partialTicks);
            float scale = 1.0f;
            float itemDistance = 0.0f;
            if (depositing) {
                double modifier = Math.max(0.0, 1.0 - Math.pow(((double)progress - 0.25) * 4.0 - 1.0, 4.0));
                itemDistance = (float)Math.max((double)tongueLength * Math.min(1.0, ((double)progress - 0.25) * 3.0), (double)tongueLength * modifier);
                tongueLength = (float)((double)tongueLength * Math.max(0.0, 1.0 - Math.pow(((double)progress * 1.25 - 0.25) * 4.0 - 1.0, 4.0)));
                headPitchModifier = (float)Math.max(0.0, 1.0 - Math.pow((double)progress * 1.25 * 2.0 - 1.0, 4.0));
                scale = 0.25f + progress * 3.0f / 4.0f;
            } else {
                tongueLength = (float)((double)tongueLength * Math.pow(Math.max(0.0, 1.0 - (double)progress * 1.25), 5.0));
                headPitchModifier = 1.0f - (float)Math.min(1.0, Math.max(0.0, (Math.pow((double)progress * 1.5, 2.0) - 0.5) * 2.0));
                scale = (float)Math.max(0.5, 1.0 - (double)progress * 1.25);
                itemDistance = tongueLength;
            }
            this.renderPackage(diff, scale, itemDistance);
        } else {
            tongueLength = 0.0f;
            float anticipation = ((FrogportBlockEntity)this.blockEntity).anticipationProgress.getValue(partialTicks);
            headPitchModifier = anticipation > 0.0f ? (float)Math.max(0.0, 1.0 - Math.pow((double)anticipation * 1.25 * 2.0 - 1.0, 4.0)) : 0.0f;
            this.rig.handle().setVisible(false);
            this.box.handle().setVisible(false);
        }
        headPitch *= headPitchModifier;
        headPitch = Math.max(headPitch, ((FrogportBlockEntity)this.blockEntity).manualOpenAnimationProgress.getValue(partialTicks) * 60.0f);
        tongueLength = Math.max(tongueLength, ((FrogportBlockEntity)this.blockEntity).manualOpenAnimationProgress.getValue(partialTicks) * 0.25f);
        if (yaw != this.lastYaw) {
            ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.body.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).center()).rotateYDegrees(yaw)).uncenter()).setChanged();
            this.basePose.set((Matrix4fc)this.body.pose).translate(0.5f, 0.625f, 0.6875f);
            this.lastYaw = yaw;
            this.lastTonguePitch = Float.NaN;
            this.lastHeadPitch = Float.NaN;
        }
        if (headPitch != this.lastHeadPitch) {
            ((TransformedInstance)((TransformedInstance)this.head.setTransform((Matrix4fc)this.basePose).rotateXDegrees(headPitch)).translateBack(0.5f, 0.625f, 0.6875f)).setChanged();
            this.lastHeadPitch = headPitch;
        }
        if (tonguePitch != this.lastTonguePitch || tongueLength != this.lastTongueLength) {
            ((TransformedInstance)((TransformedInstance)this.tongue.setTransform((Matrix4fc)this.basePose).rotateXDegrees(tonguePitch)).scale(1.0f, 1.0f, tongueLength / 0.4375f).translateBack(0.5f, 0.625f, 0.6875f)).setChanged();
            this.lastTonguePitch = tonguePitch;
            this.lastTongueLength = tongueLength;
        }
    }

    public void updateGoggles() {
        if (((FrogportBlockEntity)this.blockEntity).goggles && !this.lastGoggles) {
            this.head.delete();
            this.head = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.FROGPORT_HEAD_GOGGLES)).createInstance();
            this.lastHeadPitch = -1.0f;
            this.updateLight(0.0f);
            this.lastGoggles = true;
        }
        if (!((FrogportBlockEntity)this.blockEntity).goggles && this.lastGoggles) {
            this.head.delete();
            this.head = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.FROGPORT_HEAD)).createInstance();
            this.lastHeadPitch = -1.0f;
            this.updateLight(0.0f);
            this.lastGoggles = false;
        }
    }

    private void renderPackage(Vec3 diff, float scale, float itemDistance) {
        if (((FrogportBlockEntity)this.blockEntity).animatedPackage == null || (double)scale < 0.45) {
            this.rig.handle().setVisible(false);
            this.box.handle().setVisible(false);
            return;
        }
        ResourceLocation key = BuiltInRegistries.ITEM.getKey((Object)((FrogportBlockEntity)this.blockEntity).animatedPackage.getItem());
        if (key == BuiltInRegistries.ITEM.getDefaultKey()) {
            this.rig.handle().setVisible(false);
            this.box.handle().setVisible(false);
            return;
        }
        boolean animating = ((FrogportBlockEntity)this.blockEntity).isAnimationInProgress();
        boolean depositing = ((FrogportBlockEntity)this.blockEntity).currentlyDepositing;
        this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.PACKAGES.get(key))).stealInstance((Instance)this.box);
        this.box.handle().setVisible(true);
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.box.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).translate(0.0f, 0.1875f, 0.0f).translate(diff.normalize().scale((double)itemDistance).subtract(0.0, animating && depositing ? 0.75 : 0.0, 0.0))).center()).scale(scale)).uncenter()).setChanged();
        if (!depositing) {
            this.rig.handle().setVisible(false);
            return;
        }
        this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.PACKAGE_RIGGING.get(key))).stealInstance((Instance)this.rig);
        this.rig.handle().setVisible(true);
        this.rig.pose.set((Matrix4fc)this.box.pose);
        this.rig.setChanged();
    }

    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept((Instance)this.body);
        consumer.accept((Instance)this.head);
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.body, this.head, this.tongue, this.rig, this.box});
    }

    protected void _delete() {
        this.body.delete();
        this.head.delete();
        this.tongue.delete();
        this.rig.delete();
        this.box.delete();
    }
}
