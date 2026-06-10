package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.content.steelcable.SteelCableStrandRenderer;
import com.lycoris.loconautics.content.steelcable.SteelCableTracker;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.rope_connector.RopeConnectorBlockEntity;
import dev.simulated_team.simulated.content.blocks.rope.rope_connector.RopeConnectorRenderer;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ClientRopeStrand;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts {@link RopeConnectorRenderer#renderSafe} and redirects to
 * {@link SteelCableStrandRenderer} when the strand being rendered was created
 * by the Steel Cable item (identified via {@link SteelCableTracker}).
 */
@Mixin(value = RopeConnectorRenderer.class, remap = false)
public class RopeConnectorRendererMixin {

    @Inject(
            method = "renderSafe",
            at = @At("HEAD"),
            cancellable = true
    )
    private void loconautics$redirectSteelCableRender(
            RopeConnectorBlockEntity be,
            float partialTicks,
            PoseStack ms,
            MultiBufferSource buffer,
            int light,
            int overlay,
            CallbackInfo ci
    ) {
        RopeStrandHolderBehavior holder = be.getRopeHolder();
        if (!holder.ownsRope()) return;

        ClientRopeStrand clientStrand = holder.getClientStrand();
        if (clientStrand == null) return;

        if (!SteelCableTracker.isSteelCable(clientStrand.getUuid())) return;

        // It's a steel cable strand — render with our renderer and cancel Simulated's
        SteelCableStrandRenderer.render(be, holder, partialTicks, ms, buffer);
        ci.cancel();
    }
}