/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 */
package com.simibubi.create.foundation.gui.widget;

import com.simibubi.create.foundation.gui.widget.CompositeWidget;
import net.minecraft.client.gui.GuiGraphics;

public class ScreenOverlay
extends CompositeWidget {
    public final int zOffset;

    public ScreenOverlay(int zOffset) {
        this.zOffset = zOffset;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.pose().pushPose();
        graphics.pose().translate(0.0f, 0.0f, (float)this.zOffset);
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.pose().popPose();
    }
}
