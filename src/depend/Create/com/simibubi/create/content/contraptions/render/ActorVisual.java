/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.InstancerProvider
 *  dev.engine_room.flywheel.api.visual.Visual
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.LightLayer
 */
package com.simibubi.create.content.contraptions.render;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.api.visual.Visual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;

public abstract class ActorVisual
implements Visual {
    protected final VisualizationContext visualizationContext;
    protected final InstancerProvider instancerProvider;
    protected final BlockAndTintGetter simulationWorld;
    protected final MovementContext context;
    private boolean deleted;

    public ActorVisual(VisualizationContext visualizationContext, BlockAndTintGetter world, MovementContext context) {
        this.visualizationContext = visualizationContext;
        this.instancerProvider = visualizationContext.instancerProvider();
        this.simulationWorld = world;
        this.context = context;
    }

    public void tick() {
    }

    public void beginFrame() {
    }

    protected int localBlockLight() {
        return this.simulationWorld.getBrightness(LightLayer.BLOCK, this.context.localPos);
    }

    public void update(float partialTick) {
    }

    protected abstract void _delete();

    public final void delete() {
        if (this.deleted) {
            return;
        }
        this._delete();
        this.deleted = true;
    }
}
