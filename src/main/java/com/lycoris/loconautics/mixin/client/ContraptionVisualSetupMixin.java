package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.client.ClientPhysicsTrainRegistry;
import com.lycoris.loconautics.core.LoconauticsConstants;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.AbstractEntityVisual;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Stops Flywheel from creating <b>child block-entity visuals</b> for a physics train's ghost carriage —
 * most importantly the animated <b>bogey</b> (wheel) visuals.
 *
 * <p>{@code ContraptionVisual.setupChildren} iterates the contraption's block entities and calls
 * {@code setupVisualizer} for each, which is what creates the {@code BogeyBlockEntityVisual}. The
 * structure (block models) hides via {@code getRenderedBlocks}, but those child visuals are separate
 * and survive — that's why the wheels stay visible. Cancelling {@code setupVisualizer} for physics
 * trains drops them all (the real, physical blocks are rendered by the Sable sub-level instead). This
 * is the same approach Create-Interactive uses ({@code MixinContraptionVisual.preSetupVisualizer}).
 *
 * <p>We declare the mixin as {@code extends AbstractEntityVisual<T>} — the genuine superclass of
 * {@code ContraptionVisual} — purely so we can read its {@code protected final entity} field WITHOUT an
 * {@code @Shadow} of an inherited foreign field (that pattern previously crashed at load — see HANDOFF
 * problem #8). The constructor below is never executed; it exists only to satisfy the Java compiler.
 */
@Mixin(ContraptionVisual.class)
public abstract class ContraptionVisualSetupMixin<T extends AbstractContraptionEntity> extends AbstractEntityVisual<T> {

    private ContraptionVisualSetupMixin(VisualizationContext ctx, T entity, float partialTick) {
        super(ctx, entity, partialTick);
    }

    /** Diagnostic only: limits how many cancels we log, so we can confirm the hook fires without spam. */
    private static int loconautics$logged = 0;

    @Inject(method = "setupVisualizer", at = @At("HEAD"), cancellable = true, remap = false)
    private <B extends BlockEntity> void loconautics$skipPhysicsTrainBlockEntities(B be, float partialTicks, CallbackInfo ci) {
        if (entity instanceof CarriageContraptionEntity cce && cce.trainId != null
                && ClientPhysicsTrainRegistry.isPhysicsTrain(cce.trainId)) {
            if (loconautics$logged < 10) {
                loconautics$logged++;
                LoconauticsConstants.LOGGER.info("[bogey-cancel] skipped setupVisualizer for BE {} of physics train {}",
                        be.getType(), cce.trainId);
            }
            ci.cancel();
        }
    }
}
