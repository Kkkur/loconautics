/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.schematics.client;

import com.simibubi.create.AllItems;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;

public class SchematicPromptScreen
extends AbstractSimiScreen {
    private AllGuiTextures background;
    private final Component convertLabel = CreateLang.translateDirect("schematicAndQuill.convert", new Object[0]);
    private final Component abortLabel = CreateLang.translateDirect("action.discard", new Object[0]);
    private final Component confirmLabel = CreateLang.translateDirect("action.saveToFile", new Object[0]);
    private EditBox nameField;
    private IconButton confirm;
    private IconButton abort;
    private IconButton convert;

    public SchematicPromptScreen() {
        super((Component)CreateLang.translateDirect("schematicAndQuill.title", new Object[0]));
        this.background = AllGuiTextures.SCHEMATIC_PROMPT;
    }

    public void init() {
        this.setWindowSize(this.background.getWidth(), this.background.getHeight());
        super.init();
        int x = this.guiLeft;
        int y = this.guiTop + 2;
        this.nameField = new EditBox(this.font, x + 49, y + 26, 131, 10, CommonComponents.EMPTY);
        this.nameField.setTextColor(-1);
        this.nameField.setTextColorUneditable(-1);
        this.nameField.setBordered(false);
        this.nameField.setMaxLength(35);
        this.nameField.setFocused(true);
        this.setFocused((GuiEventListener)this.nameField);
        this.addRenderableWidget((GuiEventListener)this.nameField);
        this.abort = new IconButton(x + 7, y + 53, AllIcons.I_TRASH);
        this.abort.withCallback(() -> {
            CreateClient.SCHEMATIC_AND_QUILL_HANDLER.discard();
            this.onClose();
        });
        this.abort.setToolTip(this.abortLabel);
        this.addRenderableWidget((GuiEventListener)this.abort);
        this.confirm = new IconButton(x + 158, y + 53, AllIcons.I_CONFIRM);
        this.confirm.withCallback(() -> this.confirm(false));
        this.confirm.setToolTip(this.confirmLabel);
        this.addRenderableWidget((GuiEventListener)this.confirm);
        this.convert = new IconButton(x + 180, y + 53, AllIcons.I_SCHEMATIC);
        this.convert.withCallback(() -> this.confirm(true));
        this.convert.setToolTip(this.convertLabel);
        this.addRenderableWidget((GuiEventListener)this.convert);
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = this.guiLeft;
        int y = this.guiTop;
        this.background.render(graphics, x, y);
        graphics.drawString(this.font, this.title, x + (this.background.getWidth() - 8 - this.font.width((FormattedText)this.title)) / 2, y + 4, 0x505050, false);
        GuiGameElement.of((ItemStack)AllItems.SCHEMATIC.asStack()).at((float)(x + 22), (float)(y + 24), 0.0f).render(graphics);
        GuiGameElement.of((ItemStack)AllItems.SCHEMATIC_AND_QUILL.asStack()).scale(3.0).at((float)(x + this.background.getWidth() + 6), (float)(y + this.background.getHeight() - 38), -200.0f).render(graphics);
    }

    public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (keyCode == 257) {
            this.confirm(false);
            return true;
        }
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }
        return this.nameField.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);
    }

    private void confirm(boolean convertImmediately) {
        CreateClient.SCHEMATIC_AND_QUILL_HANDLER.saveSchematic(this.nameField.getValue(), convertImmediately);
        this.onClose();
    }
}
