package com.lycoris.loconautics.mixin.client;

import java.util.List;

import com.lycoris.loconautics.client.ClientPhysicsTrainRegistry;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.render.ClientContraption;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;

import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visual.Visual;
import dev.engine_room.flywheel.lib.task.PlanMap;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Drops the Flywheel <b>child block-entity visuals</b> of a physics train's ghost carriage (e.g. a
 * chest or other functional block entity), so they aren't drawn twice — the Sable sub-level already
 * renders those block entities rigidly with the body.
 *
 * <p>Note: the carriage's <b>bogeys</b> are NOT child visuals (Create's bogeys set
 * {@code captureBlockEntityForTrain() == false}, so they never enter the contraption's
 * {@code renderedBlockEntityView}); the travelling bogey is hidden separately by
 * {@link CarriageBogeyVisualMixin}. This hook handles the remaining BE children.
 *
 * <p>It targets {@code setupChildren(Contraption, ClientContraption, float)}, identifying the physics
 * train via the {@link Contraption} method argument ({@code contraption.entity}) — the same reliable
 * way {@link ClientContraptionRenderMixin} does. For a physics train we delete and clear any existing
 * children (covering the brief window before the sync packet arrives), clear the plan maps, advance
 * {@code lastVersionChildren} so Flywheel doesn't rebuild every frame, and cancel.
 */
@Mixin(ContraptionVisual.class)
public abstract class ContraptionVisualChildrenMixin {

    @Shadow @Final protected List<BlockEntityVisual<?>> children;
    @Shadow @Final protected PlanMap<?, ?> dynamicVisuals;
    @Shadow @Final protected PlanMap<?, ?> tickableVisuals;
    @Shadow protected int lastVersionChildren;

    @Inject(method = "setupChildren", at = @At("HEAD"), cancellable = true, remap = false)
    private void loconautics$suppressPhysicsTrainChildren(Contraption contraption, ClientContraption client,
            float partialTick, CallbackInfo ci) {
        if (!(contraption.entity instanceof CarriageContraptionEntity cce) || cce.trainId == null) {
            return;
        }
        if (!ClientPhysicsTrainRegistry.isPhysicsTrain(cce.trainId)) {
            return;
        }

        children.forEach(Visual::delete);
        children.clear();
        dynamicVisuals.clear();
        tickableVisuals.clear();
        lastVersionChildren = client.childrenVersion();
        ci.cancel();
    }
}
