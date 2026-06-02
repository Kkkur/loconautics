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
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.content.kinetics.steamEngine;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlockEntity;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlockEntity;
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
import java.util.Objects;
import java.util.function.Consumer;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SteamEngineVisual
extends AbstractBlockEntityVisual<SteamEngineBlockEntity>
implements SimpleDynamicVisual {
    protected final TransformedInstance piston;
    protected final TransformedInstance linkage;
    protected final TransformedInstance connector;
    private Float lastAngle = Float.valueOf(Float.NaN);
    private Direction.Axis lastAxis = null;

    public SteamEngineVisual(VisualizationContext context, SteamEngineBlockEntity blockEntity, float partialTick) {
        super(context, (BlockEntity)blockEntity, partialTick);
        this.piston = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.ENGINE_PISTON)).createInstance();
        this.linkage = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.ENGINE_LINKAGE)).createInstance();
        this.connector = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.ENGINE_CONNECTOR)).createInstance();
        this.animate();
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.animate();
    }

    private void animate() {
        Float angle = ((SteamEngineBlockEntity)this.blockEntity).getTargetAngle();
        Direction.Axis axis = Direction.Axis.Y;
        PoweredShaftBlockEntity shaft = ((SteamEngineBlockEntity)this.blockEntity).getShaft();
        if (shaft != null) {
            axis = KineticBlockEntityRenderer.getRotationAxisOf(shaft);
        }
        if (Objects.equals(angle, this.lastAngle) && this.lastAxis == axis) {
            return;
        }
        this.lastAngle = angle;
        this.lastAxis = axis;
        if (angle == null) {
            this.piston.setVisible(false);
            this.linkage.setVisible(false);
            this.connector.setVisible(false);
            return;
        }
        this.piston.setVisible(true);
        this.linkage.setVisible(true);
        this.connector.setVisible(true);
        Direction facing = SteamEngineBlock.getFacing(this.blockState);
        Direction.Axis facingAxis = facing.getAxis();
        boolean roll90 = facingAxis.isHorizontal() && axis == Direction.Axis.Y || facingAxis.isVertical() && axis == Direction.Axis.Z;
        float piston = 0.375f * Mth.sin((float)angle.floatValue()) - Mth.sqrt((float)(Mth.square((float)0.875f) - Mth.square((float)0.375f) * Mth.square((float)Mth.cos((float)angle.floatValue()))));
        float distance = Mth.sqrt((float)Mth.square((float)(piston - 0.375f * Mth.sin((float)angle.floatValue()))));
        float angle2 = (float)Math.acos(distance / 0.875f) * (Mth.cos((float)angle.floatValue()) >= 0.0f ? 1.0f : -1.0f);
        this.transformed(this.piston, facing, roll90).translate(0.0f, piston + 1.25f, 0.0f).setChanged();
        ((TransformedInstance)((TransformedInstance)this.transformed(this.linkage, facing, roll90).center()).translate(0.0f, 1.0f, 0.0f).uncenter()).translate(0.0f, piston + 1.25f, 0.0f).translate(0.0f, 0.25f, 0.5f).rotateX(angle2).translate(0.0f, -0.25f, -0.5f).setChanged();
        ((TransformedInstance)((TransformedInstance)this.transformed(this.connector, facing, roll90).translate(0.0f, 2.0f, 0.0f).center()).rotateX(-(angle.floatValue() + 1.5707964f)).uncenter()).setChanged();
    }

    protected TransformedInstance transformed(TransformedInstance modelData, Direction facing, boolean roll90) {
        return (TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)modelData.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing))).rotateXDegrees(AngleHelper.verticalAngle((Direction)facing) + 90.0f)).rotateYDegrees(roll90 ? -90.0f : 0.0f)).uncenter();
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.piston, this.linkage, this.connector});
    }

    protected void _delete() {
        this.piston.delete();
        this.linkage.delete();
        this.connector.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept((Instance)this.piston);
        consumer.accept((Instance)this.linkage);
        consumer.accept((Instance)this.connector);
    }
}
