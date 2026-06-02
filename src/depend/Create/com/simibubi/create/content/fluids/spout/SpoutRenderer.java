/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.platform.NeoForgeCatnipServices
 *  net.createmod.catnip.render.CachedBuffers
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids.spout;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;

public class SpoutRenderer
extends SafeBlockEntityRenderer<SpoutBlockEntity> {
    static final PartialModel[] BITS = new PartialModel[]{AllPartialModels.SPOUT_TOP, AllPartialModels.SPOUT_MIDDLE, AllPartialModels.SPOUT_BOTTOM};

    public SpoutRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(SpoutBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        SmartFluidTankBehaviour tank = be.tank;
        if (tank == null) {
            return;
        }
        SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
        FluidStack fluidStack = primaryTank.getRenderedFluid();
        float level = primaryTank.getFluidLevel().getValue(partialTicks);
        if (!fluidStack.isEmpty() && level != 0.0f) {
            boolean top = fluidStack.getFluid().getFluidType().isLighterThanAir();
            level = Math.max(level, 0.175f);
            float min = 0.15625f;
            float max = min + 0.6875f;
            float yOffset = 0.6875f * level;
            ms.pushPose();
            if (!top) {
                ms.translate(0.0f, yOffset, 0.0f);
            } else {
                ms.translate(0.0f, max - min, 0.0f);
            }
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox((Object)fluidStack, min, min - yOffset, min, max, min, max, buffer, ms, light, false, true);
            ms.popPose();
        }
        int processingTicks = be.processingTicks;
        float processingPT = (float)processingTicks - partialTicks;
        float processingProgress = 1.0f - (processingPT - 5.0f) / 10.0f;
        processingProgress = Mth.clamp((float)processingProgress, (float)0.0f, (float)1.0f);
        float radius = 0.0f;
        if (!fluidStack.isEmpty() && processingTicks != -1) {
            radius = (float)(Math.pow(2.0f * processingProgress - 1.0f, 2.0) - 1.0);
            AABB bb = new AABB(0.5, 0.0, 0.5, 0.5, -1.2, 0.5).inflate((double)(radius / 32.0f));
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox((Object)fluidStack, (float)bb.minX, (float)bb.minY, (float)bb.minZ, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ, buffer, ms, light, true, true);
        }
        float squeeze = radius;
        if (processingPT < 0.0f) {
            squeeze = 0.0f;
        } else if (processingPT < 2.0f) {
            squeeze = Mth.lerp((float)(processingPT / 2.0f), (float)0.0f, (float)-1.0f);
        } else if (processingPT < 10.0f) {
            squeeze = -1.0f;
        }
        ms.pushPose();
        for (PartialModel bit : BITS) {
            CachedBuffers.partial((PartialModel)bit, (BlockState)be.getBlockState()).light(light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
            ms.translate(0.0f, -3.0f * squeeze / 32.0f, 0.0f);
        }
        ms.popPose();
    }
}
