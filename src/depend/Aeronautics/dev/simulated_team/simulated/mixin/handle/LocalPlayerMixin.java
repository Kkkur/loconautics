/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.player.LocalPlayer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.simulated_team.simulated.mixin.handle;

import dev.simulated_team.simulated.content.blocks.handle.ClientHandleHandler;
import dev.simulated_team.simulated.index.SimClickInteractions;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LocalPlayer.class})
public class LocalPlayerMixin {
    @Inject(method={"isMovingSlowly"}, at={@At(value="HEAD")}, cancellable=true)
    private void simulated$alwaysMovingSlowly(CallbackInfoReturnable<Boolean> cir) {
        ClientHandleHandler handleHandler = SimClickInteractions.HANDLE_HANDLER;
        if (handleHandler.isActive() && handleHandler.movingSubLevel) {
            cir.setReturnValue((Object)true);
        }
    }
}
