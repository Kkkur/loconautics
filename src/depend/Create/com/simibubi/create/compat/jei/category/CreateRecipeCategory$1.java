/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.gui.drawable.IDrawable
 *  net.minecraft.client.gui.GuiGraphics
 */
package com.simibubi.create.compat.jei.category;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;

static class CreateRecipeCategory.1
implements IDrawable {
    final /* synthetic */ AllGuiTextures val$texture;

    CreateRecipeCategory.1(AllGuiTextures allGuiTextures) {
        this.val$texture = allGuiTextures;
    }

    public int getWidth() {
        return this.val$texture.getWidth();
    }

    public int getHeight() {
        return this.val$texture.getHeight();
    }

    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        this.val$texture.render(graphics, xOffset, yOffset);
    }
}
