/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.api.behaviour.display.DisplayTarget
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkContext
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkScreen
 *  com.simibubi.create.foundation.gui.widget.Label
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.simulated_team.simulated.mixin.conditional_display_target;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkScreen;
import com.simibubi.create.foundation.gui.widget.Label;
import dev.simulated_team.simulated.api.ConditionalDisplayTarget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={DisplayLinkScreen.class})
public class DisplayLinkScreenMixin {
    @Shadow
    private DisplayLinkBlockEntity blockEntity;
    @Shadow
    private Label targetLineLabel;

    @WrapOperation(method={"initGathererOptions"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/api/behaviour/display/DisplayTarget;getLineOptionText(I)Lnet/minecraft/network/chat/Component;")})
    private Component simulated$displayConditionalError(DisplayTarget instance, int line, Operation<Component> original, @Local(name={"level"}) ClientLevel level) {
        ConditionalDisplayTarget cdt;
        DisplayLinkContext context = new DisplayLinkContext((Level)level, this.blockEntity);
        if (instance instanceof ConditionalDisplayTarget && !(cdt = (ConditionalDisplayTarget)instance).allowsWriting(context)) {
            this.targetLineLabel.colored(ChatFormatting.GRAY.getColor().intValue());
            return cdt.getErrorMessage(context);
        }
        return (Component)original.call(new Object[]{instance, line});
    }
}
