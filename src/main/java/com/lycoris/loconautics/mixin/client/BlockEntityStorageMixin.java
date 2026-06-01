package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.core.LoconauticsConstants;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import dev.engine_room.flywheel.impl.visualization.storage.BlockEntityStorage;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Stops Flywheel from creating a visual for a <b>bogey block entity</b> that lives inside a physics
 * train's Sable sub-level.
 *
 * <p>When a train is assembled, Create captures the bogey blocks into the contraption, so they end up
 * as real blocks in the sub-level's plot. Flywheel then visualizes their {@link AbstractBogeyBlockEntity}
 * (the animated wheels) directly via {@link BlockEntityStorage}, independently of the (already
 * suppressed) ghost-carriage render — which is why the wheels stayed visible despite every
 * contraption-side fix. This is the same hook Create-Interactive uses for Valkyrien Skies ships
 * ({@code MixinVisualManager} on Flywheel's BlockEntityStorage); reimplemented here for Sable.
 *
 * <p>We only pay the sub-level lookup for actual bogey BEs (cheap {@code instanceof} guard first).
 */
@Mixin(BlockEntityStorage.class)
public class BlockEntityStorageMixin {

    private static int loconautics$logged = 0;

    @Inject(
            method = "willAccept(Lnet/minecraft/world/level/block/entity/BlockEntity;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private void loconautics$suppressPhysicsTrainBogey(BlockEntity be, CallbackInfoReturnable<Boolean> cir) {
        if (be instanceof AbstractBogeyBlockEntity) {
            // Race-free: suppress the Flywheel bogey visual for ANY bogey that lives inside a Sable
            // sub-level. We only ever create sub-levels for physics trains, and this check works the
            // moment the sub-level exists (no dependency on the sync packet arriving first).
            ClientSubLevel sub = Sable.HELPER.getContainingClient(be);
            if (loconautics$logged < 12) {
                loconautics$logged++;
                LoconauticsConstants.LOGGER.info("[bogey-accept] bogey BE at {} containingSubLevel={}",
                        be.getBlockPos(), sub == null ? "null" : sub.getUniqueId());
            }
            if (sub != null) {
                cir.setReturnValue(false);
            }
        }
    }
}
