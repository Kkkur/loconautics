package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.client.ClientPhysicsTrainRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.render.ClientContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.BitSet;
import java.util.List;
import java.util.UUID;

/**
 * Suppresses ALL client-side rendering for physics-train carriages.
 *
 * Three systems need to be blocked:
 *
 * 1. {@code OrientedContraptionEntity.applyLocalTransforms} — the PoseStack transform applied
 *    before Create draws every block in the contraption. Cancelling this prevents any geometry
 *    from being submitted to the vanilla/sodium renderer.
 *
 * 2. {@code ClientContraption.getRenderedBlocks} — returns the block list used by the
 *    batched/Flywheel renderer. Replacing with an empty result kills instanced block rendering.
 *
 * 3. {@code ClientContraption.getAndAdjustShouldRenderBlockEntities} — controls which block
 *    entities (chests, tanks, etc.) are drawn. Returning an empty BitSet hides them all.
 */
public class CarriageContraptionEntityClientMixin {

    // ---- Hook 1: PoseStack transform (targets OrientedContraptionEntity) ----

    @Mixin(value = OrientedContraptionEntity.class, remap = false)
    public static class PoseStackHook {
        @Inject(method = "applyLocalTransforms", at = @At("HEAD"), cancellable = true)
        private void loconautics$cancelTransform(PoseStack poseStack, float partialTicks, CallbackInfo ci) {
            Object self = this;
            if (!(self instanceof CarriageContraptionEntity cce)) return;
            if (cce.trainId != null && ClientPhysicsTrainRegistry.isPhysicsTrain(cce.trainId)) {
                ci.cancel();
            }
        }
    }

    // ---- Hooks 2 & 3: Flywheel / batched renderer (targets ClientContraption) ----

    @Mixin(value = ClientContraption.class, remap = false)
    public static abstract class ClientContraptionHook {

        @Shadow
        private Contraption contraption;

        /** Returns empty block list → no instanced/batched block geometry rendered. */
        @Inject(method = "getRenderedBlocks", at = @At("RETURN"), cancellable = true)
        private void loconautics$emptyRenderedBlocks(CallbackInfoReturnable<ClientContraption.RenderedBlocks> cir) {
            if (!isPhysicsTrain()) return;
            cir.setReturnValue(new ClientContraption.RenderedBlocks(
                    pos -> Blocks.AIR.defaultBlockState(),
                    List.of()
            ));
        }

        /** Returns empty BitSet → no block entities rendered. */
        @Inject(method = "getAndAdjustShouldRenderBlockEntities", at = @At("RETURN"), cancellable = true)
        private void loconautics$emptyBlockEntities(CallbackInfoReturnable<BitSet> cir) {
            if (!isPhysicsTrain()) return;
            cir.setReturnValue(new BitSet());
        }

        /** Cancels Flywheel visualizer setup → no GPU-instanced rendering. */
        @Inject(method = "setupRenderLevelAndRenderedBlockEntities", at = @At("HEAD"), cancellable = true)
        private void loconautics$cancelSetup(CallbackInfo ci) {
            if (isPhysicsTrain()) ci.cancel();
        }

        private boolean isPhysicsTrain() {
            if (contraption == null) return false;
            AbstractContraptionEntity entity = contraption.entity;
            if (!(entity instanceof CarriageContraptionEntity cce)) return false;
            UUID id = cce.trainId;
            return id != null && ClientPhysicsTrainRegistry.isPhysicsTrain(id);
        }
    }
}