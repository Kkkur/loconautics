/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.simibubi.create.foundation.mixin.compat.xaeros;

import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.trainmap.XaeroTrainMap;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Screen.class})
public class XaeroPauseScreenOverrideMixin {
    @Inject(method={"isPauseScreen"}, at={@At(value="HEAD")}, cancellable=true)
    private void create$xaeroPauseScreenOverride(CallbackInfoReturnable<Boolean> cir) {
        if (Mods.XAEROWORLDMAP.isLoaded() && XaeroTrainMap.isMapOpen((Screen)this)) {
            cir.setReturnValue((Object)false);
        }
    }
}
