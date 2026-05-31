package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.client.ClientPhysicsTrainRegistry;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.render.ClientContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.BitSet;
import java.util.List;

@Mixin(ClientContraption.class)
public class ClientContraptionRenderMixin {

    @Shadow @Final private Contraption contraption;

    private boolean loconautics$isPhysicsTrain() {
        if (!(contraption.entity instanceof CarriageContraptionEntity cce) || cce.trainId == null) return false;
        return ClientPhysicsTrainRegistry.isPhysicsTrain(cce.trainId);
    }

    @Inject(method = "getRenderedBlocks", at = @At("RETURN"), remap = false, cancellable = true)
    private void loconautics$emptyRenderedBlocks(CallbackInfoReturnable<ClientContraption.RenderedBlocks> cir) {
        if (loconautics$isPhysicsTrain()) {
            cir.setReturnValue(new ClientContraption.RenderedBlocks(pos -> Blocks.AIR.defaultBlockState(), List.of()));
        }
    }

    @Inject(method = "setupRenderLevelAndRenderedBlockEntities", at = @At("HEAD"), remap = false, cancellable = true)
    private void loconautics$cancelSetup(CallbackInfo ci) {
        if (loconautics$isPhysicsTrain()) ci.cancel();
    }

    @Inject(method = "getAndAdjustShouldRenderBlockEntities", at = @At("RETURN"), remap = false, cancellable = true)
    private void loconautics$emptyBlockEntities(CallbackInfoReturnable<BitSet> cir) {
        if (loconautics$isPhysicsTrain()) {
            cir.setReturnValue(new BitSet(cir.getReturnValue().length()));
        }
    }
}