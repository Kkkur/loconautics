/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.SubtitleOverlay
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={Gui.class})
public interface GuiAccessor {
    @Accessor(value="subtitleOverlay")
    public SubtitleOverlay create$getSubtitleOverlay();

    @Accessor(value="toolHighlightTimer")
    public int create$getToolHighlightTimer();

    @Invoker(value="renderTextureOverlay")
    public void create$renderTextureOverlay(GuiGraphics var1, ResourceLocation var2, float var3);
}
