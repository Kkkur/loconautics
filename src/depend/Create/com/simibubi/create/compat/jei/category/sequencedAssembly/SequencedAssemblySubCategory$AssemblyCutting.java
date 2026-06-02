/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.gui.GuiGraphics
 */
package com.simibubi.create.compat.jei.category.sequencedAssembly;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.animations.AnimatedSaw;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.client.gui.GuiGraphics;

public static class SequencedAssemblySubCategory.AssemblyCutting
extends SequencedAssemblySubCategory {
    AnimatedSaw saw = new AnimatedSaw();

    public SequencedAssemblySubCategory.AssemblyCutting() {
        super(25);
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(0.0f, 51.5f, 0.0f);
        ms.scale(0.6f, 0.6f, 0.6f);
        this.saw.draw(graphics, this.getWidth() / 2, 30);
        ms.popPose();
    }
}
