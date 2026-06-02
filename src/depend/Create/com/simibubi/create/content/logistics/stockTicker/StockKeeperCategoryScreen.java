/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.GuiGameElement$GuiRenderBuilder
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.SlotItemHandler
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperCategoryEditPacket;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperCategoryMenu;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperCategoryRefundPacket;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.menu.GhostItemSubmitPacket;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class StockKeeperCategoryScreen
extends AbstractSimiContainerScreen<StockKeeperCategoryMenu> {
    private static final int CARD_HEADER = 20;
    private static final int CARD_WIDTH = 160;
    private List<Rect2i> extraAreas = Collections.emptyList();
    private final LerpedFloat scroll = LerpedFloat.linear().startWithValue(0.0);
    private final List<ItemStack> schedule;
    private IconButton confirmButton;
    private ItemStack editingItem;
    private int editingIndex;
    private IconButton editorConfirm;
    private EditBox editorEditBox;
    final int slices = 4;
    private final Component clickToEdit = CreateLang.translateDirect("gui.schedule.lmb_edit", new Object[0]).withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC});

    public StockKeeperCategoryScreen(StockKeeperCategoryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.schedule = new ArrayList<ItemStack>(((StockTickerBlockEntity)menu.contentHolder).categories);
        menu.slotsActive = false;
    }

    @Override
    protected void init() {
        AllGuiTextures bg = AllGuiTextures.STOCK_KEEPER_CATEGORY;
        this.setWindowSize(bg.getWidth(), bg.getHeight() * 4 + AllGuiTextures.STOCK_KEEPER_CATEGORY_HEADER.getHeight() + AllGuiTextures.STOCK_KEEPER_CATEGORY_FOOTER.getHeight());
        super.init();
        this.clearWidgets();
        this.confirmButton = new IconButton(this.leftPos + bg.getWidth() - 25, this.topPos + this.imageHeight - 25, AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(() -> this.minecraft.player.closeContainer());
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.stopEditing();
        this.extraAreas = ImmutableList.of((Object)new Rect2i(this.leftPos + bg.getWidth(), this.topPos + this.imageHeight - 40, 48, 40));
    }

    protected void startEditing(int index) {
        this.confirmButton.visible = false;
        this.editorConfirm = new IconButton(this.leftPos + 36 + 131, this.topPos + 59, AllIcons.I_CONFIRM);
        ((StockKeeperCategoryMenu)this.menu).slotsActive = true;
        this.editorEditBox = new EditBox(this.font, this.leftPos + 47, this.topPos + 28, 124, 10, (Component)Component.empty());
        this.editorEditBox.setTextColor(-1118482);
        this.editorEditBox.setBordered(false);
        this.editorEditBox.setFocused(false);
        this.editorEditBox.mouseClicked(0.0, 0.0, 0);
        this.editorEditBox.setMaxLength(28);
        this.editorEditBox.setValue(index == -1 || this.schedule.get(index).isEmpty() ? CreateLang.translate("gui.stock_ticker.new_category", new Object[0]).string() : this.schedule.get(index).getHoverName().getString());
        this.editingIndex = index;
        this.editingItem = index == -1 ? ItemStack.EMPTY : this.schedule.get(index);
        ((StockKeeperCategoryMenu)this.menu).proxyInventory.setStackInSlot(0, this.editingItem);
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new GhostItemSubmitPacket(this.editingItem, 0));
        this.addRenderableWidget((GuiEventListener)this.editorConfirm);
        this.addRenderableWidget((GuiEventListener)this.editorEditBox);
    }

    protected void stopEditing() {
        this.confirmButton.visible = true;
        if (this.editingItem == null) {
            return;
        }
        this.playUiSound((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
        this.removeWidget((GuiEventListener)this.editorConfirm);
        this.removeWidget((GuiEventListener)this.editorEditBox);
        ItemStack stackInSlot = ((StockKeeperCategoryMenu)this.menu).proxyInventory.getStackInSlot(0).copy();
        boolean empty = stackInSlot.isEmpty();
        if (empty && this.editingIndex != -1) {
            this.schedule.remove(this.editingIndex);
        }
        if (!empty) {
            String value = this.editorEditBox.getValue();
            stackInSlot.set(DataComponents.CUSTOM_NAME, (Object)(value.isBlank() ? null : Component.literal((String)value)));
            if (this.editingIndex == -1) {
                this.schedule.add(stackInSlot);
            } else {
                this.schedule.set(this.editingIndex, stackInSlot);
            }
        }
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new GhostItemSubmitPacket(ItemStack.EMPTY, 0));
        this.editingItem = null;
        this.editorConfirm = null;
        this.editorEditBox = null;
        ((StockKeeperCategoryMenu)this.menu).slotsActive = false;
        this.init();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.scroll.tickChaser();
        if (this.editorEditBox == null) {
            return;
        }
        if (!this.editorEditBox.getValue().equals(CreateLang.translate("gui.stock_ticker.new_category", new Object[0]).string())) {
            return;
        }
        if (((StockKeeperCategoryMenu)this.menu).proxyInventory.getStackInSlot(0).has(DataComponents.CUSTOM_NAME)) {
            this.editorEditBox.setValue(((StockKeeperCategoryMenu)this.menu).proxyInventory.getStackInSlot(0).getHoverName().getString());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        partialTicks = AnimationTickHolder.getPartialTicksUI();
        if (((StockKeeperCategoryMenu)this.menu).slotsActive) {
            super.render(graphics, mouseX, mouseY, partialTicks);
        } else {
            this.renderBackground(graphics, mouseX, mouseY, partialTicks);
            this.renderBg(graphics, partialTicks, mouseX, mouseY);
            for (Renderable widget : this.renderables) {
                widget.render(graphics, mouseX, mouseY, partialTicks);
            }
            this.renderForeground(graphics, mouseX, mouseY, partialTicks);
        }
    }

    protected void renderCategories(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack matrixStack = graphics.pose();
        int yOffset = 25;
        List<ItemStack> entries = this.schedule;
        float scrollOffset = -this.scroll.getValue(partialTicks);
        graphics.enableScissor(this.leftPos + 3, this.topPos + 16, this.leftPos + 187, this.topPos + 19 + AllGuiTextures.STOCK_KEEPER_CATEGORY.getHeight() * 4);
        for (int i = 0; i <= entries.size(); ++i) {
            matrixStack.pushPose();
            matrixStack.translate(0.0f, scrollOffset, 0.0f);
            if (i == entries.size()) {
                AllGuiTextures.STOCK_KEEPER_CATEGORY_NEW.render(graphics, this.leftPos + 7, this.topPos + yOffset);
                matrixStack.popPose();
                break;
            }
            ItemStack scheduleEntry = entries.get(i);
            int cardY = yOffset;
            int cardHeight = this.renderScheduleEntry(graphics, i, scheduleEntry, cardY, mouseX, mouseY, partialTicks);
            yOffset += cardHeight;
            matrixStack.popPose();
        }
        graphics.disableScissor();
    }

    public int renderScheduleEntry(GuiGraphics graphics, int i, ItemStack entry, int yOffset, int mouseX, int mouseY, float partialTicks) {
        int cardHeader;
        int cardWidth = 160;
        int cardHeight = cardHeader = 20;
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float)(this.leftPos + 7), (float)(this.topPos + yOffset), 0.0f);
        AllGuiTextures.STOCK_KEEPER_CATEGORY_ENTRY.render(graphics, 0, 0);
        if (i > 0) {
            AllGuiTextures.STOCK_KEEPER_CATEGORY_UP.render(graphics, cardWidth + 12, cardHeader - 18);
        }
        if (i < this.schedule.size() - 1) {
            AllGuiTextures.STOCK_KEEPER_CATEGORY_DOWN.render(graphics, cardWidth + 12, cardHeader - 9);
        }
        graphics.renderItem(entry, 14, 1);
        graphics.drawString(this.font, (String)(entry.isEmpty() ? CreateLang.translate("gui.stock_ticker.empty_category_name_placeholder", new Object[0]).string() : entry.getHoverName().getString(20).stripTrailing() + (entry.getHoverName().getString().length() > 20 ? "..." : "")), 35, 5, 0x656565, false);
        matrixStack.popPose();
        return cardHeight;
    }

    public boolean action(@Nullable GuiGraphics graphics, double mouseX, double mouseY, int click) {
        if (mouseX < (double)this.leftPos || mouseX >= (double)(this.leftPos + this.imageWidth) || mouseY < (double)(this.topPos + 15) || mouseY >= (double)(this.topPos + 99)) {
            return false;
        }
        if (this.editingItem != null) {
            return false;
        }
        int mx = (int)mouseX;
        int my = (int)mouseY;
        int x = mx - this.leftPos - 20;
        int y = my - this.topPos - 24;
        if (x < 0 || x >= 196) {
            return false;
        }
        if (y < 0 || y >= 143) {
            return false;
        }
        y = (int)((float)y + this.scroll.getValue(0.0f));
        List<ItemStack> entries = this.schedule;
        for (int i = 0; i < entries.size(); ++i) {
            ItemStack entry = entries.get(i);
            int cardHeight = 20;
            if (y >= cardHeight) {
                if ((y -= cardHeight) >= 0) continue;
                return false;
            }
            int fieldSize = 140;
            if (x > 0 && x <= fieldSize && y > 0 && y <= 16) {
                ArrayList<Component> components = new ArrayList<Component>();
                components.add((Component)(entry.isEmpty() ? CreateLang.translate("gui.stock_ticker.empty_category_name_placeholder", new Object[0]).component() : entry.getHoverName()));
                components.add(this.clickToEdit);
                this.renderActionTooltip(graphics, components, mx, my);
                if (click == 0) {
                    this.startEditing(i);
                }
                return true;
            }
            if (x > fieldSize && x <= fieldSize + 16 && y > 0 && y <= 16) {
                this.renderActionTooltip(graphics, (List<Component>)ImmutableList.of((Object)CreateLang.translate("gui.stock_ticker.delete_category", new Object[0]).component()), mx, my);
                if (click == 0) {
                    if (!entry.isEmpty()) {
                        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new StockKeeperCategoryRefundPacket(((StockTickerBlockEntity)((StockKeeperCategoryMenu)this.menu).contentHolder).getBlockPos(), entry));
                    }
                    entries.remove(entry);
                    this.init();
                }
                return true;
            }
            if (x > 158 && x < 170) {
                if (y > 2 && y <= 10 && i > 0) {
                    this.renderActionTooltip(graphics, (List<Component>)ImmutableList.of((Object)CreateLang.translateDirect("gui.schedule.move_up", new Object[0]), (Object)CreateLang.translate("gui.stock_ticker.shift_moves_top", new Object[0]).style(ChatFormatting.DARK_GRAY).style(ChatFormatting.ITALIC).component()), mx, my);
                    if (click == 0) {
                        entries.remove(entry);
                        entries.add(StockKeeperCategoryScreen.hasShiftDown() ? 0 : i - 1, entry);
                        this.init();
                    }
                    return true;
                }
                if (y > 10 && y <= 22 && i < entries.size() - 1) {
                    this.renderActionTooltip(graphics, (List<Component>)ImmutableList.of((Object)CreateLang.translateDirect("gui.schedule.move_down", new Object[0]), (Object)CreateLang.translate("gui.stock_ticker.shift_moves_bottom", new Object[0]).style(ChatFormatting.DARK_GRAY).style(ChatFormatting.ITALIC).component()), mx, my);
                    if (click == 0) {
                        entries.remove(entry);
                        entries.add(StockKeeperCategoryScreen.hasShiftDown() ? entries.size() : i + 1, entry);
                        this.init();
                    }
                    return true;
                }
            }
            if ((x -= 18) >= 0 && (y -= 28) >= 0 && x <= 160) continue;
            return false;
        }
        if (x > 0 && x <= 16 && y > 0 && y <= 16) {
            this.renderActionTooltip(graphics, (List<Component>)ImmutableList.of((Object)CreateLang.translate("gui.stock_ticker.new_category", new Object[0]).component()), mx, my);
            if (click == 0) {
                this.playUiSound((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
                this.startEditing(-1);
            }
        }
        return false;
    }

    private void renderActionTooltip(@Nullable GuiGraphics graphics, List<Component> tooltip, int mx, int my) {
        if (graphics != null) {
            graphics.renderTooltip(this.font, tooltip, Optional.empty(), mx, my);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.editorConfirm != null && this.editorConfirm.isMouseOver(pMouseX, pMouseY)) {
            this.stopEditing();
            return true;
        }
        if (this.action(null, pMouseX, pMouseY, pButton)) {
            this.playUiSound((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
            return true;
        }
        boolean wasNotFocused = this.editorEditBox != null && !this.editorEditBox.isFocused();
        boolean mouseClicked = super.mouseClicked(pMouseX, pMouseY, pButton);
        if (this.editorEditBox != null && this.editorEditBox.isMouseOver(pMouseX, pMouseY) && wasNotFocused) {
            this.editorEditBox.moveCursorToEnd(false);
            this.editorEditBox.setHighlightPos(0);
        }
        return mouseClicked;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        boolean hitE;
        if (this.editingItem == null) {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
        InputConstants.Key mouseKey = InputConstants.getKey((int)pKeyCode, (int)pScanCode);
        boolean hitEscape = pKeyCode == 256;
        boolean hitEnter = this.getFocused() instanceof EditBox && (pKeyCode == 257 || pKeyCode == 335);
        boolean bl = hitE = this.getFocused() == null && this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey);
        if (hitE || hitEnter || hitEscape) {
            this.stopEditing();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.editingItem != null) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        float chaseTarget = this.scroll.getChaseTarget();
        float max = 40 - (3 + AllGuiTextures.STOCK_KEEPER_CATEGORY.getHeight() * 4);
        if ((max += (float)(this.schedule.size() * 20 + 24)) > 0.0f) {
            chaseTarget -= (float)(scrollY * 12.0);
            chaseTarget = Mth.clamp((float)chaseTarget, (float)0.0f, (float)max);
            this.scroll.chase((double)((int)chaseTarget), (double)0.7f, LerpedFloat.Chaser.EXP);
        } else {
            this.scroll.chase(0.0, (double)0.7f, LerpedFloat.Chaser.EXP);
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderForeground(graphics, mouseX, mouseY, partialTicks);
        ((GuiGameElement.GuiRenderBuilder)GuiGameElement.of((ItemStack)AllBlocks.STOCK_TICKER.asStack()).at((float)(this.leftPos + AllGuiTextures.STOCK_KEEPER_CATEGORY.getWidth() + 12), (float)(this.topPos + this.imageHeight - 39), -190.0f)).scale(3.0).render(graphics);
        this.action(graphics, mouseX, mouseY, -1);
        if (this.editingItem == null) {
            return;
        }
        if (this.hoveredSlot instanceof SlotItemHandler && this.hoveredSlot.getItem().isEmpty()) {
            graphics.renderComponentTooltip(this.font, List.of(CreateLang.translate("gui.stock_ticker.category_filter", new Object[0]).color(ScrollInput.HEADER_RGB).component(), CreateLang.translate("gui.stock_ticker.category_filter_tip", new Object[0]).style(ChatFormatting.GRAY).component(), CreateLang.translate("gui.stock_ticker.category_filter_tip_1", new Object[0]).style(ChatFormatting.GRAY).component()), mouseX, mouseY);
        }
        if (this.editorEditBox != null && this.editorEditBox.isHovered() && !this.editorEditBox.isFocused()) {
            graphics.renderComponentTooltip(this.font, List.of(CreateLang.translate("gui.stock_ticker.category_name", new Object[0]).color(ScrollInput.HEADER_RGB).component(), this.clickToEdit), mouseX, mouseY);
        }
    }

    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
        int y = this.topPos;
        AllGuiTextures.STOCK_KEEPER_CATEGORY_HEADER.render(graphics, this.leftPos, y);
        y += AllGuiTextures.STOCK_KEEPER_CATEGORY_HEADER.getHeight();
        for (int i = 0; i < 4; ++i) {
            AllGuiTextures.STOCK_KEEPER_CATEGORY.render(graphics, this.leftPos, y);
            y += AllGuiTextures.STOCK_KEEPER_CATEGORY.getHeight();
        }
        AllGuiTextures.STOCK_KEEPER_CATEGORY_FOOTER.render(graphics, this.leftPos, y);
        AllGuiTextures.STOCK_KEEPER_CATEGORY_SAYS.render(graphics, this.leftPos + this.imageWidth - 6, y + 7);
        FormattedCharSequence formattedcharsequence = ((StockTickerBlockEntity)((StockKeeperCategoryMenu)this.menu).contentHolder).getBlockState().getBlock().getName().getVisualOrderText();
        int center = this.leftPos + AllGuiTextures.STOCK_KEEPER_CATEGORY.getWidth() / 2;
        graphics.drawString(this.font, formattedcharsequence, (float)(center - this.font.width(formattedcharsequence) / 2), (float)this.topPos + 4.0f, 4013128, false);
        if (this.editingItem == null) {
            this.renderCategories(graphics, pMouseX, pMouseY, pPartialTick);
            return;
        }
        graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        y = this.topPos - 5;
        AllGuiTextures.STOCK_KEEPER_CATEGORY_HEADER.render(graphics, this.leftPos, y);
        AllGuiTextures.STOCK_KEEPER_CATEGORY_EDIT.render(graphics, this.leftPos, y += AllGuiTextures.STOCK_KEEPER_CATEGORY_HEADER.getHeight());
        AllGuiTextures.STOCK_KEEPER_CATEGORY_FOOTER.render(graphics, this.leftPos, y += AllGuiTextures.STOCK_KEEPER_CATEGORY_EDIT.getHeight());
        this.renderPlayerInventory(graphics, this.leftPos + 10, this.topPos + 88);
        formattedcharsequence = CreateLang.translate("gui.stock_ticker.category_editor", new Object[0]).component().getVisualOrderText();
        graphics.drawString(this.font, formattedcharsequence, (float)(center - this.font.width(formattedcharsequence) / 2), (float)this.topPos - 1.0f, 4013128, false);
    }

    public void removed() {
        super.removed();
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new StockKeeperCategoryEditPacket(((StockTickerBlockEntity)((StockKeeperCategoryMenu)this.menu).contentHolder).getBlockPos(), this.schedule));
    }

    protected List<Component> getTooltipFromContainerItem(ItemStack pStack) {
        List tooltip = super.getTooltipFromContainerItem(pStack);
        if (!(this.hoveredSlot instanceof SlotItemHandler)) {
            return tooltip;
        }
        if (!tooltip.isEmpty()) {
            tooltip.set(0, CreateLang.translate("gui.stock_ticker.category_filter", new Object[0]).color(ScrollInput.HEADER_RGB).component());
        }
        return tooltip;
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return this.extraAreas;
    }

    public Font getFont() {
        return this.font;
    }
}
