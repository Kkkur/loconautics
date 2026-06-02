/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.FluidRenderHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.FluidType
 */
package com.simibubi.create.foundation.fluid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.function.Function;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.FluidRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

@OnlyIn(value=Dist.CLIENT)
public class FluidRenderer {
    public static void renderFluidStream(FluidStack fluidStack, Direction direction, float radius, float progress, boolean inbound, MultiBufferSource buffer, PoseStack ms, int light) {
        FluidRenderer.renderFluidStream(fluidStack, direction, radius, progress, inbound, FluidRenderHelper.getFluidBuilder((MultiBufferSource)buffer), ms, light);
    }

    public static void renderFluidStream(FluidStack fluidStack, Direction direction, float radius, float progress, boolean inbound, VertexConsumer builder, PoseStack ms, int light) {
        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of((Fluid)fluid);
        FluidType fluidAttributes = fluid.getFluidType();
        Function spriteAtlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite flowTexture = (TextureAtlasSprite)spriteAtlas.apply(clientFluid.getFlowingTexture(fluidStack));
        TextureAtlasSprite stillTexture = (TextureAtlasSprite)spriteAtlas.apply(clientFluid.getStillTexture(fluidStack));
        int color = clientFluid.getTintColor(fluidStack);
        int blockLightIn = light >> 4 & 0xF;
        int luminosity = Math.max(blockLightIn, fluidAttributes.getLightLevel(fluidStack));
        light = light & 0xF00000 | luminosity << 4;
        if (inbound) {
            direction = direction.getOpposite();
        }
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        ms.pushPose();
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)msr.center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)direction))).rotateXDegrees(direction == Direction.UP ? 180.0f : (direction == Direction.DOWN ? 0.0f : 270.0f))).uncenter();
        ms.translate(0.5, 0.0, 0.5);
        float h = radius;
        float hMin = -radius;
        float hMax = radius;
        float y = inbound ? 1.0f : 0.5f;
        float yMin = y - Mth.clamp((float)(progress * 0.5f), (float)0.0f, (float)1.0f);
        float yMax = y;
        for (int i = 0; i < 4; ++i) {
            ms.pushPose();
            FluidRenderer.renderFlowingTiledFace(Direction.SOUTH, hMin, yMin, hMax, yMax, h, builder, ms, light, color, flowTexture);
            ms.popPose();
            msr.rotateYDegrees(90.0f);
        }
        if (progress != 1.0f) {
            FluidRenderHelper.renderStillTiledFace((Direction)Direction.DOWN, (float)hMin, (float)hMin, (float)hMax, (float)hMax, (float)yMin, (VertexConsumer)builder, (PoseStack)ms, (int)light, (int)color, (TextureAtlasSprite)stillTexture);
        }
        ms.popPose();
    }

    public static void renderFlowingTiledFace(Direction dir, float left, float down, float right, float up, float depth, VertexConsumer builder, PoseStack ms, int light, int color, TextureAtlasSprite texture) {
        FluidRenderHelper.renderTiledFace((Direction)dir, (float)left, (float)down, (float)right, (float)up, (float)depth, (VertexConsumer)builder, (PoseStack)ms, (int)light, (int)color, (TextureAtlasSprite)texture, (float)0.5f);
    }
}
