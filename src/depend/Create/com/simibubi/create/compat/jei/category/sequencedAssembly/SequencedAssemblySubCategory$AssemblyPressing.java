/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.gui.GuiGraphics
 */
package com.simibubi.create.compat.jei.category.sequencedAssembly;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.animations.AnimatedPress;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.client.gui.GuiGraphics;

public static class SequencedAssemblySubCategory.AssemblyPressing
extends SequencedAssemblySubCategory {
    AnimatedPress press = new AnimatedPress(false);

    public SequencedAssemblySubCategory.AssemblyPressing() {
        super(25);
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        this.press.offset = index;
        ms.pushPose();
        ms.translate(-5.0f, 50.0f, 0.0f);
        ms.scale(0.6f, 0.6f, 0.6f);
        this.press.draw(graphics, this.getWidth() / 2, 0);
        ms.popPose();
    }
}
