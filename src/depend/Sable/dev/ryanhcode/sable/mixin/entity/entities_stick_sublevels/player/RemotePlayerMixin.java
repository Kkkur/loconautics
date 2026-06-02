/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.player.RemotePlayer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entities_stick_sublevels.player;

import dev.ryanhcode.sable.mixinterface.entity.entities_stick_sublevels.LivingEntityStickExtension;
import net.minecraft.client.player.RemotePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RemotePlayer.class})
public abstract class RemotePlayerMixin
implements LivingEntityStickExtension {
    @Inject(method={"aiStep"}, at={@At(value="HEAD")})
    private void sable$updateRotLerp(CallbackInfo ci) {
        this.sable$setupLerp();
    }

    @Inject(method={"aiStep"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/player/RemotePlayer;updateSwingTime()V", shift=At.Shift.BEFORE)})
    private void sable$updateSubLevelPosition(CallbackInfo ci) {
        this.sable$applyLerp();
    }
}
