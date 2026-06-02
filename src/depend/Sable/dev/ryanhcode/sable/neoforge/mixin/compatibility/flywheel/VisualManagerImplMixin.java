/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.task.Plan
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualManager
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.impl.visualization.VisualManagerImpl
 *  dev.engine_room.flywheel.impl.visualization.storage.Storage
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.flywheel;

import dev.engine_room.flywheel.api.task.Plan;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualManager;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.impl.visualization.VisualManagerImpl;
import dev.engine_room.flywheel.impl.visualization.storage.Storage;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.flywheel.BlockEntityStorageExtension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={VisualManagerImpl.class}, remap=false)
public abstract class VisualManagerImplMixin<T, S extends Storage<T>>
implements VisualManager<T> {
    @Shadow
    @Final
    private S storage;

    @Inject(method={"framePlan"}, at={@At(value="HEAD")})
    private void sable$preFramePlan(VisualizationContext visualizationContext, CallbackInfoReturnable<Plan<DynamicVisual.Context>> cir) {
        S s = this.storage;
        if (s instanceof BlockEntityStorageExtension) {
            BlockEntityStorageExtension extension = (BlockEntityStorageExtension)s;
            extension.sable$setPlanVisualizationContext(visualizationContext);
        }
    }
}
