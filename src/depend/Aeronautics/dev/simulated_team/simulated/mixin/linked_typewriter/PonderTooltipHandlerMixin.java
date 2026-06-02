/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.foundation.PonderTooltipHandler
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.MutableComponent
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.simulated_team.simulated.mixin.linked_typewriter;

import net.createmod.ponder.foundation.PonderTooltipHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={PonderTooltipHandler.class})
public class PonderTooltipHandlerMixin {
    @Redirect(method={"makeProgressBar"}, at=@At(value="INVOKE", target="Lnet/minecraft/network/chat/MutableComponent;withStyle(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/MutableComponent;", ordinal=0))
    private static MutableComponent dontBreakMutabilityContracts(MutableComponent instance, ChatFormatting format) {
        return instance.copy().withStyle(format);
    }
}
