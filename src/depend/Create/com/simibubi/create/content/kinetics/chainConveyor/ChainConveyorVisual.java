/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instancer
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visual.TickableVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleTickableVisual
 *  dev.engine_room.flywheel.lib.visual.util.SmartRecycler
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.render.SpecialModels;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import dev.engine_room.flywheel.lib.visual.util.SmartRecycler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class ChainConveyorVisual
extends SingleAxisRotatingVisual<ChainConveyorBlockEntity>
implements SimpleDynamicVisual,
SimpleTickableVisual {
    private final List<TransformedInstance> guards = new ArrayList<TransformedInstance>();
    private final SmartRecycler<ResourceLocation, TransformedInstance> boxes;
    private final SmartRecycler<ResourceLocation, TransformedInstance> rigging;

    public ChainConveyorVisual(VisualizationContext context, ChainConveyorBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.partial((PartialModel)AllPartialModels.CHAIN_CONVEYOR_SHAFT));
        this.setupGuards();
        this.boxes = new SmartRecycler(key -> (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.PACKAGES.get(key))).createInstance());
        this.rigging = new SmartRecycler(key -> (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.PACKAGE_RIGGING.get(key))).createInstance());
    }

    @Override
    public void update(float pt) {
        super.update(pt);
        this.setupGuards();
    }

    @Override
    public void tick(TickableVisual.Context context) {
        ((ChainConveyorBlockEntity)this.blockEntity).tickBoxVisuals();
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        float partialTicks = ctx.partialTick();
        this.boxes.resetCount();
        this.rigging.resetCount();
        for (ChainConveyorPackage chainConveyorPackage : ((ChainConveyorBlockEntity)this.blockEntity).loopingPackages) {
            this.setupBoxVisual((ChainConveyorBlockEntity)this.blockEntity, chainConveyorPackage, partialTicks);
        }
        for (Map.Entry entry : ((ChainConveyorBlockEntity)this.blockEntity).travellingPackages.entrySet()) {
            for (ChainConveyorPackage box : (List)entry.getValue()) {
                this.setupBoxVisual((ChainConveyorBlockEntity)this.blockEntity, box, partialTicks);
            }
        }
        this.boxes.discardExtra();
        this.rigging.discardExtra();
    }

    private void setupBoxVisual(ChainConveyorBlockEntity be, ChainConveyorPackage box, float partialTicks) {
        if (box.worldPosition == null) {
            return;
        }
        if (box.item == null || box.item.isEmpty()) {
            return;
        }
        ChainConveyorPackage.ChainConveyorPackagePhysicsData physicsData = box.physicsData((LevelAccessor)be.getLevel());
        if (physicsData.prevPos == null) {
            return;
        }
        Vec3 position = physicsData.prevPos.lerp(physicsData.pos, (double)partialTicks);
        Vec3 targetPosition = physicsData.prevTargetPos.lerp(physicsData.targetPos, (double)partialTicks);
        float yaw = AngleHelper.angleLerp((double)partialTicks, (double)physicsData.prevYaw, (double)physicsData.yaw);
        Vec3 offset = new Vec3(targetPosition.x - (double)this.pos.getX(), targetPosition.y - (double)this.pos.getY(), targetPosition.z - (double)this.pos.getZ());
        BlockPos containingPos = BlockPos.containing((Position)position);
        Level level = be.getLevel();
        int light = LightTexture.pack((int)level.getBrightness(LightLayer.BLOCK, containingPos), (int)level.getBrightness(LightLayer.SKY, containingPos));
        if (physicsData.modelKey == null) {
            ResourceLocation key = BuiltInRegistries.ITEM.getKey((Object)box.item.getItem());
            if (key == BuiltInRegistries.ITEM.getDefaultKey()) {
                return;
            }
            physicsData.modelKey = key;
        }
        TransformedInstance rigBuffer = (TransformedInstance)this.rigging.get((Object)physicsData.modelKey);
        TransformedInstance boxBuffer = (TransformedInstance)this.boxes.get((Object)physicsData.modelKey);
        Vec3 dangleDiff = VecHelper.rotate((Vec3)targetPosition.add(0.0, 0.5, 0.0).subtract(position), (double)(-yaw), (Direction.Axis)Direction.Axis.Y);
        float zRot = Mth.wrapDegrees((float)((float)Mth.atan2((double)(-dangleDiff.x), (double)dangleDiff.y) * 57.295776f)) / 2.0f;
        float xRot = Mth.wrapDegrees((float)((float)Mth.atan2((double)dangleDiff.z, (double)dangleDiff.y) * 57.295776f)) / 2.0f;
        zRot = Mth.clamp((float)zRot, (float)-25.0f, (float)25.0f);
        xRot = Mth.clamp((float)xRot, (float)-25.0f, (float)25.0f);
        for (TransformedInstance buf : new TransformedInstance[]{rigBuffer, boxBuffer}) {
            buf.setIdentityTransform();
            buf.translate((Vec3i)this.getVisualPosition());
            buf.translate(offset);
            buf.translate(0.0f, 0.625f, 0.0f);
            buf.rotateYDegrees(yaw);
            buf.rotateZDegrees(zRot);
            buf.rotateXDegrees(xRot);
            if (physicsData.flipped && buf == rigBuffer) {
                buf.rotateYDegrees(180.0f);
            }
            buf.uncenter();
            buf.translate(0.0f, -PackageItem.getHookDistance(box.item) + 0.4375f, 0.0f);
            buf.light(light);
            buf.setChanged();
        }
    }

    private void deleteGuards() {
        for (TransformedInstance guard : this.guards) {
            guard.delete();
        }
        this.guards.clear();
    }

    private void setupGuards() {
        this.deleteGuards();
        Instancer wheelInstancer = this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.chunkDiffuse(AllPartialModels.CHAIN_CONVEYOR_WHEEL));
        Instancer guardInstancer = this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.chunkDiffuse(AllPartialModels.CHAIN_CONVEYOR_GUARD));
        TransformedInstance wheel = (TransformedInstance)wheelInstancer.createInstance();
        ((TransformedInstance)wheel.translate((Vec3i)this.getVisualPosition())).light(this.rotatingModel.light).setChanged();
        this.guards.add(wheel);
        for (BlockPos blockPos : ((ChainConveyorBlockEntity)this.blockEntity).connections) {
            ChainConveyorBlockEntity.ConnectionStats stats = ((ChainConveyorBlockEntity)this.blockEntity).connectionStats.get(blockPos);
            if (stats == null) continue;
            Vec3 diff = stats.end().subtract(stats.start());
            double yaw = 57.2957763671875 * Mth.atan2((double)diff.x, (double)diff.z);
            TransformedInstance guard = (TransformedInstance)guardInstancer.createInstance();
            ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)guard.translate((Vec3i)this.getVisualPosition())).center()).rotateYDegrees((float)yaw)).uncenter()).light(this.rotatingModel.light).setChanged();
            this.guards.add(guard);
        }
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        for (TransformedInstance guard : this.guards) {
            this.relight(new FlatLit[]{guard});
        }
    }

    @Override
    protected void _delete() {
        super._delete();
        this.deleteGuards();
        this.boxes.delete();
        this.rigging.delete();
    }
}
