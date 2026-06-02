/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 */
package com.simibubi.create.content.logistics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.AddressEditBoxHelper;
import com.simibubi.create.content.trains.schedule.DestinationSuggestions;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class AddressEditBox
extends EditBox {
    private DestinationSuggestions destinationSuggestions;
    private Consumer<String> mainResponder;
    private String prevValue = "=)";

    public AddressEditBox(Screen screen, Font pFont, int pX, int pY, int pWidth, int pHeight, boolean anchorToBottom) {
        this(screen, pFont, pX, pY, pWidth, pHeight, anchorToBottom, null);
    }

    public AddressEditBox(Screen screen, Font pFont, int pX, int pY, int pWidth, int pHeight, boolean anchorToBottom, String localAddress) {
        super(pFont, pX, pY, pWidth, pHeight, (Component)Component.empty());
        this.destinationSuggestions = AddressEditBoxHelper.createSuggestions(screen, this, anchorToBottom, localAddress);
        this.destinationSuggestions.setAllowSuggestions(true);
        this.destinationSuggestions.updateCommandInfo();
        this.mainResponder = t -> {
            if (!t.equals(this.prevValue)) {
                this.destinationSuggestions.updateCommandInfo();
            }
            this.prevValue = t;
        };
        this.setResponder(this.mainResponder);
        this.setBordered(false);
        this.setFocused(false);
        this.mouseClicked(0.0, 0.0, 0);
        this.setMaxLength(25);
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.destinationSuggestions.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        }
        if (this.isFocused() && pKeyCode == 257) {
            this.setFocused(false);
            this.moveCursorToEnd(false);
            this.mouseClicked(0.0, 0.0, 0);
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.destinationSuggestions.mouseScrolled(Mth.clamp((double)scrollY, (double)-1.0, (double)1.0))) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 1 && this.isMouseOver(pMouseX, pMouseY)) {
            this.setValue("");
            return true;
        }
        boolean wasFocused = this.isFocused();
        if (super.mouseClicked(pMouseX, pMouseY, pButton)) {
            if (!wasFocused) {
                this.setHighlightPos(0);
                this.setCursorPosition(this.getValue().length());
            }
            return true;
        }
        return this.destinationSuggestions.mouseClicked((int)pMouseX, (int)pMouseY, pButton);
    }

    public void setValue(String text) {
        this.setHighlightPos(0);
        super.setValue(text);
    }

    public void setFocused(boolean focused) {
        super.setFocused(focused);
    }

    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        PoseStack matrixStack = pGuiGraphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(0.0f, 0.0f, 500.0f);
        this.destinationSuggestions.render(pGuiGraphics, pMouseX, pMouseY);
        matrixStack.popPose();
        if (!this.destinationSuggestions.isEmpty()) {
            return;
        }
        int itemX = this.getX() + this.width + 4;
        int itemY = this.getY() - 4;
        pGuiGraphics.renderItem(AllBlocks.CLIPBOARD.asStack(), itemX, itemY);
        if (pMouseX >= itemX && pMouseX < itemX + 16 && pMouseY >= itemY && pMouseY < itemY + 16) {
            List<Object> promiseTip = List.of();
            promiseTip = List.of(CreateLang.translate("gui.address_box.clipboard_tip", new Object[0]).color(ScrollInput.HEADER_RGB).component(), CreateLang.translate("gui.address_box.clipboard_tip_1", new Object[0]).style(ChatFormatting.GRAY).component(), CreateLang.translate("gui.address_box.clipboard_tip_2", new Object[0]).style(ChatFormatting.GRAY).component(), CreateLang.translate("gui.address_box.clipboard_tip_3", new Object[0]).style(ChatFormatting.GRAY).component(), CreateLang.translate("gui.address_box.clipboard_tip_4", new Object[0]).style(ChatFormatting.DARK_GRAY).component());
            pGuiGraphics.renderComponentTooltip(Minecraft.getInstance().font, promiseTip, pMouseX, pMouseY);
        }
    }

    public void setResponder(Consumer<String> pResponder) {
        super.setResponder(pResponder == this.mainResponder ? this.mainResponder : this.mainResponder.andThen(pResponder));
    }

    public void tick() {
        if (!this.isFocused()) {
            this.destinationSuggestions.hide();
        }
        if (this.isFocused() && this.destinationSuggestions.suggestions == null) {
            this.destinationSuggestions.updateCommandInfo();
        }
        this.destinationSuggestions.tick();
    }
}
