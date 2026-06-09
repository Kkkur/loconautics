package com.lycoris.loconautics.foundation.screen;

import com.lycoris.loconautics.foundation.menu.BindFrequencyMenu;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

/**
 * Shared screen for any {@link BindFrequencyMenu} subclass.
 *
 * <p>Renders the {@code frecuency_bind.png} panel with two labelled frequency rows.
 * The row labels are read from {@link BindFrequencyMenu#firstRowLabel} and
 * {@link BindFrequencyMenu#secondRowLabel}, so each block type can supply its own
 * translated or literal text without subclassing this screen.
 *
 * <p>To register this screen for a new menu type, call:
 * <pre>{@code
 * event.register(MY_MENU.get(), BindFrequencyScreen::new);
 * }</pre>
 *
 * @param <T> the concrete {@link BlockEntity} type owned by the menu
 * @param <M> the concrete {@link BindFrequencyMenu} subtype
 */
public class BindFrequencyScreen<T extends BlockEntity, M extends BindFrequencyMenu<T>>
        extends AbstractSimiContainerScreen<M> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "loconautics", "textures/gui/frecuency_bind.png");

    private static final int PANEL_U = 0;
    private static final int PANEL_V = 0;
    private static final int PANEL_W = 214;
    private static final int PANEL_H = 95;

    private static final int INV_X = 19;
    private static final int INV_Y = 100;

    // Button positions are panel-relative (see coordinate derivation in AnalogControllerScreen)
    private static final int BTN_Y         = 60;
    private static final int BTN_CONFIRM_X = 157;
    private static final int BTN_TRASH_X   = 179;

    // Horizontal center of the label area in the texture (grey rectangles span ~x=23..86)
    private static final int LABEL_CENTER_X = 54;
    // Vertical centers of the two rows (panel-relative pixel coords)
    private static final int FIRST_ROW_LABEL_Y  = 40;
    private static final int SECOND_ROW_LABEL_Y = 69;

    private IconButton confirmButton;
    private IconButton trashButton;
    private Rect2i inventoryArea = new Rect2i(0, 0, 0, 0);

    public BindFrequencyScreen(M menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
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
        for (int i = 0; i < 4; i++) {
            menu.ghostInventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, PANEL_U, PANEL_V, PANEL_W, PANEL_H, 214, 127);
        renderPlayerInventory(graphics, leftPos + INV_X, topPos + INV_Y);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        var font = minecraft.font;
        int color = 0xFFFFFFFF;
        int textH = font.lineHeight;

        Component firstLabel  = menu.firstRowLabel;
        Component secondLabel = menu.secondRowLabel;

        graphics.drawString(font, firstLabel,
                LABEL_CENTER_X - font.width(firstLabel) / 2,
                FIRST_ROW_LABEL_Y - textH / 2,
                color, true);

        graphics.drawString(font, secondLabel,
                LABEL_CENTER_X - font.width(secondLabel) / 2,
                SECOND_ROW_LABEL_Y - textH / 2,
                color, true);
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return List.of(inventoryArea);
    }
}