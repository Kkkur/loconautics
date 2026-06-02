/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.createmod.catnip.animation.AnimationTickHolder
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
import com.simibubi.create.content.kinetics.deployer.DeployerBlock;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class AnimatedDeployer
extends AnimatedKinetics {
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float)xOffset, (float)yOffset, 100.0f);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 20;
        this.blockElement(this.shaft(Direction.Axis.Z)).rotateBlock(0.0, 0.0, (double)AnimatedDeployer.getCurrentAngle()).scale((double)scale).render(graphics);
        this.blockElement((BlockState)((BlockState)AllBlocks.DEPLOYER.getDefaultState().setValue((Property)DeployerBlock.FACING, (Comparable)Direction.DOWN)).setValue((Property)DeployerBlock.AXIS_ALONG_FIRST_COORDINATE, (Comparable)Boolean.valueOf(false))).scale((double)scale).render(graphics);
        float cycle = (AnimationTickHolder.getRenderTime() - (float)(this.offset * 8)) % 30.0f;
        float offset = cycle < 10.0f ? cycle / 10.0f : (cycle < 20.0f ? (20.0f - cycle) / 10.0f : 0.0f);
        matrixStack.pushPose();
        matrixStack.translate(0.0f, offset * 17.0f, 0.0f);
        this.blockElement(AllPartialModels.DEPLOYER_POLE).rotateBlock(90.0, 0.0, 0.0).scale((double)scale).render(graphics);
        this.blockElement(AllPartialModels.DEPLOYER_HAND_HOLDING).rotateBlock(90.0, 0.0, 0.0).scale((double)scale).render(graphics);
        matrixStack.popPose();
        this.blockElement(AllBlocks.DEPOT.getDefaultState()).atLocal(0.0, 2.0, 0.0).scale((double)scale).render(graphics);
        matrixStack.popPose();
    }
}
