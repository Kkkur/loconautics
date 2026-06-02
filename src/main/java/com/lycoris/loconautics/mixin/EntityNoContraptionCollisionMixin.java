package com.lycoris.loconautics.mixin;

import com.lycoris.loconautics.client.ClientPhysicsTrainRegistry;
import com.lycoris.loconautics.server.PhysicsTrainRegistry;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes a physics train's ghost {@link CarriageContraptionEntity} completely non-collidable, so the
 * ONLY thing the player collides with is the Sable sub-level physics body (which sits exactly at the
 * visible position — verified: {@code logicalPose == renderPose}).
 *
 * <p>Create's entity-push collision is already cancelled in {@code ContraptionColliderSkipMixin}
 * ({@code ContraptionCollider.collideEntities}); this is the belt-and-suspenders that also forces the
 * vanilla entity-collision predicate {@code canBeCollidedWith()} to {@code false} for physics carriages,
 * so nothing about the Create contraption can block or shove the player.
 */
@Mixin(Entity.class)
public class EntityNoContraptionCollisionMixin {

    @Inject(method = "canBeCollidedWith", at = @At("HEAD"), cancellable = true)
    private void loconautics$physicsTrainNonCollidable(CallbackInfoReturnable<Boolean> cir) {
        if (!(((Object) this) instanceof CarriageContraptionEntity cce) || cce.trainId == null) {
            return;
        }
        boolean physics = cce.level().isClientSide()
                ? ClientPhysicsTrainRegistry.isPhysicsTrain(cce.trainId)
                : PhysicsTrainRegistry.get(((ServerLevel) cce.level()).getServer()).isPhysicsTrain(cce.trainId);
        if (physics) {
            cir.setReturnValue(false);
        }
    }
}
