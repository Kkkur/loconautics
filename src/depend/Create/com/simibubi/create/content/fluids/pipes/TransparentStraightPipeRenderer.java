/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockGetter
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids.pipes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.neoforged.neoforge.fluids.FluidStack;

public class TransparentStraightPipeRenderer
extends SafeBlockEntityRenderer<StraightPipeBlockEntity> {
    public TransparentStraightPipeRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(StraightPipeBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        FluidTransportBehaviour pipe = be.getBehaviour(FluidTransportBehaviour.TYPE);
        if (pipe == null) {
            return;
        }
        for (Direction side : Iterate.directions) {
            LerpedFloat progress;
            FluidStack fluidStack;
            PipeConnection.Flow flow = pipe.getFlow(side);
            if (flow == null || (fluidStack = flow.fluid).isEmpty() || (progress = flow.progress) == null) continue;
            float value = progress.getValue(partialTicks);
            boolean inbound = flow.inbound;
            if (value == 1.0f) {
                if (inbound) {
                    PipeConnection.Flow opposite = pipe.getFlow(side.getOpposite());
                    if (opposite == null) {
                        value -= 1.0E-6f;
                    }
                } else {
                    FluidTransportBehaviour adjacent = BlockEntityBehaviour.get((BlockGetter)be.getLevel(), be.getBlockPos().relative(side), FluidTransportBehaviour.TYPE);
                    if (adjacent == null) {
                        value -= 1.0E-6f;
                    } else {
                        PipeConnection.Flow other = adjacent.getFlow(side.getOpposite());
                        if (other == null || !other.inbound && !other.complete) {
                            value -= 1.0E-6f;
                        }
                    }
                }
            }
            FluidRenderer.renderFluidStream(fluidStack, side, 0.1875f, value, inbound, buffer, ms, light);
        }
    }
}
