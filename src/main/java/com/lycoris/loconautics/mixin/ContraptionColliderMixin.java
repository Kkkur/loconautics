package com.lycoris.loconautics.mixin;

import com.lycoris.loconautics.client.ClientPhysicsTrainRegistry;
import com.lycoris.loconautics.server.PhysicsTrainRegistry;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * Disables collision for physics-train carriages by returning false from
 * {@code canCollideWith}. This is the method AbstractContraptionEntity uses to
 * decide whether to push/block other entities — returning false makes the
 * carriage entity non-solid without affecting Sable's own collision.
 */
@Mixin(CarriageContraptionEntity.class)
public class ContraptionColliderMixin {

    @Shadow(remap = false)
    public UUID trainId;

    @Inject(method = "canCollideWith", at = @At("HEAD"), cancellable = true)
    private void loconautics$noCollideForPhysicsTrain(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (trainId == null) return;
        CarriageContraptionEntity self = (CarriageContraptionEntity) (Object) this;

        boolean isPhysics;
        if (self.level().isClientSide()) {
            isPhysics = ClientPhysicsTrainRegistry.isPhysicsTrain(trainId);
        } else {
            isPhysics = PhysicsTrainRegistry.get(((ServerLevel) self.level()).getServer()).isPhysicsTrain(trainId);
        }

        if (isPhysics) {
            cir.setReturnValue(false);
        }
    }
}