/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyboardHandler
 *  net.minecraft.client.Minecraft
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.simulated_team.simulated.mixin.hold_interaction;

import dev.simulated_team.simulated.events.SimulatedCommonClientEvents;
import dev.simulated_team.simulated.util.SimDistUtil;
import dev.simulated_team.simulated.util.click_interactions.InteractCallback;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={KeyboardHandler.class})
public class KeyboardHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method={"keyPress"}, at={@At(value="FIELD", target="Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", ordinal=0, opcode=180)}, cancellable=true)
    private void simulated$preOnPress(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (this.minecraft.screen == null) {
            InteractCallback.Result status;
            if (action == 2) {
                ci.cancel();
                return;
            }
            if (SimDistUtil.getClientPlayer() != null && !SimDistUtil.getClientPlayer().isSpectator() && (status = SimulatedCommonClientEvents.onBeforeMouseInput(InteractCallback.Input.key(key, scanCode), modifiers, action)).cancelled()) {
                ci.cancel();
            }
        }
    }
}
