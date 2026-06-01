package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.client.ClientPhysicsTrainRegistry;
import com.simibubi.create.content.trains.bogey.BogeyVisual;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionVisual;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hides the <b>travelling, spinning bogey</b> of a physics train's ghost carriage — the one that
 * followed the train with ~0.5s lag.
 *
 * <p>This bogey is NOT a contraption block-entity child (Create's bogeys set
 * {@code captureBlockEntityForTrain() == false}, so they never enter
 * {@code ClientContraption.renderedBlockEntityView} and never become {@code setupChildren} visuals —
 * which is why clearing children did nothing). Instead, Create's <b>carriage-specific</b> Flywheel
 * visual, {@link CarriageContraptionVisual}, keeps its own {@code BogeyVisual[] visuals} that it
 * creates and animates along the track every frame inside {@code animate(float)} (called from
 * {@code beginFrame}). That animated visual interpolates on the carriage entity's phase, out of sync
 * with the Sable body, hence the lag.
 *
 * <p>For physics trains we hide each bogey visual and cancel {@code animate}, so Create draws no
 * travelling bogey at all — the only bogey shown is the one rigidly carried by the Sable sub-level.
 * We identify the train via the visual's own {@code contraption} field (reliable, like
 * {@code ClientContraptionRenderMixin}), not the inherited Flywheel {@code entity} field.
 */
@Mixin(CarriageContraptionVisual.class)
public abstract class CarriageBogeyVisualMixin {

    @Shadow @Final private CarriageContraption contraption;
    @Shadow private int numBogeys;
    @Shadow @Final private BogeyVisual[] visuals;

    @Inject(method = "animate", at = @At("HEAD"), cancellable = true, remap = false)
    private void loconautics$hidePhysicsTrainBogeys(float partialTick, CallbackInfo ci) {
        if (!(contraption.entity instanceof CarriageContraptionEntity cce) || cce.trainId == null) {
            return;
        }
        if (!ClientPhysicsTrainRegistry.isPhysicsTrain(cce.trainId)) {
            return;
        }
        for (int i = 0; i < numBogeys; i++) {
            if (visuals[i] != null) {
                visuals[i].hide();
            }
        }
        ci.cancel();
    }
}
