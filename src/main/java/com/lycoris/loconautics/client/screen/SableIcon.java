package com.lycoris.loconautics.client.screen;

import com.lycoris.loconautics.core.LoconauticsConstants;

import net.createmod.catnip.gui.element.ScreenElement;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * A {@link ScreenElement} that draws a custom 16x16 icon from this mod's textures, so our button
 * can use its own art instead of a Create atlas sprite.
 *
 * <p>The PNG must live at {@code assets/loconautics/textures/gui/sable_button.png} (16x16).
 */
public final class SableIcon implements ScreenElement {

    /** The button icon texture. Provided by the art pass; place the PNG at this path. */
    public static final SableIcon SABLE_BUTTON =
            new SableIcon(LoconauticsConstants.id("textures/gui/sable_button.png"));

    private final ResourceLocation texture;

    public SableIcon(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y) {
        // 1.21.1 blit: (texture, x, y, uOffset, vOffset, width, height, textureWidth, textureHeight)
        graphics.blit(texture, x, y, 0.0F, 0.0F, 16, 16, 16, 16);
    }
}
