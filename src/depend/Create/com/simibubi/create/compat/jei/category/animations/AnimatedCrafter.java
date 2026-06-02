/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.minecraft.client.gui.GuiGraphics
 */
package com.simibubi.create.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.gui.GuiGraphics;

public class AnimatedCrafter
extends AnimatedKinetics {
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float)xOffset, (float)yOffset, 0.0f);
        AllGuiTextures.JEI_SHADOW.render(graphics, -16, 13);
        matrixStack.translate(3.0f, 16.0f, 0.0f);
        ((PoseTransformStack)TransformStack.of((PoseStack)matrixStack).rotateXDegrees(-12.5f)).rotateYDegrees(-22.5f);
        int scale = 22;
        this.blockElement(this.cogwheel()).rotateBlock(90.0, 0.0, (double)AnimatedCrafter.getCurrentAngle()).scale((double)scale).render(graphics);
        this.blockElement(AllBlocks.MECHANICAL_CRAFTER.getDefaultState()).rotateBlock(0.0, 180.0, 0.0).scale((double)scale).render(graphics);
        matrixStack.popPose();
    }
}
