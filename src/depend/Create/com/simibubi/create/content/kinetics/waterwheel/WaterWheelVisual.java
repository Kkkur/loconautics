/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.model.Model
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder
 *  dev.engine_room.flywheel.lib.util.RendererReloadCache
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.waterwheel;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlockEntity;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelRenderer;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder;
import dev.engine_room.flywheel.lib.util.RendererReloadCache;
import java.util.function.Consumer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

public class WaterWheelVisual<T extends WaterWheelBlockEntity>
extends KineticBlockEntityVisual<T> {
    private static final RendererReloadCache<ModelKey, Model> MODEL_CACHE = new RendererReloadCache(WaterWheelVisual::createModel);
    protected final boolean large;
    protected BlockState lastMaterial;
    protected RotatingInstance rotatingModel;

    public WaterWheelVisual(VisualizationContext context, T blockEntity, boolean large, float partialTick) {
        super(context, blockEntity, partialTick);
        this.large = large;
        this.setupInstance();
    }

    public static <T extends WaterWheelBlockEntity> WaterWheelVisual<T> standard(VisualizationContext context, T blockEntity, float partialTick) {
        return new WaterWheelVisual<T>(context, blockEntity, false, partialTick);
    }

    public static <T extends WaterWheelBlockEntity> WaterWheelVisual<T> large(VisualizationContext context, T blockEntity, float partialTick) {
        return new WaterWheelVisual<T>(context, blockEntity, true, partialTick);
    }

    private void setupInstance() {
        this.lastMaterial = ((WaterWheelBlockEntity)this.blockEntity).material;
        this.rotatingModel = (RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, (Model)MODEL_CACHE.get((Object)new ModelKey(WaterWheelRenderer.Variant.of(this.large, this.blockState), ((WaterWheelBlockEntity)this.blockEntity).material))).createInstance();
        this.rotatingModel.setup((KineticBlockEntity)this.blockEntity).setPosition((Vec3i)this.getVisualPosition()).rotateToFace(this.rotationAxis()).setChanged();
    }

    public void update(float pt) {
        if (this.lastMaterial != ((WaterWheelBlockEntity)this.blockEntity).material) {
            this.rotatingModel.delete();
            this.setupInstance();
        } else {
            this.rotatingModel.setup((KineticBlockEntity)this.blockEntity).setChanged();
        }
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

    private static Model createModel(ModelKey key) {
        BakedModel model = WaterWheelRenderer.generateModel(key.variant(), key.material());
        return new BakedModelBuilder(model).build();
    }

    public record ModelKey(WaterWheelRenderer.Variant variant, BlockState material) {
    }
}
