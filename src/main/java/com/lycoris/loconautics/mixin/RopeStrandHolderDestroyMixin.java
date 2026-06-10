package com.lycoris.loconautics.mixin;

import com.lycoris.loconautics.content.steelcable.SteelCableTracker;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

/**
 * Fixes steel cable strands dropping Simulated's rope item on break.
 * Also unregisters the UUID from the persistent SavedData when the strand is destroyed.
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
        RopeStrandHolderBehavior self = (RopeStrandHolderBehavior) (Object) this;
        UUID ropeId = ((RopeStrandHolderBehaviorAccessor) self).loconautics$getAttachedRopeID();

        if (ropeId != null && SteelCableTracker.isSteelCable(ropeId)) {
            ServerLevel serverLevel = ((RopeStrandHolderBehaviorAccessor) self).loconautics$getLevel();
            if (serverLevel != null) {
                SteelCableTracker.unregisterServer(serverLevel, ropeId);
            } else {
                SteelCableTracker.unregister(ropeId);
            }
            return new ItemStack(LoconauticsRegistries.STEEL_CABLE.get());
        }

        return new ItemStack(item);
    }
}