package com.lycoris.loconautics.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lycoris.loconautics.server.assembly.PhysicsAssemblyContext;
import com.simibubi.create.content.trains.entity.CarriageContraption;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

/**
 * Skips the "train controls required" check during physics train assembly.
 *
 * <p>Create's {@code CarriageContraption.assemble()} throws an {@link com.simibubi.create.content.contraptions.AssemblyException}
 * when the contraption contains no conductor seat (i.e. no {@code create:controls} block).
 * Physics trains are driven by Sable, not by a player at a controls block, so this
 * requirement is meaningless for them.
 *
 * <p>The check is implemented as {@code if (conductorSeats.isEmpty()) throw ...}.
 * We wrap the {@link Map#isEmpty()} call: when a physics assembly is in progress
 * ({@link PhysicsAssemblyContext#isPending()} is true), we always return {@code false}
 * so the exception is never thrown. In all other cases the original call is forwarded.
 */
@Mixin(CarriageContraption.class)
public class CarriageContraptionAssembleMixin {

    @WrapOperation(
            method = "assemble",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;isEmpty()Z",
                    remap = false
            ),
            remap = true
    )
    private boolean loconautics$skipControlsCheckForPhysicsTrain(Map<?, ?> conductorSeats, Operation<Boolean> original) {
        if (PhysicsAssemblyContext.isPending()) {
            // Physics trains don't need a controls block — skip the requirement.
            return false;
        }
        return original.call(conductorSeats);
    }
}