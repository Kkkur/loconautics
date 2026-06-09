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
 * Frequency config screen — opened via Shift+right-click on the Analog Controller.
 *
 * Renders the Aeronautics bind panel exactly as EntryModifierScreen does,
 * but as a standalone screen (our window IS the modification menu).
 *
 * Coordinate derivation from Aeronautics source:
 *   EntryModifierScreen.renderBG():
 *     MODIFICATION_MENU.render(guiGraphics, getCenterWidth(), getCenterHeight())
 *       → getCenterWidth()  = parentScreen.leftPos + 11
 *       → getCenterHeight() = parentScreen.topPos  - 31
 *     renderPlayerInventory(getCenterWidth()+19, getCenterHeight()+72)
 *       → (leftPos+30, topPos+41) in their system
 *
 *   EntryModifierScreen.resetXYPositions():
 *     widgetHeight = getCenterHeight() + 32 = topPos + 1
 *     confirmWidget.x = getCenterWidth() + BIND.width - 56 = leftPos + 11 + 212 - 56 = leftPos + 167
 *     cancelWidget.x  = getCenterWidth() + BIND.width - 33 = leftPos + 11 + 212 - 33 = leftPos + 190
 *
 *   In our standalone screen the panel is at (leftPos+0, topPos+0), so we subtract
 *   their panel origin offsets (+11, -31) from every coordinate:
 *     renderPlayerInventory → (leftPos+0+19, topPos+0+72) = (leftPos+19, topPos+72)
 *     confirmButton → leftPos+(167-11)=leftPos+156 … wait, no:
 *     Their confirmButton is absolute, not relative to the panel.
 *     Their panel at leftPos+11 → our panel at leftPos+0 → shift everything left by 11.
 *     confirmButton.x = leftPos+167 → relative to our leftPos+0: leftPos+(167-11)=leftPos+156? No —
 *     The button positions are screen-absolute. In their screen leftPos is their leftPos.
 *     In our screen leftPos is our leftPos. We add them as offsets in init().
 *     confirmButton: panel_relative_x = 167-11 = 156 → leftPos+156
 *       But BIND.width=212, so the confirm is at panel_origin + 212 - 56 = 0 + 212 - 56 = 156. ✓
 *     trashButton:   panel_relative_x = 190-11 = 179 → leftPos+179. 212-33=179. ✓
 *     widgetHeight (y): topPos+1 → panel_relative_y = 1-(-31) ... no, we remove the -31 offset:
 *       their topPos-31+32 = topPos+1. In our system topPos+0+32 = topPos+32? No:
 *       widgetHeight formula: getCenterHeight()+32 = (topPos-31)+32 = topPos+1.
 *       In our screen: panel_top=topPos+0, so equivalent formula: panelTop+32 = topPos+32.
 *       But wait — the y=1 in their slot coords means topPos+1 (slot y is absolute).
 *       Ghost slots are at panel-relative y=32 (from the menu derivation).
 *       Buttons should be near the slots. Checking:
 *       widgetY = topPos+1 → relative to our topPos+0: topPos+1. So button y = topPos+1.
 */

public class AnalogControllerScreen extends AbstractSimiContainerScreen<AnalogControllerMenu> {

    // Our custom texture: loconautics:textures/gui/frecuency_bind.png — 214×127
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "loconautics", "textures/gui/frecuency_bind.png");

    // Panel region in the texture: full top section, 214×95
    private static final int PANEL_U = 0;
    private static final int PANEL_V = 0;
    private static final int PANEL_W = 214;
    private static final int PANEL_H = 95;

    // Player inventory rendered below the panel
    private static final int INV_X = 19;
    private static final int INV_Y = 100;

    // Buttons sit inside the backward row right-side area (texture pixels x=157..211, y=60..77)
    // confirm at x=157, trash at x=175 (each icon is ~16px wide with 2px gap)
    private static final int BTN_Y         = 60;
    private static final int BTN_CONFIRM_X = 157;
    private static final int BTN_TRASH_X   = 175;

    private IconButton confirmButton;
    private IconButton trashButton;
    private Rect2i inventoryArea = new Rect2i(0, 0, 0, 0);

    public AnalogControllerScreen(AnalogControllerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        // Window height: panel (95px) + gap (5px) + inventory (108px) = 208px
        setWindowSize(PANEL_W, INV_Y + 108);
        super.init();

        menu.slotsActive = true;

        confirmButton = new IconButton(leftPos + BTN_CONFIRM_X, topPos + BTN_Y, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> minecraft.player.closeContainer());
        addRenderableWidget(confirmButton);

        trashButton = new IconButton(leftPos + BTN_TRASH_X, topPos + BTN_Y, AllIcons.I_TRASH);
        trashButton.withCallback(this::clearAllFrequencies);
        addRenderableWidget(trashButton);

        inventoryArea = new Rect2i(leftPos + INV_X, topPos + INV_Y, 176, 108);
    }

    private void clearAllFrequencies() {
        menu.ghostInventory.setStackInSlot(0, ItemStack.EMPTY);
        menu.ghostInventory.setStackInSlot(1, ItemStack.EMPTY);
        menu.ghostInventory.setStackInSlot(2, ItemStack.EMPTY);
        menu.ghostInventory.setStackInSlot(3, ItemStack.EMPTY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        // Draw our custom panel (214×95) from the texture
        graphics.blit(TEXTURE, leftPos, topPos, PANEL_U, PANEL_V, PANEL_W, PANEL_H, 214, 127);

        // Player inventory background below the panel
        renderPlayerInventory(graphics, leftPos + INV_X, topPos + INV_Y);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Labels are baked into the texture
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return List.of(inventoryArea);
    }
}