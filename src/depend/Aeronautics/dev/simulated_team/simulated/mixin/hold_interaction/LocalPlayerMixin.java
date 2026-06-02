/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.player.Input
 *  net.minecraft.client.player.LocalPlayer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.simulated_team.simulated.mixin.hold_interaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import dev.simulated_team.simulated.util.hold_interaction.HoldInteractionManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LocalPlayer.class})
public abstract class LocalPlayerMixin
extends AbstractClientPlayer {
    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method={"isShiftKeyDown"}, at={@At(value="RETURN")}, cancellable=true)
    private void simulated$handlerShiftBlock(CallbackInfoReturnable<Boolean> cir) {
        if (((Boolean)cir.getReturnValue()).booleanValue() && !HoldInteractionManager.canCrouch()) {
            cir.setReturnValue((Object)false);
        }
    }

    @WrapOperation(method={"tick"}, at={@At(value="FIELD", target="Lnet/minecraft/client/player/Input;shiftKeyDown:Z")})
    private boolean simulated$shhhhDontTellTheServer(Input instance, Operation<Boolean> original) {
        return (Boolean)original.call(new Object[]{instance}) != false && HoldInteractionManager.canCrouch();
    }
}
