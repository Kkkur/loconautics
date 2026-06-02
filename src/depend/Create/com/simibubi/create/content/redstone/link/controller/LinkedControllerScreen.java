/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.GuiGameElement$GuiRenderBuilder
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.redstone.link.controller;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerMenu;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.ControlsUtil;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class LinkedControllerScreen
extends AbstractSimiContainerScreen<LinkedControllerMenu> {
    protected AllGuiTextures background;
    private List<Rect2i> extraAreas = Collections.emptyList();
    private IconButton resetButton;
    private IconButton confirmButton;

    public LinkedControllerScreen(LinkedControllerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.background = AllGuiTextures.LINKED_CONTROLLER;
    }

    @Override
    protected void init() {
        this.setWindowSize(this.background.getWidth(), this.background.getHeight() + 4 + AllGuiTextures.PLAYER_INVENTORY.getHeight());
        this.setWindowOffset(1, 0);
        super.init();
        int x = this.leftPos;
        int y = this.topPos;
        this.resetButton = new IconButton(x + this.background.getWidth() - 62, y + this.background.getHeight() - 24, AllIcons.I_TRASH);
        this.resetButton.withCallback(() -> {
            ((LinkedControllerMenu)this.menu).clearContents();
            ((LinkedControllerMenu)this.menu).sendClearPacket();
        });
        this.confirmButton = new IconButton(x + this.background.getWidth() - 33, y + this.background.getHeight() - 24, AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(() -> this.minecraft.player.closeContainer());
        this.addRenderableWidget((GuiEventListener)this.resetButton);
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.extraAreas = ImmutableList.of((Object)new Rect2i(x + this.background.getWidth() + 4, y + this.background.getHeight() - 44, 64, 56));
    }

    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int invX = this.getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.getWidth());
        int invY = this.topPos + this.background.getHeight() + 4;
        this.renderPlayerInventory(graphics, invX, invY);
        int x = this.leftPos;
        int y = this.topPos;
        this.background.render(graphics, x, y);
        graphics.drawString(this.font, this.title, x + 15, y + 4, 5841956, false);
        ((GuiGameElement.GuiRenderBuilder)GuiGameElement.of((ItemStack)((ItemStack)((LinkedControllerMenu)this.menu).contentHolder)).at((float)(x + this.background.getWidth() - 4), (float)(y + this.background.getHeight() - 56), -200.0f)).scale(5.0).render(graphics);
    }

    @Override
    protected void containerTick() {
        if (!ItemStack.matches((ItemStack)((LinkedControllerMenu)this.menu).player.getMainHandItem(), (ItemStack)((ItemStack)((LinkedControllerMenu)this.menu).contentHolder))) {
            ((LinkedControllerMenu)this.menu).player.closeContainer();
        }
        super.containerTick();
    }

    protected void renderTooltip(GuiGraphics graphics, int x, int y) {
        if (!((LinkedControllerMenu)this.menu).getCarried().isEmpty() || this.hoveredSlot == null || this.hoveredSlot.container == ((LinkedControllerMenu)this.menu).playerInventory) {
            super.renderTooltip(graphics, x, y);
            return;
        }
        List<Object> list = new LinkedList<Component>();
        if (this.hoveredSlot.hasItem()) {
            list = this.getTooltipFromContainerItem(this.hoveredSlot.getItem());
        }
        graphics.renderComponentTooltip(this.font, this.addToTooltip(list, this.hoveredSlot.getSlotIndex()), x, y);
    }

    private List<Component> addToTooltip(List<Component> list, int slot) {
        if (slot < 0 || slot >= 12) {
            return list;
        }
        list.add((Component)CreateLang.translateDirect("linked_controller.frequency_slot_" + (slot % 2 + 1), ControlsUtil.getControls().get(slot / 2).getTranslatedKeyMessage().getString()).withStyle(ChatFormatting.GOLD));
        return list;
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return this.extraAreas;
    }
}
