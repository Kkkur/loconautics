package com.lycoris.loconautics.content.analogcontroller;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import com.lycoris.loconautics.core.LoconauticsConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import org.jetbrains.annotations.NotNull;

/**
 * Client-side frequency selection screen for the Analog Controller.
 *
 * Layout (176 × 96 window):
 *
 *   ┌──────────────────────────────────┐
 *   │   Analog Controller              │
 *   │                                  │
 *   │   [FREQ 1]   [FREQ 2]            │
 *   │  ┌──────┐   ┌──────┐            │
 *   │  │  ░░  │   │  ░░  │            │
 *   │  └──────┘   └──────┘            │
 *   │       Frequency Slots            │
 *   │                          [DONE] │
 *   └──────────────────────────────────┘
 *
 * Players drag items into the two ghost slots to set the two-part frequency,
 * matching Create's Redstone Link frequency convention (pair of items).
 * Clicking DONE closes the screen and fires the saved packet.
 */
public class AnalogControllerScreen extends AbstractContainerScreen<AnalogControllerMenu> {

    // Placeholder: we'd normally have a real texture atlas sheet.
    // Since this is a code stub (assets are separate), we use a simple coloured panel.
    private static final ResourceLocation BG_TEXTURE =
            LoconauticsConstants.id("textures/gui/analog_controller.png");

    private static final int BG_WIDTH = 176;
    private static final int BG_HEIGHT = 96;

    public AnalogControllerScreen(AnalogControllerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = BG_WIDTH;
        this.imageHeight = BG_HEIGHT;
        // Suppress default "Inventory" label
        this.inventoryLabelY = Integer.MIN_VALUE;
    }

    @Override
    protected void init() {
        super.init();

        // Done / confirm button
        addRenderableWidget(
                Button.builder(Component.translatable("gui.done"), btn -> onClose())
                        .pos(leftPos + BG_WIDTH - 54, topPos + BG_HEIGHT - 24)
                        .size(50, 18)
                        .tooltip(Tooltip.create(Component.translatable(
                                "block.loconautics.analog_controller.gui.done_tooltip")))
                        .build()
        );

        // Clear frequency button
        addRenderableWidget(
                Button.builder(Component.literal("✕"), btn -> clearFrequency())
                        .pos(leftPos + 8, topPos + BG_HEIGHT - 24)
                        .size(18, 18)
                        .tooltip(Tooltip.create(Component.translatable(
                                "block.loconautics.analog_controller.gui.clear_tooltip")))
                        .build()
        );
    }

    private void clearFrequency() {
        menu.getSlot(AnalogControllerMenu.SLOT_FREQ_FIRST).set(ItemStack.EMPTY);
        menu.getSlot(AnalogControllerMenu.SLOT_FREQ_SECOND).set(ItemStack.EMPTY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // --- Background panel (drawn as solid colours until the texture is made) ---
        int x = leftPos;
        int y = topPos;

        // Draw dark panel background
        graphics.fill(x, y, x + BG_WIDTH, y + BG_HEIGHT, 0xFF3D3D3D);
        // Inner lighter border
        graphics.fill(x + 1, y + 1, x + BG_WIDTH - 1, y + BG_HEIGHT - 1, 0xFF595959);
        // Top title bar
        graphics.fill(x + 1, y + 1, x + BG_WIDTH - 1, y + 14, 0xFF2A2A2A);

        // --- Frequency slot backgrounds ---
        drawSlotBackground(graphics, x + 43, y + 34);
        drawSlotBackground(graphics, x + 79, y + 34);

        // --- Labels ---
        graphics.drawString(font,
                Component.translatable("block.loconautics.analog_controller.gui.slot1"),
                x + 43, y + 22, 0xFFAAAAAA, false);
        graphics.drawString(font,
                Component.translatable("block.loconautics.analog_controller.gui.slot2"),
                x + 79, y + 22, 0xFFAAAAAA, false);
    }

    private void drawSlotBackground(GuiGraphics graphics, int x, int y) {
        // Standard slot background (dark inset square)
        graphics.fill(x - 1, y - 1, x + 17, y + 17, 0xFF1A1A1A);
        graphics.fill(x, y, x + 16, y + 16, 0xFF333333);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        // Title
        graphics.drawString(font, title,
                leftPos + (BG_WIDTH / 2) - (font.width(title) / 2),
                topPos + 4,
                0xFFDDDDDD, false);

        // Hint text
        Component hint = Component.translatable("block.loconautics.analog_controller.gui.hint");
        graphics.drawString(font, hint,
                leftPos + (BG_WIDTH / 2) - (font.width(hint) / 2),
                topPos + 60,
                0xFF888888, false);
    }

    @Override
    public void onClose() {
        // Screen close triggers menu.removed() on the server → saves frequency.
        super.onClose();
    }
}