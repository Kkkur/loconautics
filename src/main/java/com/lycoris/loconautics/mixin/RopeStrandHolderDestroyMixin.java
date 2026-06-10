package com.lycoris.loconautics.mixin;

import com.lycoris.loconautics.content.steelcable.SteelCableTracker;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.index.SimItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

/**
 * Fixes steel cable strands dropping Simulated's rope item on break.
 *
 * {@link RopeStrandHolderBehavior#destroyRope} hardcodes {@code SimItems.ROPE_COUPLING}
 * as the dropped item. We redirect the ItemStack construction and swap it for the
 * steel cable item when the strand UUID is tracked in {@link SteelCableTracker}.
 */
@Mixin(value = RopeStrandHolderBehavior.class, remap = false)
public class RopeStrandHolderDestroyMixin {

    @Redirect(
            method = "destroyRope",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/world/item/ItemStack",
                    ordinal = 0
            )
    )
    private ItemStack loconautics$swapDroppedItem(net.minecraft.world.level.ItemLike item) {
        // Check if the strand being destroyed is a steel cable strand
        RopeStrandHolderBehavior self = (RopeStrandHolderBehavior) (Object) this;
        UUID ropeId = ((RopeStrandHolderBehaviorAccessor) self).loconautics$getAttachedRopeID();

        if (ropeId != null && SteelCableTracker.isSteelCable(ropeId)) {
            // Untrack it — the strand is being destroyed
            SteelCableTracker.unregister(ropeId);
            return new ItemStack(LoconauticsRegistries.STEEL_CABLE.get());
        }

        return new ItemStack(item);
    }
}