package com.lycoris.loconautics.content.analogcontroller;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Frequency config screen — opened via Shift+right-click.
 *
 * Uses the LINKED_TYPEWRITER_BIND panel from Aeronautics as background,
 * mirroring how EntryModifierScreen renders the bind submenu inside the typewriter.
 *
 * Background : simulated:textures/gui/linked_typewriter/linked_typewriter.png
 * UV region  : LINKED_TYPEWRITER_BIND — startX=0, startY=154, width=212, height=89
 *
 * Layout mirrors EntryModifierScreen.renderBG():
 *   - MODIFICATION_MENU panel rendered at (leftPos, topPos)
 *   - Player inventory rendered at (leftPos + 19, topPos + 72)
 *   - Confirm button at (leftPos + PANEL_W - 56, topPos + 32)
 *   - Trash button  at (leftPos + PANEL_W - 33, topPos + 32)
 */
public class AnalogControllerScreen extends AbstractSimiContainerScreen<AnalogControllerMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "simulated", "textures/gui/linked_typewriter/linked_typewriter.png");

    // LINKED_TYPEWRITER_KEY_MODIFICATION_MENU — the outer grey panel, (0,145) 214×80
    private static final int BG_U = 0;
    private static final int BG_V = 145;
    private static final int BG_W = 214;
    private static final int BG_H = 80;

    // LINKED_TYPEWRITER_BIND — the inner bind strip, (0,154) 212×89
    private static final int BIND_U = 0;
    private static final int BIND_V = 154;
    private static final int BIND_W = 212;
    private static final int BIND_H = 89;

    // Player inventory render offset relative to leftPos/topPos — mirrors EntryModifierScreen
    private static final int INV_OFFSET_X = 19;
    private static final int INV_OFFSET_Y = 72;

    private IconButton confirmButton;
    private IconButton trashButton;

    // Inventory bubble area for getExtraAreas()
    private Rect2i inventoryArea = new Rect2i(0, 0, 0, 0);

    public AnalogControllerScreen(AnalogControllerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        setWindowSize(BG_W, BG_H);
        super.init();

        // Enable player slot interaction — mirrors EntryModifierScreen.startModifying()
        menu.slotsActive = true;

        // Confirm button — mirrors EntryModifierScreen.resetXYPositions()
        // x = leftPos + BIND_W - 56, y = topPos + 32 (vertical centre of the bind strip)
        confirmButton = new IconButton(leftPos + BIND_W - 56, topPos + 32, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> minecraft.player.closeContainer());
        addRenderableWidget(confirmButton);

        // Trash button — single click clears both slots, mirrors typewriter's clear path
        // x = leftPos + BIND_W - 33
        trashButton = new IconButton(leftPos + BIND_W - 33, topPos + 32, AllIcons.I_TRASH);
        trashButton.withCallback(this::clearFrequency);
        addRenderableWidget(trashButton);

        // Record inventory bubble for getExtraAreas() so Create doesn't occlude it
        // 9 slots × 18 = 162px wide, 4 rows × 18 = 72px tall (3 rows + hotbar)
        inventoryArea = new Rect2i(leftPos + INV_OFFSET_X, topPos + INV_OFFSET_Y, 162, 76);
    }

    private void clearFrequency() {
        menu.ghostInventory.setStackInSlot(0, ItemStack.EMPTY);
        menu.ghostInventory.setStackInSlot(1, ItemStack.EMPTY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        // Outer grey modification panel
        graphics.blit(TEXTURE, leftPos, topPos, BG_U, BG_V, BG_W, BG_H, 256, 256);

        // Inner bind strip overlaid on top — mirrors EntryModifierScreen.renderBG()
        graphics.blit(TEXTURE, leftPos, topPos, BIND_U, BIND_V, BIND_W, BIND_H, 256, 256);

        // Player inventory — mirrors EntryModifierScreen.renderBG() call to renderPlayerInventory()
        renderPlayerInventory(graphics, leftPos + INV_OFFSET_X, topPos + INV_OFFSET_Y);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // No labels
    }

    /**
     * Tell Create about the inventory bubble so it isn't occluded.
     * Mirrors LinkedTypewriterScreen.getExtraAreas().
     */
    @Override
    public List<Rect2i> getExtraAreas() {
        return List.of(inventoryArea);
    }
}