/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  xaero.map.gui.GuiMap
 */
package com.simibubi.create.foundation.mixin.compat.xaeros;

import com.simibubi.create.Create;
import com.simibubi.create.compat.trainmap.XaeroTrainMap;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.gui.GuiMap;

@Mixin(value={GuiMap.class})
public abstract class XaeroFullscreenMapMixin {
    @Unique
    private boolean create$failedToRenderTrainMap = false;

    @Inject(method={"render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V")}, require=0)
    private void create$xaeroMapFullscreenRender(GuiGraphics guiGraphics, int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci) {
        try {
            if (!this.create$failedToRenderTrainMap) {
                XaeroTrainMap.onRender(guiGraphics, (GuiMap)this, scaledMouseX, scaledMouseY, partialTicks);
            }
        }
        catch (Throwable e) {
            Create.LOGGER.error("Failed to render Xaero's World Map train map integration:", e);
            this.create$failedToRenderTrainMap = true;
        }
    }
}
