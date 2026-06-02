/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Style
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.clipboard;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
static class ClipboardScreen.LineInfo {
    final Style style;
    final String contents;
    final Component asComponent;
    final int x;
    final int y;

    public ClipboardScreen.LineInfo(Style pStyle, String pContents, int pX, int pY) {
        this.style = pStyle;
        this.contents = pContents;
        this.x = pX;
        this.y = pY;
        this.asComponent = Component.literal((String)pContents).setStyle(pStyle);
    }
}
