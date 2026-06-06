package com.lycoris.loconautics.content.analogcontroller;

import com.lycoris.loconautics.core.LoconauticsConstants;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Frequency config screen — opened via Shift+right-click.
 *
 * Small 81×31 panel with two ghost frequency slots and a confirm button.
 * The actual HUD (bars, power number, lock icon) is rendered by
 * {@link AnalogControllerHUD} as a LayeredDraw overlay, not here.
 */
public class AnalogControllerScreen extends AbstractSimiContainerScreen<AnalogControllerMenu> {

    private static final ResourceLocation TEXTURE =
            LoconauticsConstants.id("textures/gui/analog_controller.png");

    private static final int PANEL_W = 81;
    private static final int PANEL_H = 31;

    private IconButton confirmButton;

    public AnalogControllerScreen(AnalogControllerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        setWindowSize(PANEL_W, PANEL_H);
        super.init();
        confirmButton = new IconButton(leftPos + 47, topPos + 6, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> minecraft.player.closeContainer());
        addRenderableWidget(confirmButton);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, PANEL_W, PANEL_H, 256, 256);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // No labels — panel has no room for title text
    }
}