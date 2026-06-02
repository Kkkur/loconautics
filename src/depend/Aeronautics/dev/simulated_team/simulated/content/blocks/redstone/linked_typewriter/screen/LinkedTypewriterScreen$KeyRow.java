/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.element.ScreenElement
 *  net.minecraft.client.gui.GuiGraphics
 *  org.joml.Vector2i
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen;

import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterEntries;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.widgets.KeyWidget;
import java.util.ArrayList;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2i;

private class LinkedTypewriterScreen.KeyRow
extends ArrayList<KeyWidget> {
    Vector2i pos;
    LinkedTypewriterBlockEntity be;

    public LinkedTypewriterScreen.KeyRow(int x, int y, LinkedTypewriterBlockEntity be) {
        this.pos = new Vector2i(x, y);
        this.be = be;
    }

    public void add(int length, int glfwKey, ScreenElement icon) {
        KeyWidget kWid = new KeyWidget(2, 2, length, glfwKey, icon, LinkedTypewriterScreen.this);
        kWid.withCallback(() -> {
            LinkedTypewriterScreen.this.switchStates(false);
            LinkedTypewriterScreen.this.modifier.startModifying(LinkedTypewriterScreen.this.newEntries.getEntry(glfwKey), newEntry -> {
                LinkedTypewriterScreen.this.switchStates(true);
                if (newEntry != null) {
                    LinkedTypewriterScreen.this.getNewEntries().setKey(newEntry.glfwKeyCode, (LinkedTypewriterEntries.KeyboardEntry)newEntry);
                }
            }).keyCode(glfwKey);
        });
        this.add(kWid);
    }

    public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float pt, boolean keyboardActive) {
        int length = 0;
        for (KeyWidget key : this) {
            key.render(guiGraphics, x + length, y, mouseX, mouseY, pt, keyboardActive);
            length += key.getWidth();
        }
    }
}
