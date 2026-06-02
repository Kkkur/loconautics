/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.GuiGameElement$GuiRenderBuilder
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.logistics.filter;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import com.simibubi.create.content.logistics.filter.FilterScreenPacket;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.item.TooltipHelper;
import java.util.Collections;
import java.util.List;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractFilterScreen<F extends AbstractFilterMenu>
extends AbstractSimiContainerScreen<F> {
    protected AllGuiTextures background;
    private List<Rect2i> extraAreas = Collections.emptyList();
    private IconButton resetButton;
    private IconButton confirmButton;

    protected AbstractFilterScreen(F menu, Inventory inv, Component title, AllGuiTextures background) {
        super(menu, inv, title);
        this.background = background;
    }

    @Override
    protected void init() {
        this.setWindowSize(Math.max(this.background.getWidth(), AllGuiTextures.PLAYER_INVENTORY.getWidth()), this.background.getHeight() + 4 + AllGuiTextures.PLAYER_INVENTORY.getHeight());
        super.init();
        int x = this.leftPos;
        int y = this.topPos;
        this.resetButton = new IconButton(x + this.background.getWidth() - 62, y + this.background.getHeight() - 24, AllIcons.I_TRASH);
        this.resetButton.withCallback(() -> {
            ((AbstractFilterMenu)this.menu).clearContents();
            this.contentsCleared();
            ((AbstractFilterMenu)this.menu).sendClearPacket();
        });
        this.confirmButton = new IconButton(x + this.background.getWidth() - 33, y + this.background.getHeight() - 24, AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(() -> this.minecraft.player.closeContainer());
        this.addRenderableWidget((GuiEventListener)this.resetButton);
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.extraAreas = ImmutableList.of((Object)new Rect2i(x + this.background.getWidth(), y + this.background.getHeight() - 40, 80, 48));
    }

    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int invX = this.getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.getWidth());
        int invY = this.topPos + this.background.getHeight() + 4;
        this.renderPlayerInventory(graphics, invX, invY);
        int x = this.leftPos;
        int y = this.topPos;
        this.background.render(graphics, x, y);
        graphics.drawString(this.font, this.title, x + (this.background.getWidth() - 8) / 2 - this.font.width((FormattedText)this.title) / 2, y + 4, this.getTitleColor(), false);
        ((GuiGameElement.GuiRenderBuilder)GuiGameElement.of((ItemStack)((ItemStack)((AbstractFilterMenu)this.menu).contentHolder)).at((float)(x + this.background.getWidth() + 8), (float)(y + this.background.getHeight() - 52), -200.0f)).scale(4.0).render(graphics);
    }

    protected int getTitleColor() {
        return 5841956;
    }

    @Override
    protected void containerTick() {
        if (!ItemStack.matches((ItemStack)((AbstractFilterMenu)this.menu).player.getMainHandItem(), (ItemStack)((ItemStack)((AbstractFilterMenu)this.menu).contentHolder))) {
            ((AbstractFilterMenu)this.menu).player.closeContainer();
        }
        super.containerTick();
        this.handleTooltips();
        this.handleIndicators();
    }

    protected void handleTooltips() {
        List<IconButton> tooltipButtons = this.getTooltipButtons();
        for (IconButton button : tooltipButtons) {
            if (button.getToolTip().isEmpty()) continue;
            button.setToolTip((Component)button.getToolTip().get(0));
            button.getToolTip().add(TooltipHelper.holdShift(FontHelper.Palette.YELLOW, AbstractFilterScreen.hasShiftDown()));
        }
        if (AbstractFilterScreen.hasShiftDown()) {
            List<MutableComponent> tooltipDescriptions = this.getTooltipDescriptions();
            for (int i = 0; i < tooltipButtons.size(); ++i) {
                this.fillToolTip(tooltipButtons.get(i), (Component)tooltipDescriptions.get(i));
            }
        }
    }

    public void handleIndicators() {
        for (IconButton button : this.getTooltipButtons()) {
            button.green = !this.isButtonEnabled(button);
        }
    }

    protected abstract boolean isButtonEnabled(IconButton var1);

    protected List<IconButton> getTooltipButtons() {
        return Collections.emptyList();
    }

    protected List<MutableComponent> getTooltipDescriptions() {
        return Collections.emptyList();
    }

    private void fillToolTip(IconButton button, Component tooltip) {
        if (!button.isHoveredOrFocused()) {
            return;
        }
        List tip = button.getToolTip();
        tip.addAll(TooltipHelper.cutTextComponent(tooltip, FontHelper.Palette.ALL_GRAY));
    }

    protected void contentsCleared() {
    }

    protected void sendOptionUpdate(FilterScreenPacket.Option option) {
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new FilterScreenPacket(option));
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return this.extraAreas;
    }
}
