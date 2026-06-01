package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.client.PhysicsTrainRenderInvalidator;
import com.lycoris.loconautics.core.LoconauticsConstants;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import dev.engine_room.flywheel.impl.visualization.storage.BlockEntityStorage;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hides Create's <b>ghost-carriage bogey</b> from Flywheel so the train's bogeys come ONLY from the
 * Sable sub-level — making the body and the bogeys one rigid object that can't drift apart (they share
 * the sub-level's render pose, which {@code ClientSubLevelRenderMixin} couples to the carriage).
 *
 * <p>Create draws the ghost carriage's bogey as a Flywheel {@link AbstractBogeyBlockEntity} visual that
 * lives in the contraption's <i>virtual render world</i>. We suppress exactly those (the BE's level is
 * one of the physics trains' contraption render worlds, tracked by
 * {@link PhysicsTrainRenderInvalidator}). The bogey blocks the sub-level captured render normally, at
 * the coupled pose. This is the same Flywheel hook Create-Interactive uses ({@code MixinVisualManager}
 * on {@code BlockEntityStorage}); reimplemented here for Sable.
 */
@Mixin(BlockEntityStorage.class)
public class BlockEntityStorageMixin {

    private static int loconautics$logged = 0;

    @Inject(
            method = "willAccept(Lnet/minecraft/world/level/block/entity/BlockEntity;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private void loconautics$hideGhostBogey(BlockEntity be, CallbackInfoReturnable<Boolean> cir) {
        if (be instanceof AbstractBogeyBlockEntity) {
            boolean ghostWorld = PhysicsTrainRenderInvalidator.isPhysicsContraptionWorld(be.getLevel());
            if (loconautics$logged < 20) {
                loconautics$logged++;
                LoconauticsConstants.LOGGER.info("[bogey-accept] bogey BE at {} level={}#{} ghostWorld={}",
                        be.getBlockPos(),
                        be.getLevel() == null ? "null" : be.getLevel().getClass().getSimpleName(),
                        System.identityHashCode(be.getLevel()), ghostWorld);
            }
            if (ghostWorld) {
                cir.setReturnValue(false);
            }
        }
    }
}
