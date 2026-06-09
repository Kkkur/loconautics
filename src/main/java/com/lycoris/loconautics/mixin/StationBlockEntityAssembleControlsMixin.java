package com.lycoris.loconautics.mixin;

import com.lycoris.loconautics.server.assembly.PhysicsAssemblyContext;
import com.simibubi.create.content.trains.station.StationBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.UUID;

/**
 * Skips the "at least one forward controls block required" check during physics train assembly.
 *
 * <p>In {@link StationBlockEntity#assemble(UUID)}, after all carriages are assembled, Create checks
 * {@code atLeastOneForwardControls} and calls {@code exception(...)} with "train_assembly.no_controls"
 * if false. Physics trains are driven by Sable, not a player at a controls block, so this
 * requirement is meaningless for them.
 *
 * <p>Strategy: {@code @ModifyVariable} captures {@code atLeastOneForwardControls} at TAIL
 * (the point just before it is read for the final check). When a physics assembly is pending
 * ({@link PhysicsAssemblyContext#isPending()}), we return {@code true} so the guard never fires.
 *
 * <p>The exception() call for "no_controls" is the 7th call (ordinal 6, 0-indexed) in assemble().
 * Order in source:
 *   0 - frontmost_bogey_at_station (before loop)
 *   1 - bogeys_too_close          (in loop)
 *   2 - nothing_attached          (in loop, catch block)
 *   3 - not_connected_in_order    (in loop)
 *   4 - single_bogey_carriage     (in loop)
 *   5 - no_bogeys                 (after loop)
 *   6 - no_controls               (after loop) ← target
 */

// TODO: Skip the "forward controls required" check during physics train assembly.

@Mixin(StationBlockEntity.class)
public class StationBlockEntityAssembleControlsMixin {

    @ModifyVariable(
            method = "assemble(Ljava/util/UUID;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/trains/station/StationBlockEntity;exception(Lcom/simibubi/create/content/contraptions/AssemblyException;I)V",
                    ordinal = 6,
                    shift = At.Shift.BEFORE
            ),
            name = "atLeastOneForwardControls"
    )
    private boolean loconautics$skipControlsCheckForPhysicsTrain(boolean atLeastOneForwardControls) {
        try {
            if (PhysicsAssemblyContext.isPending()) {
                return true;
            }
        } catch (Throwable ignored) {
            // Never let our check break Create's assembly — fall back to the real value.
        }
        return atLeastOneForwardControls;
    }
}