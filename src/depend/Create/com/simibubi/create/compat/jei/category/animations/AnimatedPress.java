/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.core.Direction$Axis
 */
package com.simibubi.create.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;

public class AnimatedPress
extends AnimatedKinetics {
    private boolean basin;

    public AnimatedPress(boolean basin) {
        this.basin = basin;
    }

    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float)xOffset, (float)yOffset, 200.0f);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = this.basin ? 23 : 24;
        this.blockElement(this.shaft(Direction.Axis.Z)).rotateBlock(0.0, 0.0, (double)AnimatedPress.getCurrentAngle()).scale((double)scale).render(graphics);
        this.blockElement(AllBlocks.MECHANICAL_PRESS.getDefaultState()).scale((double)scale).render(graphics);
        this.blockElement(AllPartialModels.MECHANICAL_PRESS_HEAD).atLocal(0.0, (double)(-this.getAnimatedHeadOffset()), 0.0).scale((double)scale).render(graphics);
        if (this.basin) {
            this.blockElement(AllBlocks.BASIN.getDefaultState()).atLocal(0.0, 1.65, 0.0).scale((double)scale).render(graphics);
        }
        matrixStack.popPose();
    }

    private float getAnimatedHeadOffset() {
        float cycle = (AnimationTickHolder.getRenderTime() - (float)(this.offset * 8)) % 30.0f;
        if (cycle < 10.0f) {
            float progress = cycle / 10.0f;
            return -(progress * progress * progress);
        }
        if (cycle < 15.0f) {
            return -1.0f;
        }
        if (cycle < 20.0f) {
            return -1.0f + (1.0f - (20.0f - cycle) / 5.0f);
        }
        return 0.0f;
    }
}
