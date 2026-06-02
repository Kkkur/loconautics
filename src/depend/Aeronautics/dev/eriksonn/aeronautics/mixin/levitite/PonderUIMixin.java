/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.foundation.ui.PonderUI
 *  net.minecraft.client.gui.GuiGraphics
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.eriksonn.aeronautics.mixin.levitite;

import dev.eriksonn.aeronautics.content.blocks.levitite.LevititeShaderManager;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PonderUI.class})
public class PonderUIMixin {
    @Inject(method={"renderScene"}, at={@At(value="HEAD")})
    protected void renderScene(GuiGraphics graphics, int mouseX, int mouseY, int i, float partialTicks, CallbackInfo ci) {
        LevititeShaderManager.disableShader();
    }
}
