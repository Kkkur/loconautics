package com.lycoris.loconautics.mixin;

import com.lycoris.loconautics.client.ClientPhysicsTrainRegistry;
import com.lycoris.loconautics.server.PhysicsTrainRegistry;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * Keeps {@code validForRender = false} after every tickContraption for physics trains.
 * We inject at RETURN (not HEAD+cancel) so that sounds, passengers, and controls
 * all continue to function normally — only the render flag is forced off.
 */
@Mixin(CarriageContraptionEntity.class)
public class CarriageContraptionEntityMixin {

    @Shadow(remap = false)
    public UUID trainId;

    @Shadow(remap = false)
    public boolean validForRender;

    @Inject(method = "tickContraption", at = @At("RETURN"), remap = false)
    private void loconautics$forceInvisibleAfterTick(CallbackInfo ci) {
        CarriageContraptionEntity self = (CarriageContraptionEntity) (Object) this;
        if (trainId == null) return;

        if (self.level().isClientSide()) {
            if (ClientPhysicsTrainRegistry.isPhysicsTrain(trainId)) {
                validForRender = false;
            }
        } else {
            ServerLevel serverLevel = (ServerLevel) self.level();
            MinecraftServer server = serverLevel.getServer();
            if (PhysicsTrainRegistry.get(server).isPhysicsTrain(trainId)) {
                // Server-side: nothing to suppress visually, but we could add
                // future hooks here (e.g. suppress server-side collision sounds).
            }
        }
    }
}