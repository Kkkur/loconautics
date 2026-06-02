/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Lighting
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.gui.UIRenderHelper
 *  net.createmod.catnip.platform.NeoForgeCatnipServices
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.util.Mth
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.compat.jei.category.animations;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import java.util.List;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;

public class AnimatedSpout
extends AnimatedKinetics {
    private List<FluidStack> fluids;

    public AnimatedSpout withFluids(List<FluidStack> fluids) {
        this.fluids = fluids;
        return this;
    }

    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float)xOffset, (float)yOffset, 100.0f);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 20;
        this.blockElement(AllBlocks.SPOUT.getDefaultState()).scale((double)scale).render(graphics);
        float cycle = (AnimationTickHolder.getRenderTime() - (float)(this.offset * 8)) % 30.0f;
        float squeeze = cycle < 20.0f ? Mth.sin((float)((float)((double)(cycle / 20.0f) * Math.PI))) : 0.0f;
        matrixStack.pushPose();
        this.blockElement(AllPartialModels.SPOUT_TOP).scale((double)scale).render(graphics);
        matrixStack.translate(0.0f, -3.0f * (squeeze *= 20.0f) / 32.0f, 0.0f);
        this.blockElement(AllPartialModels.SPOUT_MIDDLE).scale((double)scale).render(graphics);
        matrixStack.translate(0.0f, -3.0f * squeeze / 32.0f, 0.0f);
        this.blockElement(AllPartialModels.SPOUT_BOTTOM).scale((double)scale).render(graphics);
        matrixStack.translate(0.0f, -3.0f * squeeze / 32.0f, 0.0f);
        matrixStack.popPose();
        this.blockElement(AllBlocks.DEPOT.getDefaultState()).atLocal(0.0, 2.0, 0.0).scale((double)scale).render(graphics);
        AnimatedKinetics.DEFAULT_LIGHTING.applyLighting();
        matrixStack.pushPose();
        UIRenderHelper.flipForGuiRender((PoseStack)matrixStack);
        matrixStack.scale(16.0f, 16.0f, 16.0f);
        float from = 0.1875f;
        float to = 1.0625f;
        FluidStack fluidStack = this.fluids.get(0);
        NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox((Object)fluidStack, from, from, from, to, to, to, (MultiBufferSource)graphics.bufferSource(), matrixStack, 0xF000F0, false, true);
        matrixStack.popPose();
        float width = 0.0078125f * squeeze;
        matrixStack.translate((float)scale / 2.0f, (float)scale * 1.5f, (float)scale / 2.0f);
        UIRenderHelper.flipForGuiRender((PoseStack)matrixStack);
        matrixStack.scale(16.0f, 16.0f, 16.0f);
        matrixStack.translate(-0.5f, 0.0f, -0.5f);
        from = -width / 2.0f + 0.5f;
        to = width / 2.0f + 0.5f;
        NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox((Object)fluidStack, from, 0.0f, from, to, 2.0f, to, (MultiBufferSource)graphics.bufferSource(), matrixStack, 0xF000F0, false, true);
        graphics.flush();
        Lighting.setupFor3DItems();
        matrixStack.popPose();
    }
}
