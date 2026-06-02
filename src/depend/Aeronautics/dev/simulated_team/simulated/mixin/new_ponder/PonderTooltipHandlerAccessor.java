/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.foundation.PonderTooltipHandler
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.simulated_team.simulated.mixin.new_ponder;

import net.createmod.ponder.foundation.PonderTooltipHandler;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={PonderTooltipHandler.class})
public interface PonderTooltipHandlerAccessor {
    @Accessor
    public static ItemStack getTrackingStack() {
        return null;
    }
}
