/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package com.simibubi.create.content.logistics.funnel;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.FlapStuffs;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class FunnelVisual
extends AbstractBlockEntityVisual<FunnelBlockEntity>
implements SimpleDynamicVisual {
    private final FlapStuffs.Visual flaps;

    public FunnelVisual(VisualizationContext context, FunnelBlockEntity blockEntity, float partialTick) {
        super(context, (BlockEntity)blockEntity, partialTick);
        if (!blockEntity.hasFlap()) {
            this.flaps = null;
            return;
        }
        Direction funnelFacing = FunnelBlock.getFunnelFacing(this.blockState);
        PartialModel flapPartial = this.blockState.getBlock() instanceof FunnelBlock ? AllPartialModels.FUNNEL_FLAP : AllPartialModels.BELT_FUNNEL_FLAP;
        Matrix4f commonTransform = FlapStuffs.commonTransform(this.getVisualPosition(), funnelFacing, -blockEntity.getFlapOffset());
        this.flaps = new FlapStuffs.Visual(this.instancerProvider(), (Matrix4fc)commonTransform, FlapStuffs.FUNNEL_PIVOT, Models.partial((PartialModel)flapPartial));
        this.flaps.update(blockEntity.flap.getValue(partialTick));
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        if (this.flaps == null) {
            return;
        }
        this.flaps.update(((FunnelBlockEntity)this.blockEntity).flap.getValue(ctx.partialTick()));
    }

    public void updateLight(float partialTick) {
        if (this.flaps != null) {
            this.flaps.updateLight(this.computePackedLight());
        }
    }

    protected void _delete() {
        if (this.flaps == null) {
            return;
        }
        this.flaps.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        if (this.flaps == null) {
            return;
        }
        this.flaps.collectCrumblingInstances(consumer);
    }
}
