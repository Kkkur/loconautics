/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.platform.NeoForgeCatnipServices
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 */
package com.simibubi.create.content.fluids.tank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FluidTankRenderer
extends SafeBlockEntityRenderer<FluidTankBlockEntity> {
    public FluidTankRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(FluidTankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (!be.isController()) {
            return;
        }
        if (!be.window) {
            if (be.boiler.isActive()) {
                this.renderAsBoiler(be, partialTicks, ms, buffer, light, overlay);
            }
            return;
        }
        LerpedFloat fluidLevel = be.getFluidLevel();
        if (fluidLevel == null) {
            return;
        }
        float capHeight = 0.25f;
        float tankHullWidth = 0.0703125f;
        float minPuddleHeight = 0.0625f;
        float totalHeight = (float)be.height - 2.0f * capHeight - minPuddleHeight;
        float level = fluidLevel.getValue(partialTicks);
        if (level < 1.0f / (512.0f * totalHeight)) {
            return;
        }
        float clampedLevel = Mth.clamp((float)(level * totalHeight), (float)0.0f, (float)totalHeight);
        FluidTank tank = be.tankInventory;
        FluidStack fluidStack = tank.getFluid();
        if (fluidStack.isEmpty()) {
            return;
        }
        boolean top = fluidStack.getFluid().getFluidType().isLighterThanAir();
        float xMin = tankHullWidth;
        float xMax = xMin + (float)be.width - 2.0f * tankHullWidth;
        float yMin = totalHeight + capHeight + minPuddleHeight - clampedLevel;
        float yMax = yMin + clampedLevel;
        if (top) {
            yMin += totalHeight - clampedLevel;
            yMax += totalHeight - clampedLevel;
        }
        float zMin = tankHullWidth;
        float zMax = zMin + (float)be.width - 2.0f * tankHullWidth;
        ms.pushPose();
        ms.translate(0.0f, clampedLevel - totalHeight, 0.0f);
        NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox((Object)fluidStack, xMin, yMin, zMin, xMax, yMax, zMax, buffer, ms, light, false, true);
        ms.popPose();
    }

    protected void renderAsBoiler(FluidTankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.cutout());
        ms.pushPose();
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        msr.translate((double)((float)be.width / 2.0f), 0.5, (double)((float)be.width / 2.0f));
        float dialPivotY = 0.375f;
        float dialPivotZ = 0.5f;
        float progress = be.boiler.gauge.getValue(partialTicks);
        for (Direction d : Iterate.horizontalDirections) {
            if (be.boiler.occludedDirections[d.get2DDataValue()]) continue;
            ms.pushPose();
            float yRot = -d.toYRot() - 90.0f;
            ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.BOILER_GAUGE, (BlockState)blockState).rotateYDegrees(yRot)).uncenter()).translate((float)be.width / 2.0f - 0.375f, 0.0f, 0.0f)).light(light).renderInto(ms, vb);
            ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.BOILER_GAUGE_DIAL, (BlockState)blockState).rotateYDegrees(yRot)).uncenter()).translate((float)be.width / 2.0f - 0.375f, 0.0f, 0.0f)).translate(0.0f, dialPivotY, dialPivotZ)).rotateXDegrees(-145.0f * progress + 90.0f)).translate(0.0f, -dialPivotY, -dialPivotZ)).light(light).renderInto(ms, vb);
            ms.popPose();
        }
        ms.popPose();
    }

    public boolean shouldRenderOffScreen(FluidTankBlockEntity be) {
        return be.isController();
    }
}
