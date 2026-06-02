package com.lycoris.loconautics.mixin;

import com.lycoris.loconautics.client.ClientPhysicsTrainRegistry;
import com.lycoris.loconautics.server.PhysicsTrainRegistry;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Disables Create's <b>block-level contraption collision</b> for physics-train carriages.
 *
 * <p>{@code ContraptionCollider.collideEntities} pushes entities (the player) out of the contraption's
 * moving block shapes every tick, using Create's contraption rotation. For a physics train the visible,
 * physical body IS the Sable sub-level, which has its own collision. The invisible Create contraption's
 * collision shapes do NOT follow the Sable body when the train turns/rotates, so the player kept
 * bumping into a ghost hitbox offset from what they see. (The existing {@code ContraptionColliderMixin}
 * only suppresses vanilla entity-push {@code canCollideWith} and storage access — not this OBB-based
 * block collision.)
 *
 * <p>Cancelling {@code collideEntities} at HEAD for physics trains leaves collision entirely to the
 * Sable sub-level body. The method early-returns cleanly on its own when the contraption/bbox is null,
 * so skipping it outright is safe.
 */
@Mixin(ContraptionCollider.class)
public class ContraptionColliderSkipMixin {

    @Inject(method = "collideEntities", at = @At("HEAD"), cancellable = true, remap = false)
    private static void loconautics$skipPhysicsTrainCollision(AbstractContraptionEntity entity, CallbackInfo ci) {
        if (!(entity instanceof CarriageContraptionEntity cce) || cce.trainId == null) {
            return;
        }
        boolean physics = cce.level().isClientSide()
                ? ClientPhysicsTrainRegistry.isPhysicsTrain(cce.trainId)
                : PhysicsTrainRegistry.get(((ServerLevel) cce.level()).getServer()).isPhysicsTrain(cce.trainId);
        if (physics) {
            ci.cancel();
        }
    }
}
