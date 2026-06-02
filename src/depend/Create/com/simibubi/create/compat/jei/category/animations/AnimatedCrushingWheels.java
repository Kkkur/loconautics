/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class AnimatedCrushingWheels
extends AnimatedKinetics {
    private final BlockState wheel = (BlockState)AllBlocks.CRUSHING_WHEEL.getDefaultState().setValue((Property)BlockStateProperties.AXIS, (Comparable)Direction.Axis.X);

    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float)xOffset, (float)yOffset, 100.0f);
        matrixStack.mulPose(Axis.YP.rotationDegrees(-22.5f));
        int scale = 22;
        this.blockElement(this.wheel).rotateBlock(0.0, 90.0, (double)(-AnimatedCrushingWheels.getCurrentAngle())).scale((double)scale).render(graphics);
        this.blockElement(this.wheel).rotateBlock(0.0, 90.0, (double)AnimatedCrushingWheels.getCurrentAngle()).atLocal(2.0, 0.0, 0.0).scale((double)scale).render(graphics);
        matrixStack.popPose();
    }
}
