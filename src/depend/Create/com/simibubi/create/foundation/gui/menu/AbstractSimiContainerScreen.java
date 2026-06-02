/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.gui.TickableGuiEventListener
 *  net.createmod.catnip.gui.widget.AbstractSimiWidget
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.foundation.gui.menu;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.TickableGuiEventListener;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
@ParametersAreNonnullByDefault
public abstract class AbstractSimiContainerScreen<T extends AbstractContainerMenu>
extends AbstractContainerScreen<T> {
    protected int windowXOffset;
    protected int windowYOffset;

    public AbstractSimiContainerScreen(T container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    protected void setWindowSize(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
    }

    protected void setWindowOffset(int xOffset, int yOffset) {
        this.windowXOffset = xOffset;
        this.windowYOffset = yOffset;
    }

    protected void init() {
        super.init();
        this.leftPos += this.windowXOffset;
        this.topPos += this.windowYOffset;
    }

    protected void containerTick() {
        for (GuiEventListener listener : this.children()) {
            if (!(listener instanceof TickableGuiEventListener)) continue;
            TickableGuiEventListener tickable = (TickableGuiEventListener)listener;
            tickable.tick();
        }
    }

    protected <W extends GuiEventListener & Renderable> void addRenderableWidgets(W ... widgets) {
        for (W widget : widgets) {
            this.addRenderableWidget((GuiEventListener)widget);
        }
    }

    protected <W extends GuiEventListener & Renderable> void addRenderableWidgets(Collection<W> widgets) {
        for (GuiEventListener widget : widgets) {
            this.addRenderableWidget(widget);
        }
    }

    protected void removeWidgets(GuiEventListener ... widgets) {
        for (GuiEventListener widget : widgets) {
            this.removeWidget(widget);
        }
    }

    protected void removeWidgets(Collection<? extends GuiEventListener> widgets) {
        for (GuiEventListener guiEventListener : widgets) {
            this.removeWidget(guiEventListener);
        }
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        partialTicks = AnimationTickHolder.getPartialTicksUI();
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderForeground(graphics, mouseX, mouseY, partialTicks);
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderTooltip(graphics, mouseX, mouseY);
        for (Renderable widget : this.renderables) {
            List tooltip;
            AbstractSimiWidget simiWidget;
            if (!(widget instanceof AbstractSimiWidget) || !(simiWidget = (AbstractSimiWidget)widget).isMouseOver((double)mouseX, (double)mouseY) || (tooltip = simiWidget.getToolTip()).isEmpty()) continue;
            int ttx = simiWidget.lockedTooltipX == -1 ? mouseX : simiWidget.lockedTooltipX + simiWidget.getX();
            int tty = simiWidget.lockedTooltipY == -1 ? mouseY : simiWidget.lockedTooltipY + simiWidget.getY();
            graphics.renderComponentTooltip(this.font, tooltip, ttx, tty);
        }
    }

    public int getLeftOfCentered(int textureWidth) {
        return this.leftPos - this.windowXOffset + (this.imageWidth - textureWidth) / 2;
    }

    public void renderPlayerInventory(GuiGraphics graphics, int x, int y) {
        AllGuiTextures.PLAYER_INVENTORY.render(graphics, x, y);
        graphics.drawString(this.font, this.playerInventoryTitle, x + 8, y + 6, 0x404040, false);
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.getFocused() instanceof EditBox && pKeyCode != 256) {
            return this.getFocused().keyPressed(pKeyCode, pScanCode, pModifiers);
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.getFocused() != null && !this.getFocused().isMouseOver(pMouseX, pMouseY)) {
            this.setFocused(null);
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public GuiEventListener getFocused() {
        GuiEventListener focused = super.getFocused();
        if (focused instanceof AbstractWidget && !((AbstractWidget)focused).isFocused()) {
            focused = null;
        }
        this.setFocused(focused);
        return focused;
    }

    public List<Rect2i> getExtraAreas() {
        return Collections.emptyList();
    }

    @Deprecated
    protected void debugWindowArea(GuiGraphics graphics) {
        graphics.fill(this.leftPos + this.imageWidth, this.topPos + this.imageHeight, this.leftPos, this.topPos, -741092397);
    }

    @Deprecated
    protected void debugExtraAreas(GuiGraphics graphics) {
        for (Rect2i area : this.getExtraAreas()) {
            graphics.fill(area.getX() + area.getWidth(), area.getY() + area.getHeight(), area.getX(), area.getY(), -741092397);
        }
    }

    protected void playUiSound(SoundEvent sound, float volume, float pitch) {
        Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)sound, (float)pitch, (float)(volume * 0.25f)));
    }
}
