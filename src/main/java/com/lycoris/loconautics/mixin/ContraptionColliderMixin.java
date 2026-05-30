package com.lycoris.loconautics.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lycoris.loconautics.client.ClientPhysicsTrainRegistry;
import com.lycoris.loconautics.server.PhysicsTrainRegistry;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * For physics-train carriages:
 *  - suppresses entity-push collision (canCollideWith)
 *  - suppresses chest/inventory access (storage interaction only, not the conductor seat)
 */
@Mixin(AbstractContraptionEntity.class)
public class ContraptionColliderMixin {

    @Inject(method = "canCollideWith", at = @At("HEAD"), cancellable = true)
    private void loconautics$noCollide(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (isPhysicsTrain()) cir.setReturnValue(false);
    }

    /**
     * Blocks chest/inventory/storage GUI access on the phantom carriage entity.
     * Does NOT cancel the whole handlePlayerInteraction, so conductor seats still work.
     */
    @WrapOperation(
            method = "handlePlayerInteraction",
            at = @At(value = "INVOKE",
                    target = "Lcom/simibubi/create/content/contraptions/MountedStorageManager;" +
                            "handlePlayerStorageInteraction(Lcom/simibubi/create/content/contraptions/Contraption;" +
                            "Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;)Z"),
            remap = false
    )
    private boolean loconautics$noStorageAccess(MountedStorageManager storage, Contraption contraption,
                                                Player player, BlockPos pos, Operation<Boolean> original) {
        if (isPhysicsTrain()) return false;
        return original.call(storage, contraption, player, pos);
    }

    private boolean isPhysicsTrain() {
        if (!((Object) this instanceof CarriageContraptionEntity self) || self.trainId == null) return false;
        if (self.level().isClientSide()) {
            return ClientPhysicsTrainRegistry.isPhysicsTrain(self.trainId);
        } else {
            return PhysicsTrainRegistry.get(((ServerLevel) self.level()).getServer()).isPhysicsTrain(self.trainId);
        }
    }
}