/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.createmod.catnip.lang.LangBuilder
 *  net.createmod.ponder.foundation.PonderTooltipHandler
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.simulated_team.simulated.mixin.new_ponder;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.mixin.new_ponder.PonderTooltipHandlerAccessor;
import dev.simulated_team.simulated.ponder.new_ponder_tooltip.NewPonderTooltipManager;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.ponder.foundation.PonderTooltipHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={PonderTooltipHandler.class})
public class PonderTooltipHandlerMixin {
    @WrapOperation(method={"makeProgressBar"}, at={@At(value="INVOKE", target="Lnet/createmod/catnip/lang/LangBuilder;component()Lnet/minecraft/network/chat/MutableComponent;")})
    private static MutableComponent simulated$addToTooltip(LangBuilder instance, Operation<MutableComponent> original) {
        MutableComponent component = (MutableComponent)original.call(new Object[]{instance});
        ItemStack stack = PonderTooltipHandlerAccessor.getTrackingStack();
        if (stack != null && !NewPonderTooltipManager.hasWatchedAllScenes(stack.getItem())) {
            component.append(" ").append((Component)SimLang.translate("tooltip.new_ponder", new Object[0]).style(ChatFormatting.GOLD).component());
        }
        return component;
    }
}
