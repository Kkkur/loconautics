/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.util.Mth
 */
package com.simibubi.create.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class AnimatedMixer
extends AnimatedKinetics {
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float)xOffset, (float)yOffset, 200.0f);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 23;
        this.blockElement(this.cogwheel()).rotateBlock(0.0, (double)(AnimatedMixer.getCurrentAngle() * 2.0f), 0.0).atLocal(0.0, 0.0, 0.0).scale((double)scale).render(graphics);
        this.blockElement(AllBlocks.MECHANICAL_MIXER.getDefaultState()).atLocal(0.0, 0.0, 0.0).scale((double)scale).render(graphics);
        float animation = (Mth.sin((float)(AnimationTickHolder.getRenderTime() / 32.0f)) + 1.0f) / 5.0f + 0.5f;
        this.blockElement(AllPartialModels.MECHANICAL_MIXER_POLE).atLocal(0.0, (double)animation, 0.0).scale((double)scale).render(graphics);
        this.blockElement(AllPartialModels.MECHANICAL_MIXER_HEAD).rotateBlock(0.0, (double)(AnimatedMixer.getCurrentAngle() * 4.0f), 0.0).atLocal(0.0, (double)animation, 0.0).scale((double)scale).render(graphics);
        this.blockElement(AllBlocks.BASIN.getDefaultState()).atLocal(0.0, 1.65, 0.0).scale((double)scale).render(graphics);
        matrixStack.popPose();
    }
}
