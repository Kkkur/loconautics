package com.lycoris.loconautics.content.analogcontroller;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.lycoris.loconautics.core.LoconauticsConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Client-side frequency screen for the Analog Controller.
 *
 * Renders an 81×31 px panel from the mod texture atlas.  The panel contains:
 *   - A red ghost slot  at panel (2, 6)  — frequency first
 *   - A blue ghost slot at panel (20, 6) — frequency second
 *   - A confirm button  at panel (47, 6)
 *
 * No player inventory is shown (this is a tiny "quick config" overlay).
 */
public class AnalogControllerScreen extends AbstractSimiContainerScreen<AnalogControllerMenu> {

    private static final ResourceLocation TEXTURE =
            LoconauticsConstants.id("textures/gui/analog_controller.png");

    /** Panel dimensions as laid out in the 256×256 atlas. */
    private static final int PANEL_W = 81;
    private static final int PANEL_H = 31;

    private IconButton confirmButton;

    public AnalogControllerScreen(AnalogControllerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    // ------------------------------------------------------------------ lifecycle

    @Override
    protected void init() {
        setWindowSize(PANEL_W, PANEL_H);
        super.init();

        // Confirm button — uses Create's standard confirm icon, positioned over the
        // grey square at panel-relative (47, 6).
        confirmButton = new IconButton(leftPos + 47, topPos + 6, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> minecraft.player.closeContainer());
        addRenderableWidget(confirmButton);
    }

    // ------------------------------------------------------------------ rendering

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        // Blit the 81×31 panel from the top-left corner of the 256×256 atlas.
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, PANEL_W, PANEL_H, 256, 256);
    }

    /**
     * Suppress all label rendering — the panel has no room for title / inventory text.
     */
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // intentionally empty
    }
}