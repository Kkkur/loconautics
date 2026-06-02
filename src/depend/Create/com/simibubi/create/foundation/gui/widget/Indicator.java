/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.gui.widget.AbstractSimiWidget
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 */
package com.simibubi.create.foundation.gui.widget;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class Indicator
extends AbstractSimiWidget {
    public State state;

    public Indicator(int x, int y, Component tooltip) {
        super(x, y, AllGuiTextures.INDICATOR.getWidth(), AllGuiTextures.INDICATOR.getHeight());
        this.toolTip = this.toolTip.isEmpty() ? ImmutableList.of() : ImmutableList.of((Object)tooltip);
        this.state = State.OFF;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        (switch (this.state.ordinal()) {
            case 1 -> AllGuiTextures.INDICATOR_WHITE;
            case 0 -> AllGuiTextures.INDICATOR;
            case 2 -> AllGuiTextures.INDICATOR_RED;
            case 3 -> AllGuiTextures.INDICATOR_YELLOW;
            case 4 -> AllGuiTextures.INDICATOR_GREEN;
            default -> AllGuiTextures.INDICATOR;
        }).render(graphics, this.getX(), this.getY());
    }

    public static enum State {
        OFF,
        ON,
        RED,
        YELLOW,
        GREEN;

    }
}
