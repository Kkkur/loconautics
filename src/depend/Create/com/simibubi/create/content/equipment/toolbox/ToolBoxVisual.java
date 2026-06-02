/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.instance.Instancer
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.equipment.toolbox;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
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
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.Property;

public class ToolBoxVisual
extends AbstractBlockEntityVisual<ToolboxBlockEntity>
implements SimpleDynamicVisual {
    private final Direction facing;
    private final TransformedInstance lid;
    private final TransformedInstance[] drawers;
    private float lastLidAngle = Float.NaN;
    private float lastDrawerOffset = Float.NaN;

    public ToolBoxVisual(VisualizationContext context, ToolboxBlockEntity blockEntity, float partialTick) {
        super(context, (BlockEntity)blockEntity, partialTick);
        this.facing = ((Direction)this.blockState.getValue((Property)ToolboxBlock.FACING)).getOpposite();
        Instancer drawerModel = this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.TOOLBOX_DRAWER));
        this.drawers = new TransformedInstance[]{(TransformedInstance)drawerModel.createInstance(), (TransformedInstance)drawerModel.createInstance()};
        this.lid = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.TOOLBOX_LIDS.get(blockEntity.getColor()))).createInstance();
        this.animate(partialTick);
    }

    protected void _delete() {
        this.lid.delete();
        for (TransformedInstance drawer : this.drawers) {
            drawer.delete();
        }
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.animate(ctx.partialTick());
    }

    private void animate(float partialTicks) {
        float lidAngle = ((ToolboxBlockEntity)this.blockEntity).lid.getValue(partialTicks);
        float drawerOffset = ((ToolboxBlockEntity)this.blockEntity).drawers.getValue(partialTicks);
        if (lidAngle != this.lastLidAngle) {
            ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.lid.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).center()).rotateYDegrees(-this.facing.toYRot())).uncenter()).translate(0.0f, 0.375f, 0.75f).rotateXDegrees(135.0f * lidAngle)).translateBack(0.0f, 0.375f, 0.75f)).setChanged();
        }
        if (drawerOffset != this.lastDrawerOffset) {
            for (int offset : Iterate.zeroAndOne) {
                ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.drawers[offset].setIdentityTransform().translate((Vec3i)this.getVisualPosition())).center()).rotateYDegrees(-this.facing.toYRot())).uncenter()).translate(0.0f, (float)(offset * 1) / 8.0f, -drawerOffset * 0.175f * (float)(2 - offset)).setChanged();
            }
        }
        this.lastLidAngle = lidAngle;
        this.lastDrawerOffset = drawerOffset;
    }

    public void updateLight(float partialTick) {
        this.relight((FlatLit[])this.drawers);
        this.relight(new FlatLit[]{this.lid});
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept((Instance)this.lid);
        for (TransformedInstance drawer : this.drawers) {
            consumer.accept((Instance)drawer);
        }
    }
}
