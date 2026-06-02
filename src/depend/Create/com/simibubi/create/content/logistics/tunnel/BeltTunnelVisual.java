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
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package com.simibubi.create.content.logistics.tunnel;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.FlapStuffs;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class BeltTunnelVisual
extends AbstractBlockEntityVisual<BeltTunnelBlockEntity>
implements SimpleDynamicVisual {
    private final Map<Direction, FlapStuffs.Visual> tunnelFlaps = new EnumMap<Direction, FlapStuffs.Visual>(Direction.class);
    private int light;

    public BeltTunnelVisual(VisualizationContext context, BeltTunnelBlockEntity blockEntity, float partialTick) {
        super(context, (BlockEntity)blockEntity, partialTick);
        this.createFlaps();
        this.updateFlaps(partialTick);
    }

    private void createFlaps() {
        ((BeltTunnelBlockEntity)this.blockEntity).flaps.forEach((direction, flapValue) -> {
            Matrix4f commonTransform = FlapStuffs.commonTransform(this.visualPos, direction, 0.0f);
            FlapStuffs.Visual flapSide = new FlapStuffs.Visual(this.instancerProvider(), (Matrix4fc)commonTransform, FlapStuffs.TUNNEL_PIVOT, Models.partial((PartialModel)AllPartialModels.BELT_TUNNEL_FLAP));
            flapSide.updateLight(this.light);
            this.tunnelFlaps.put((Direction)direction, flapSide);
        });
    }

    public void update(float partialTick) {
        super.update(partialTick);
        this._delete();
        this.createFlaps();
        this.updateFlaps(partialTick);
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.updateFlaps(ctx.partialTick());
    }

    private void updateFlaps(float partialTicks) {
        this.tunnelFlaps.forEach((direction, keys) -> {
            LerpedFloat lerpedFloat = ((BeltTunnelBlockEntity)this.blockEntity).flaps.get(direction);
            if (lerpedFloat == null) {
                return;
            }
            keys.update(lerpedFloat.getValue(partialTicks));
        });
    }

    public void updateLight(float partialTick) {
        this.light = this.computePackedLight();
        for (FlapStuffs.Visual value : this.tunnelFlaps.values()) {
            value.updateLight(this.light);
        }
    }

    protected void _delete() {
        this.tunnelFlaps.values().forEach(FlapStuffs.Visual::delete);
        this.tunnelFlaps.clear();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        for (FlapStuffs.Visual value : this.tunnelFlaps.values()) {
            value.collectCrumblingInstances(consumer);
        }
    }
}
