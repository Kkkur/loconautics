/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.TickableVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleTickableVisual
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.content.redstone.diodes;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.redstone.diodes.BrassDiodeBlockEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import java.util.function.Consumer;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BrassDiodeVisual
extends AbstractBlockEntityVisual<BrassDiodeBlockEntity>
implements SimpleTickableVisual {
    protected final TransformedInstance indicator = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.FLEXPEATER_INDICATOR)).createInstance();
    protected int previousState;

    public BrassDiodeVisual(VisualizationContext context, BrassDiodeBlockEntity blockEntity, float partialTick) {
        super(context, (BlockEntity)blockEntity, partialTick);
        ((TransformedInstance)this.indicator.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).colorRgb(this.getColor()).setChanged();
        this.previousState = blockEntity.state;
    }

    public void tick(TickableVisual.Context context) {
        if (this.previousState == ((BrassDiodeBlockEntity)this.blockEntity).state) {
            return;
        }
        this.indicator.colorRgb(this.getColor());
        this.indicator.setChanged();
        this.previousState = ((BrassDiodeBlockEntity)this.blockEntity).state;
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.indicator});
    }

    protected void _delete() {
        this.indicator.delete();
    }

    protected int getColor() {
        return Color.mixColors((int)2884352, (int)0xCD0000, (float)((BrassDiodeBlockEntity)this.blockEntity).getProgress());
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept((Instance)this.indicator);
    }
}
