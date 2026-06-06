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

    // simulated:textures/gui/linked_typewriter/linked_typewriter.png — 256×256 sheet
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "simulated", "textures/gui/linked_typewriter/linked_typewriter.png");

    // LINKED_TYPEWRITER_KEY_MODIFICATION_MENU: UV(0,145) 214×80
    private static final int PANEL_U = 0;
    private static final int PANEL_V = 145;
    private static final int PANEL_W = 214;
    private static final int PANEL_H = 80;

    // Player inventory background is 176×108; rendered at (leftPos+19, topPos+72).
    // Extends below the panel — getExtraAreas() covers that region.
    private static final int INV_X = 19;
    private static final int INV_Y = 72;

    // Button x = panel_origin + BIND.width - offset  (BIND.width = 212, from SimGUITextures)
    // confirm: 212 - 56 = 156;  trash: 212 - 33 = 179
    // Button y = topPos+1  (Aeronautics: widgetHeight = getCenterHeight()+32 = (topPos-31)+32 = topPos+1)
    private static final int BTN_Y         = 1;
    private static final int BTN_CONFIRM_X = 212 - 56; // 156
    private static final int BTN_TRASH_X   = 212 - 33; // 179

    private IconButton confirmButton;
    private IconButton trashButton;
    private Rect2i inventoryArea = new Rect2i(0, 0, 0, 0);

    public AnalogControllerScreen(AnalogControllerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        // Window height must cover the full panel + inventory area so topPos centers correctly.
        // Panel is 80px, but inventory renders at INV_Y=72 and is 108px tall → total = 72+108 = 180px.
        // Using PANEL_H (80) here would center only the panel, pushing the inventory off-screen.
        setWindowSize(PANEL_W, INV_Y + 108);
        super.init();

        menu.slotsActive = true;

        confirmButton = new IconButton(leftPos + BTN_CONFIRM_X, topPos + BTN_Y, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> minecraft.player.closeContainer());
        addRenderableWidget(confirmButton);

        trashButton = new IconButton(leftPos + BTN_TRASH_X, topPos + BTN_Y, AllIcons.I_TRASH);
        trashButton.withCallback(this::clearFrequency);
        addRenderableWidget(trashButton);

        // Full inventory area for getExtraAreas(): 9 cols × 18 = 162 wide, (3 rows + hotbar) = 76 tall
        // but PLAYER_INVENTORY texture is 176×108, so report the full texture rect.
        inventoryArea = new Rect2i(leftPos + INV_X, topPos + INV_Y, 176, 108);
    }

    private void clearFrequency() {
        menu.ghostInventory.setStackInSlot(0, ItemStack.EMPTY);
        menu.ghostInventory.setStackInSlot(1, ItemStack.EMPTY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        // LINKED_TYPEWRITER_KEY_MODIFICATION_MENU: UV(0,145) 214×80, sheet 256×256
        graphics.blit(TEXTURE, leftPos, topPos, PANEL_U, PANEL_V, PANEL_W, PANEL_H, 256, 256);

        // Player inventory background — same relative offsets as EntryModifierScreen.renderBG()
        // adjusted for our panel being at (leftPos+0, topPos+0) instead of (leftPos+11, topPos-31)
        renderPlayerInventory(graphics, leftPos + INV_X, topPos + INV_Y);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // No labels — title is part of the texture
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return List.of(inventoryArea);
    }
}