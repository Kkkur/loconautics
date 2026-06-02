/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.createmod.catnip.gui.UIRenderHelper
 *  net.createmod.catnip.platform.NeoForgeCatnipServices
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.neoforge.fluids.FluidStack;

public class AnimatedItemDrain
extends AnimatedKinetics {
    private FluidStack fluid;

    public AnimatedItemDrain withFluid(FluidStack fluid) {
        this.fluid = fluid;
        return this;
    }

    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float)xOffset, (float)yOffset, 100.0f);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 20;
        this.blockElement(AllBlocks.ITEM_DRAIN.getDefaultState()).scale((double)scale).render(graphics);
        UIRenderHelper.flipForGuiRender((PoseStack)matrixStack);
        matrixStack.scale((float)scale, (float)scale, (float)scale);
        float from = 0.125f;
        float to = 1.0f - from;
        NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox((Object)this.fluid, from, from, from, to, 0.75f, to, (MultiBufferSource)graphics.bufferSource(), matrixStack, 0xF000F0, false, true);
        graphics.flush();
        matrixStack.popPose();
    }
}
