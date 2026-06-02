/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.foundation.gui.AllIcons
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.ScreenElement
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.item.ItemStack
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllIcons;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterEntries;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.KeyEditorScreen;
import dev.simulated_team.simulated.index.SimGUITextures;
import dev.simulated_team.simulated.index.SimIcons;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

private class KeyEditorScreen.KeyEntryWidget {
    private final KeyEditorScreen.NoXYButton editWidget = (KeyEditorScreen.NoXYButton)new KeyEditorScreen.NoXYButton((ScreenElement)SimIcons.ADD_OR_EDIT).withCallback(() -> KeyEditorScreen.this.modifyEntry(this));
    private final KeyEditorScreen.NoXYButton deleteWidget = (KeyEditorScreen.NoXYButton)new KeyEditorScreen.NoXYButton((ScreenElement)AllIcons.I_TRASH).withCallback(() -> KeyEditorScreen.this.removeWidget(this));
    private LinkedTypewriterEntries.KeyboardEntry entry;

    public KeyEditorScreen.KeyEntryWidget(LinkedTypewriterEntries.KeyboardEntry entry) {
        this.entry = entry;
    }

    private float getCurrentHeight(float partialTick) {
        int index = KeyEditorScreen.this.keyboardEntryWrappers.indexOf((Object)this);
        return (float)(KeyEditorScreen.this.parentScreen.getTopPos() - 65 + 50) - KeyEditorScreen.this.lerpedScroll.getValue(partialTick) + (float)(index * (SimGUITextures.LINKED_TYPEWRITER_KEY_ENTRY.height + 3));
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pt, PoseStack ps) {
        ps.pushPose();
        int x = KeyEditorScreen.this.leftPos() + 12;
        float y = this.getCurrentHeight(pt);
        ps.translate((float)x, y, 0.0f);
        int editIconOffset = 167;
        float iconY = (float)KeyEditorScreen.KEY_ENTRY.height / 2.0f;
        this.updateWidgetPositions(x, 167, y, iconY);
        this.renderItems(guiGraphics, ps);
        ps.popPose();
    }

    public void renderBackground(GuiGraphics guiGraphics, float pt, int mouseX, int mouseY) {
        PoseStack ps = guiGraphics.pose();
        int editIconOffset = 167;
        float iconY = (float)KeyEditorScreen.KEY_ENTRY.height / 2.0f;
        ps.pushPose();
        int x = KeyEditorScreen.this.leftPos() + 12;
        float y = this.getCurrentHeight(pt);
        ps.translate((float)x, y, 0.0f);
        KEY_ENTRY.render(guiGraphics, 0, 0);
        this.renderText(guiGraphics, ps);
        this.renderWidgets(guiGraphics, mouseX, mouseY, pt, ps, 167, iconY);
        ps.popPose();
    }

    private void updateWidgetPositions(int x, int editIconOffset, float y, float iconY) {
        this.editWidget.setX(x + editIconOffset);
        this.editWidget.setY((int)(y + iconY - 9.0f));
        this.deleteWidget.setX(x + editIconOffset + 23);
        this.deleteWidget.setY((int)(y + iconY - 9.0f));
    }

    private void renderItems(GuiGraphics guiGraphics, PoseStack ps) {
        if (!KeyEditorScreen.this.parentScreen.modifier.modifying) {
            ps.pushPose();
            ps.translate(0.0f, (float)KeyEditorScreen.KEY_ENTRY.height / 2.0f - 8.0f, 0.0f);
            ps.translate(82.0f, 0.0f, 0.0f);
            GuiGameElement.of((ItemStack)this.entry.getFirstAsItemStack()).render(guiGraphics);
            ps.translate(18.0f, 0.0f, 0.0f);
            GuiGameElement.of((ItemStack)this.entry.getSecondAsItemStack()).render(guiGraphics);
            ps.popPose();
        }
    }

    private void renderText(GuiGraphics guiGraphics, PoseStack ps) {
        ps.pushPose();
        ps.translate(9.0f, 11.0f, 0.0f);
        guiGraphics.drawString(Minecraft.getInstance().font, InputConstants.getKey((int)this.entry.glfwKeyCode, (int)-1).getDisplayName(), 0, 0, 0xFFFFFF, true);
        ps.popPose();
    }

    private void renderWidgets(GuiGraphics guiGraphics, int mouseX, int mouseY, float pt, PoseStack ps, int editIconOffset, float iconY) {
        ps.pushPose();
        ps.translate((float)editIconOffset, iconY - 9.0f, 0.0f);
        this.editWidget.render(guiGraphics, mouseX, mouseY, pt);
        ps.translate(23.0f, 0.0f, 0.0f);
        this.deleteWidget.render(guiGraphics, mouseX, mouseY, pt);
        ps.popPose();
    }
}
