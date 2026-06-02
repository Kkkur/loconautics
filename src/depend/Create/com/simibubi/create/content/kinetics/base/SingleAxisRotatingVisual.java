/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.model.Model
 *  dev.engine_room.flywheel.api.visual.TickableVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.SimpleTickableVisual
 *  dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer$Factory
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.equipment.armor.BacktankRenderer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public class SingleAxisRotatingVisual<T extends KineticBlockEntity>
extends KineticBlockEntityVisual<T>
implements SimpleTickableVisual {
    public static boolean rainbowMode = false;
    protected final RotatingInstance rotatingModel;

    public SingleAxisRotatingVisual(VisualizationContext context, T blockEntity, float partialTick, Model model) {
        this(context, blockEntity, partialTick, Direction.UP, model);
    }

    public SingleAxisRotatingVisual(VisualizationContext context, T blockEntity, float partialTick, Direction from, Model model) {
        super(context, blockEntity, partialTick);
        this.rotatingModel = ((RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, model).createInstance()).rotateToFace(from, this.rotationAxis()).setup((KineticBlockEntity)blockEntity).setPosition((Vec3i)this.getVisualPosition());
        this.rotatingModel.setChanged();
    }

    public static <T extends KineticBlockEntity> SimpleBlockEntityVisualizer.Factory<T> of(PartialModel partial) {
        return (context, blockEntity, partialTick) -> new SingleAxisRotatingVisual<KineticBlockEntity>(context, (KineticBlockEntity)blockEntity, partialTick, Models.partial((PartialModel)partial));
    }

    public static <T extends KineticBlockEntity> SimpleBlockEntityVisualizer.Factory<T> ofZ(PartialModel partial) {
        return (context, blockEntity, partialTick) -> new SingleAxisRotatingVisual<KineticBlockEntity>(context, (KineticBlockEntity)blockEntity, partialTick, Direction.SOUTH, Models.partial((PartialModel)partial));
    }

    public static <T extends KineticBlockEntity> SingleAxisRotatingVisual<T> shaft(VisualizationContext context, T blockEntity, float partialTick) {
        return new SingleAxisRotatingVisual<T>(context, blockEntity, partialTick, Models.partial((PartialModel)AllPartialModels.SHAFT));
    }

    public static <T extends KineticBlockEntity> SingleAxisRotatingVisual<T> backtank(VisualizationContext context, T blockEntity, float partialTick) {
        Model model = Models.partial((PartialModel)BacktankRenderer.getShaftModel(blockEntity.getBlockState()));
        return new SingleAxisRotatingVisual<T>(context, blockEntity, partialTick, model);
    }

    public void update(float pt) {
        this.rotatingModel.setup((KineticBlockEntity)this.blockEntity).setChanged();
    }

    public void tick(TickableVisual.Context context) {
        SingleAxisRotatingVisual.applyOverstressEffect((KineticBlockEntity)this.blockEntity, this.rotatingModel);
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.rotatingModel});
    }

    protected void _delete() {
        this.rotatingModel.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept((Instance)this.rotatingModel);
    }
}
