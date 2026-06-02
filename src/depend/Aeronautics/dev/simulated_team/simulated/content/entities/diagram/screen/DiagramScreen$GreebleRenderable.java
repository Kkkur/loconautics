/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.resources.ResourceLocation
 */
package dev.simulated_team.simulated.content.entities.diagram.screen;

import dev.simulated_team.simulated.content.entities.diagram.screen.Greeble;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.resources.ResourceLocation;

public record DiagramScreen.GreebleRenderable(int x, int y, int width, int height, ResourceLocation texture, Greeble.TextureSlice slice) implements Renderable
{
    public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
        guiGraphics.blit(this.texture, this.x, this.y, (float)this.slice.x(), (float)this.slice.y(), this.slice.width(), this.slice.height(), this.width, this.height);
    }
}
