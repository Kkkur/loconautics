/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.network.chat.CommonComponents
 */
package com.simibubi.create.infrastructure.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;

protected static class CreateMainMenuScreen.PlatformIconButton
extends Button {
    protected final AllGuiTextures icon;
    protected final float scale;

    public CreateMainMenuScreen.PlatformIconButton(int pX, int pY, int pWidth, int pHeight, AllGuiTextures icon, float scale, Button.OnPress pOnPress, Tooltip tooltip) {
        super(pX, pY, pWidth, pHeight, CommonComponents.EMPTY, pOnPress, DEFAULT_NARRATION);
        this.icon = icon;
        this.scale = scale;
        this.setTooltip(tooltip);
    }

    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pt) {
        super.renderWidget(graphics, pMouseX, pMouseY, pt);
        PoseStack pPoseStack = graphics.pose();
        pPoseStack.pushPose();
        pPoseStack.translate((float)(this.getX() + this.width / 2) - (float)this.icon.getWidth() * this.scale / 2.0f, (float)(this.getY() + this.height / 2) - (float)this.icon.getHeight() * this.scale / 2.0f, 0.0f);
        pPoseStack.scale(this.scale, this.scale, 1.0f);
        this.icon.render(graphics, 0, 0);
        pPoseStack.popPose();
    }
}
