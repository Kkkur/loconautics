package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.client.ClientPhysicsTrainRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hides the Create carriage's <b>bogeys</b> (wheels) for physics trains.
 *
 * <p>The contraption <i>structure</i> is already suppressed via {@link ClientContraptionRenderMixin}
 * (empty {@code getRenderedBlocks}), but the bogeys are drawn separately — in <b>immediate mode</b> —
 * by {@code CarriageContraptionEntityRenderer.render()}, in a {@code carriage.bogeys.forEach(...)}
 * loop that runs <i>after</i> {@code super.render()} and executes even when Flywheel is active (the
 * superclass only skips the immediate <i>structure</i> draw under Flywheel, never the bogeys).
 *
 * <p>Cancelling the whole {@code render()} for physics trains drops the bogeys. It is safe: the
 * structure is already empty, and Flywheel's contraption visual is managed by the visualization
 * manager independently of this entity renderer, so nothing we still want to see is lost.
 */
@Mixin(CarriageContraptionEntityRenderer.class)
public class CarriageBogeyRenderMixin {

    @Inject(
            method = "render(Lcom/simibubi/create/content/trains/entity/CarriageContraptionEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private void loconautics$hidePhysicsTrainBogeys(CarriageContraptionEntity entity, float partialTicks, float yaw,
            PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        if (entity.trainId != null && ClientPhysicsTrainRegistry.isPhysicsTrain(entity.trainId)) {
            ci.cancel();
        }
    }
}
