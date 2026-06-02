/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.createmod.catnip.gui.widget.AbstractSimiWidget
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public class Label
extends AbstractSimiWidget {
    public Component text;
    public String suffix;
    protected boolean hasShadow;
    protected int color;
    protected Font font;

    public Label(int x, int y, Component text) {
        super(x, y, Minecraft.getInstance().font.width((FormattedText)text), 10);
        this.font = Minecraft.getInstance().font;
        this.text = Component.literal((String)"Label");
        this.color = 0xFFFFFF;
        this.hasShadow = false;
        this.suffix = "";
    }

    public Label colored(int color) {
        this.color = color;
        return this;
    }

    public Label withShadow() {
        this.hasShadow = true;
        return this;
    }

    public Label withSuffix(String s) {
        this.suffix = s;
        return this;
    }

    public void setTextAndTrim(Component newText, boolean trimFront, int maxWidthPx) {
        Font fontRenderer = Minecraft.getInstance().font;
        if (fontRenderer.width((FormattedText)newText) <= maxWidthPx) {
            this.text = newText;
            return;
        }
        String trim = "...";
        int trimWidth = fontRenderer.width(trim);
        String raw = newText.getString();
        StringBuilder builder = new StringBuilder(raw);
        int startIndex = trimFront ? 0 : raw.length() - 1;
        int endIndex = !trimFront ? 0 : raw.length() - 1;
        int step = (int)Math.signum(endIndex - startIndex);
        for (int i = startIndex; i != endIndex; i += step) {
            String sub = builder.substring(trimFront ? i : startIndex, trimFront ? endIndex + 1 : i + 1);
            if (fontRenderer.width((FormattedText)Component.literal((String)sub).setStyle(newText.getStyle())) + trimWidth > maxWidthPx) continue;
            this.text = Component.literal((String)(trimFront ? trim + sub : sub + trim)).setStyle(newText.getStyle());
            return;
        }
    }

    protected void doRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.text == null || this.text.getString().isEmpty()) {
            return;
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        MutableComponent copy = this.text.plainCopy();
        if (this.suffix != null && !this.suffix.isEmpty()) {
            copy.append(this.suffix);
        }
        graphics.drawString(this.font, (Component)copy, this.getX(), this.getY(), this.color, this.hasShadow);
    }
}
