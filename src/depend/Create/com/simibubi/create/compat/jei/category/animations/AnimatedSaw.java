/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class AnimatedSaw
extends AnimatedKinetics {
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float)xOffset, (float)yOffset, 0.0f);
        matrixStack.translate(0.0f, 0.0f, 200.0f);
        matrixStack.translate(2.0f, 22.0f, 0.0f);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(112.5f));
        int scale = 25;
        this.blockElement(this.shaft(Direction.Axis.X)).rotateBlock((double)(-AnimatedSaw.getCurrentAngle()), 0.0, 0.0).scale((double)scale).render(graphics);
        this.blockElement((BlockState)AllBlocks.MECHANICAL_SAW.getDefaultState().setValue((Property)SawBlock.FACING, (Comparable)Direction.UP)).rotateBlock(0.0, 0.0, 0.0).scale((double)scale).render(graphics);
        this.blockElement(AllPartialModels.SAW_BLADE_VERTICAL_ACTIVE).rotateBlock(0.0, -90.0, -90.0).scale((double)scale).render(graphics);
        matrixStack.popPose();
    }
}
