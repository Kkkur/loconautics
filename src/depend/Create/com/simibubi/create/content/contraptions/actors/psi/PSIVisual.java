/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visual.TickableVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleTickableVisual
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.content.contraptions.actors.psi;

import com.simibubi.create.content.contraptions.actors.psi.PIInstance;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlockEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import java.util.function.Consumer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PSIVisual
extends AbstractBlockEntityVisual<PortableStorageInterfaceBlockEntity>
implements SimpleDynamicVisual,
SimpleTickableVisual {
    private final PIInstance instance;

    public PSIVisual(VisualizationContext visualizationContext, PortableStorageInterfaceBlockEntity blockEntity, float partialTick) {
        super(visualizationContext, (BlockEntity)blockEntity, partialTick);
        this.instance = new PIInstance(visualizationContext.instancerProvider(), this.blockState, this.getVisualPosition(), this.isLit());
        this.instance.beginFrame(blockEntity.getExtensionDistance(partialTick));
    }

    public void tick(TickableVisual.Context ctx) {
        this.instance.tick(this.isLit());
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.instance.beginFrame(((PortableStorageInterfaceBlockEntity)this.blockEntity).getExtensionDistance(ctx.partialTick()));
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.instance.middle, this.instance.top});
    }

    protected void _delete() {
        this.instance.remove();
    }

    private boolean isLit() {
        return ((PortableStorageInterfaceBlockEntity)this.blockEntity).isConnected();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        this.instance.collectCrumblingInstances(consumer);
    }
}
