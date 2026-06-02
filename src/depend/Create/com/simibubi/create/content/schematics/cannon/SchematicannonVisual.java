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
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.content.schematics.cannon;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.simibubi.create.content.schematics.cannon.SchematicannonRenderer;
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
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SchematicannonVisual
extends AbstractBlockEntityVisual<SchematicannonBlockEntity>
implements SimpleDynamicVisual {
    private final TransformedInstance connector;
    private final TransformedInstance pipe;
    private double lastYaw = Double.NaN;
    private double lastPitch = Double.NaN;
    private double lastRecoil = Double.NaN;

    public SchematicannonVisual(VisualizationContext context, SchematicannonBlockEntity blockEntity, float partialTick) {
        super(context, (BlockEntity)blockEntity, partialTick);
        this.connector = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.SCHEMATICANNON_CONNECTOR)).createInstance();
        this.pipe = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.SCHEMATICANNON_PIPE)).createInstance();
        this.animate(partialTick);
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.animate(ctx.partialTick());
    }

    private void animate(float partialTicks) {
        double[] cannonAngles = SchematicannonRenderer.getCannonAngles((SchematicannonBlockEntity)this.blockEntity, this.pos, partialTicks);
        double yaw = cannonAngles[0];
        double pitch = cannonAngles[1];
        double recoil = SchematicannonRenderer.getRecoil((SchematicannonBlockEntity)this.blockEntity, partialTicks);
        if (yaw != this.lastYaw) {
            ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.connector.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).center()).rotate((float)((yaw + 90.0) / 180.0 * Math.PI), Direction.UP)).uncenter()).setChanged();
        }
        if (pitch != this.lastPitch || recoil != this.lastRecoil) {
            ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.pipe.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).translate(0.5f, 0.9375f, 0.5f).rotate((float)((yaw + 90.0) / 180.0 * Math.PI), Direction.UP)).rotate((float)(pitch / 180.0 * Math.PI), Direction.SOUTH)).translateBack(0.5f, 0.9375f, 0.5f)).translate(0.0, -recoil / 100.0, 0.0)).setChanged();
        }
        this.lastYaw = yaw;
        this.lastPitch = pitch;
        this.lastRecoil = recoil;
    }

    protected void _delete() {
        this.connector.delete();
        this.pipe.delete();
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.connector, this.pipe});
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept((Instance)this.connector);
        consumer.accept((Instance)this.pipe);
    }
}
