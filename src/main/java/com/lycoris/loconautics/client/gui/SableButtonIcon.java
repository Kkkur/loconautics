package com.lycoris.loconautics.client.gui;

import com.lycoris.loconautics.core.LoconauticsConstants;

import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * Custom {@link ScreenElement} icon for the "Assemble as Sable Train" button in the station assembly screen.
 * Blits {@code assets/loconautics/textures/gui/sable_button.png} (a 24×16 sprite, sized to match Create's own
 * {@code I_ASSEMBLE_TRAIN} icon) the same way {@code AllIcons}/{@code AllGuiTextures} render their icons.
 */
public final class SableButtonIcon implements ScreenElement {

    public static final SableButtonIcon INSTANCE = new SableButtonIcon();

    private static final ResourceLocation TEXTURE = LoconauticsConstants.id("textures/gui/sable_button.png");
    private static final int WIDTH = 24;
    private static final int HEIGHT = 16;

    private SableButtonIcon() {
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(TEXTURE, x, y, 0, 0.0F, 0.0F, WIDTH, HEIGHT, WIDTH, HEIGHT);
    }
}
