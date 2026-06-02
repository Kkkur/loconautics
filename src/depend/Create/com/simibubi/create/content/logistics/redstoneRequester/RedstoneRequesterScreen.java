/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package com.simibubi.create.content.logistics.redstoneRequester;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.AddressEditBox;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlockEntity;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterConfigurationPacket;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterMenu;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class RedstoneRequesterScreen
extends AbstractSimiContainerScreen<RedstoneRequesterMenu> {
    private AddressEditBox addressBox;
    private IconButton confirmButton;
    private List<Rect2i> extraAreas = Collections.emptyList();
    private List<Integer> amounts = new ArrayList<Integer>();
    private IconButton dontAllowPartial;
    private IconButton allowPartial;

    public RedstoneRequesterScreen(RedstoneRequesterMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        for (int i = 0; i < 9; ++i) {
            this.amounts.add(1);
        }
        List<BigItemStack> stacks = ((RedstoneRequesterBlockEntity)((RedstoneRequesterMenu)this.menu).contentHolder).encodedRequest.stacks();
        for (int i = 0; i < stacks.size(); ++i) {
            this.amounts.set(i, Math.max(1, stacks.get((int)i).count));
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.addressBox.tick();
        for (int i = 0; i < this.amounts.size(); ++i) {
            if (!((RedstoneRequesterMenu)this.menu).ghostInventory.getStackInSlot(i).isEmpty()) continue;
            this.amounts.set(i, 1);
        }
    }

    @Override
    protected void init() {
        int bgHeight = AllGuiTextures.REDSTONE_REQUESTER.getHeight();
        int bgWidth = AllGuiTextures.REDSTONE_REQUESTER.getWidth();
        this.setWindowSize(bgWidth, bgHeight + AllGuiTextures.PLAYER_INVENTORY.getHeight());
        super.init();
        this.clearWidgets();
        int x = this.getGuiLeft();
        int y = this.getGuiTop();
        if (this.addressBox == null) {
            this.addressBox = new AddressEditBox((Screen)this, new NoShadowFontWrapper(this.font), x + 55, y + 68, 110, 10, false);
            this.addressBox.setValue(((RedstoneRequesterBlockEntity)((RedstoneRequesterMenu)this.menu).contentHolder).encodedTargetAdress);
            this.addressBox.setTextColor(0x555555);
        }
        this.addRenderableWidget((GuiEventListener)this.addressBox);
        this.confirmButton = new IconButton(x + bgWidth - 30, y + bgHeight - 25, AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(() -> this.minecraft.player.closeContainer());
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.allowPartial = new IconButton(x + 12, y + bgHeight - 25, AllIcons.I_PARTIAL_REQUESTS);
        this.allowPartial.withCallback(() -> {
            this.allowPartial.green = true;
            this.dontAllowPartial.green = false;
        });
        this.allowPartial.green = ((RedstoneRequesterBlockEntity)((RedstoneRequesterMenu)this.menu).contentHolder).allowPartialRequests;
        this.allowPartial.setToolTip((Component)CreateLang.translate("gui.redstone_requester.allow_partial", new Object[0]).component());
        this.addRenderableWidget((GuiEventListener)this.allowPartial);
        this.dontAllowPartial = new IconButton(x + 12 + 18, y + bgHeight - 25, AllIcons.I_FULL_REQUESTS);
        this.dontAllowPartial.withCallback(() -> {
            this.allowPartial.green = false;
            this.dontAllowPartial.green = true;
        });
        this.dontAllowPartial.green = !((RedstoneRequesterBlockEntity)((RedstoneRequesterMenu)this.menu).contentHolder).allowPartialRequests;
        this.dontAllowPartial.setToolTip((Component)CreateLang.translate("gui.redstone_requester.dont_allow_partial", new Object[0]).component());
        this.addRenderableWidget((GuiEventListener)this.dontAllowPartial);
        this.extraAreas = List.of(new Rect2i(x + bgWidth, y + bgHeight - 50, 70, 60));
    }

    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = this.getGuiLeft();
        int y = this.getGuiTop();
        AllGuiTextures.REDSTONE_REQUESTER.render(pGuiGraphics, x + 3, y);
        this.renderPlayerInventory(pGuiGraphics, x - 3, y + 124);
        ItemStack stack = AllBlocks.REDSTONE_REQUESTER.asStack();
        MutableComponent title = CreateLang.text(stack.getHoverName().getString()).component();
        pGuiGraphics.drawString(this.font, (Component)title, x + 117 - this.font.width((FormattedText)title) / 2, y + 4, 4013128, false);
        GuiGameElement.of((ItemStack)stack).scale(3.0).render(pGuiGraphics, x + 245, y + 80);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderForeground(graphics, mouseX, mouseY, partialTicks);
        int x = this.getGuiLeft();
        int y = this.getGuiTop();
        for (int i = 0; i < this.amounts.size(); ++i) {
            int inputX = x + 27 + i * 20;
            int inputY = y + 28;
            ItemStack itemStack = ((RedstoneRequesterMenu)this.menu).ghostInventory.getStackInSlot(i);
            if (itemStack.isEmpty()) continue;
            PoseStack ms = graphics.pose();
            ms.pushPose();
            ms.translate(0.0f, 0.0f, 100.0f);
            graphics.renderItemDecorations(this.font, itemStack, inputX, inputY, String.valueOf(this.amounts.get(i)));
            ms.popPose();
        }
        if (this.addressBox.isHovered() && !this.addressBox.isFocused()) {
            if (this.addressBox.getValue().isBlank()) {
                graphics.renderComponentTooltip(this.font, List.of(CreateLang.translate("gui.redstone_requester.requester_address", new Object[0]).color(ScrollInput.HEADER_RGB).component(), CreateLang.translate("gui.redstone_requester.requester_address_tip", new Object[0]).style(ChatFormatting.GRAY).component(), CreateLang.translate("gui.redstone_requester.requester_address_tip_1", new Object[0]).style(ChatFormatting.GRAY).component(), CreateLang.translate("gui.schedule.lmb_edit", new Object[0]).style(ChatFormatting.DARK_GRAY).style(ChatFormatting.ITALIC).component()), mouseX, mouseY);
            } else {
                graphics.renderComponentTooltip(this.font, List.of(CreateLang.translate("gui.redstone_requester.requester_address_given", new Object[0]).color(ScrollInput.HEADER_RGB).component(), CreateLang.text("'" + this.addressBox.getValue() + "'").style(ChatFormatting.GRAY).component()), mouseX, mouseY);
            }
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int x = this.getGuiLeft();
        int y = this.getGuiTop();
        if (this.addressBox.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }
        for (int i = 0; i < this.amounts.size(); ++i) {
            int inputX = x + 27 + i * 20;
            int inputY = y + 28;
            if (!(mouseX >= (double)inputX) || !(mouseX < (double)(inputX + 16)) || !(mouseY >= (double)inputY) || !(mouseY < (double)(inputY + 16))) continue;
            ItemStack itemStack = ((RedstoneRequesterMenu)this.menu).ghostInventory.getStackInSlot(i);
            if (itemStack.isEmpty()) {
                return true;
            }
            this.amounts.set(i, Mth.clamp((int)((int)((double)this.amounts.get(i).intValue() + Math.signum(scrollY) * (double)(RedstoneRequesterScreen.hasShiftDown() ? 10 : 1))), (int)1, (int)256));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    protected List<Component> getTooltipFromContainerItem(ItemStack pStack) {
        List tooltip = super.getTooltipFromContainerItem(pStack);
        if (!(this.hoveredSlot instanceof SlotItemHandler)) {
            return tooltip;
        }
        int slotIndex = this.hoveredSlot.getSlotIndex();
        if (slotIndex >= this.amounts.size()) {
            return tooltip;
        }
        return List.of(CreateLang.translate("gui.factory_panel.send_item", CreateLang.itemName(pStack).add(CreateLang.text(" x" + String.valueOf(this.amounts.get(slotIndex))))).color(ScrollInput.HEADER_RGB).component(), CreateLang.translate("gui.factory_panel.scroll_to_change_amount", new Object[0]).style(ChatFormatting.DARK_GRAY).style(ChatFormatting.ITALIC).component(), CreateLang.translate("gui.scrollInput.shiftScrollsFaster", new Object[0]).style(ChatFormatting.DARK_GRAY).style(ChatFormatting.ITALIC).component());
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return this.extraAreas;
    }

    public void removed() {
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new RedstoneRequesterConfigurationPacket(((RedstoneRequesterBlockEntity)((RedstoneRequesterMenu)this.menu).contentHolder).getBlockPos(), this.addressBox.getValue(), this.allowPartial.green, this.amounts));
        super.removed();
    }
}
