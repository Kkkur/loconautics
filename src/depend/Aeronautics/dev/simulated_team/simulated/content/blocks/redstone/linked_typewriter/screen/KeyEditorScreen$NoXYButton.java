/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.simibubi.create.AllKeys
 *  com.simibubi.create.foundation.gui.AllGuiTextures
 *  com.simibubi.create.foundation.gui.widget.IconButton
 *  net.createmod.catnip.gui.element.ScreenElement
 *  net.minecraft.client.gui.GuiGraphics
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;

public static class KeyEditorScreen.NoXYButton
extends IconButton {
    public KeyEditorScreen.NoXYButton(ScreenElement icon) {
        super(0, 0, icon);
    }

    public void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            boolean bl = this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            AllGuiTextures button = !this.active ? AllGuiTextures.BUTTON_DISABLED : (this.isHovered && AllKeys.isMouseButtonDown((int)0) ? AllGuiTextures.BUTTON_DOWN : (this.isHovered ? AllGuiTextures.BUTTON_HOVER : (this.green ? AllGuiTextures.BUTTON_GREEN : AllGuiTextures.BUTTON)));
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            graphics.blit(button.location, 0, 0, button.getStartX(), button.getStartY(), button.getWidth(), button.getHeight());
            this.icon.render(graphics, 1, 1);
        }
    }
}
