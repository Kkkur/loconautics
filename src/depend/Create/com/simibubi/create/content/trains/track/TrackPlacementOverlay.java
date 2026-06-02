/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.LayeredDraw$Layer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.GameType
 */
package com.simibubi.create.content.trains.track;

import com.mojang.blaze3d.platform.Window;
import com.simibubi.create.content.trains.track.TrackPlacement;
import com.simibubi.create.foundation.mixin.accessor.GuiAccessor;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;

public class TrackPlacementOverlay
implements LayeredDraw.Layer {
    public static final TrackPlacementOverlay INSTANCE = new TrackPlacementOverlay();

    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        if (TrackPlacement.hoveringPos == null) {
            return;
        }
        if (TrackPlacement.cached == null || TrackPlacement.cached.curve == null || !TrackPlacement.cached.valid) {
            return;
        }
        if (TrackPlacement.extraTipWarmup < 4) {
            return;
        }
        if (((GuiAccessor)mc.gui).create$getToolHighlightTimer() > 0) {
            return;
        }
        boolean active = mc.options.keySprint.isDown();
        MutableComponent text = CreateLang.translateDirect("track.hold_for_smooth_curve", Component.keybind((String)"key.sprint").withStyle(active ? ChatFormatting.WHITE : ChatFormatting.GRAY));
        Window window = mc.getWindow();
        int x = (window.getGuiScaledWidth() - mc.font.width((FormattedText)text)) / 2;
        int y = window.getGuiScaledHeight() - 61;
        Color color = new Color(4905802).setAlpha(Mth.clamp((float)((float)(TrackPlacement.extraTipWarmup - 4) / 3.0f), (float)0.1f, (float)1.0f));
        guiGraphics.drawString(mc.font, (Component)text, x, y, color.getRGB(), false);
    }
}
