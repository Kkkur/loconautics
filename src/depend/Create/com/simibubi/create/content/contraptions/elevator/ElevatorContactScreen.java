/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.GuiGameElement$GuiRenderBuilder
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.contraptions.elevator;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactEditPacket;
import com.simibubi.create.content.decoration.slidingDoor.DoorControl;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.TooltipArea;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

public class ElevatorContactScreen
extends AbstractSimiScreen {
    private AllGuiTextures background;
    private EditBox shortNameInput;
    private EditBox longNameInput;
    private IconButton confirm;
    private String shortName;
    private String longName;
    private DoorControl doorControl;
    private BlockPos pos;

    public ElevatorContactScreen(BlockPos pos, String prevShortName, String prevLongName, DoorControl prevDoorControl) {
        super((Component)CreateLang.translateDirect("elevator_contact.title", new Object[0]));
        this.pos = pos;
        this.doorControl = prevDoorControl;
        this.background = AllGuiTextures.ELEVATOR_CONTACT;
        this.shortName = prevShortName;
        this.longName = prevLongName;
    }

    public void init() {
        this.setWindowSize(this.background.getWidth() + 30, this.background.getHeight());
        super.init();
        int x = this.guiLeft;
        int y = this.guiTop;
        this.confirm = new IconButton(x + 200, y + 58, AllIcons.I_CONFIRM);
        this.confirm.withCallback(this::confirm);
        this.addRenderableWidget((GuiEventListener)this.confirm);
        this.shortNameInput = this.editBox(33, 30, 4);
        this.shortNameInput.setValue(this.shortName);
        this.centerInput(x);
        this.shortNameInput.setResponder(s -> {
            this.shortName = s;
            this.centerInput(x);
        });
        this.shortNameInput.setFocused(true);
        this.setFocused((GuiEventListener)this.shortNameInput);
        this.shortNameInput.setHighlightPos(0);
        this.longNameInput = this.editBox(63, 140, 30);
        this.longNameInput.setValue(this.longName);
        this.longNameInput.setResponder(s -> {
            this.longName = s;
        });
        MutableComponent rmbToEdit = CreateLang.translate("gui.schedule.lmb_edit", new Object[0]).style(ChatFormatting.DARK_GRAY).style(ChatFormatting.ITALIC).component();
        this.addRenderableOnly((Renderable)new TooltipArea(x + 21, y + 23, 30, 18).withTooltip((List<Component>)ImmutableList.of((Object)CreateLang.translate("elevator_contact.floor_identifier", new Object[0]).color(5476833).component(), (Object)rmbToEdit)));
        this.addRenderableOnly((Renderable)new TooltipArea(x + 57, y + 23, 147, 18).withTooltip((List<Component>)ImmutableList.of((Object)CreateLang.translate("elevator_contact.floor_description", new Object[0]).color(5476833).component(), (Object)CreateLang.translate("crafting_blueprint.optional", new Object[0]).style(ChatFormatting.GRAY).component(), (Object)rmbToEdit)));
        Pair<ScrollInput, Label> doorControlWidgets = DoorControl.createWidget(x + 58, y + 57, mode -> {
            this.doorControl = mode;
        }, this.doorControl);
        this.addRenderableWidget((GuiEventListener)((ScrollInput)((Object)doorControlWidgets.getFirst())));
        this.addRenderableWidget((GuiEventListener)((Label)((Object)doorControlWidgets.getSecond())));
    }

    private int centerInput(int x) {
        int centeredX = x + (this.shortName.isEmpty() ? 34 : 36 - this.font.width(this.shortName) / 2);
        this.shortNameInput.setX(centeredX);
        return centeredX;
    }

    private EditBox editBox(int x, int width, int chars) {
        EditBox editBox = new EditBox(this.font, this.guiLeft + x, this.guiTop + 30, width, 10, CommonComponents.EMPTY);
        editBox.setTextColor(-1);
        editBox.setTextColorUneditable(-1);
        editBox.setBordered(false);
        editBox.setMaxLength(chars);
        editBox.setFocused(false);
        editBox.mouseClicked(0.0, 0.0, 0);
        this.addRenderableWidget((GuiEventListener)editBox);
        return editBox;
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = this.guiLeft;
        int y = this.guiTop;
        this.background.render(graphics, x, y);
        FormattedCharSequence formattedcharsequence = this.title.getVisualOrderText();
        graphics.drawString(this.font, formattedcharsequence, (float)(x + (this.background.getWidth() - 8) / 2 - this.font.width(formattedcharsequence) / 2), (float)y + 6.0f, 3094328, false);
        ((GuiGameElement.GuiRenderBuilder)GuiGameElement.of((ItemStack)AllBlocks.ELEVATOR_CONTACT.asStack()).at((float)(x + this.background.getWidth() + 6), (float)(y + this.background.getHeight() - 56), -200.0f)).scale(5.0).render(graphics);
        graphics.renderItem(AllBlocks.TRAIN_DOOR.asStack(), x + 37, y + 58);
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        boolean consumed = super.mouseClicked(pMouseX, pMouseY, pButton);
        if (!this.shortNameInput.isFocused()) {
            int length = this.shortNameInput.getValue().length();
            this.shortNameInput.setHighlightPos(length);
            this.shortNameInput.setCursorPosition(length);
        }
        if (this.shortNameInput.isHoveredOrFocused()) {
            this.longNameInput.mouseClicked(0.0, 0.0, 0);
        }
        if (!consumed && pMouseX > (double)(this.guiLeft + 22) && pMouseY > (double)(this.guiTop + 24) && pMouseX < (double)(this.guiLeft + 50) && pMouseY < (double)(this.guiTop + 40)) {
            this.setFocused((GuiEventListener)this.shortNameInput);
            this.shortNameInput.setFocused(true);
            return true;
        }
        return consumed;
    }

    public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (super.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_)) {
            return true;
        }
        if (keyCode == 257) {
            this.confirm();
            return true;
        }
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }
        return false;
    }

    private void confirm() {
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ElevatorContactEditPacket(this.pos, this.shortName, this.longName, this.doorControl));
        this.onClose();
    }
}
