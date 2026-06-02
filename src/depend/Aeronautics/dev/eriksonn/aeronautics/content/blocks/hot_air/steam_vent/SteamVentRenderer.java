/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.simulated_team.simulated.util.SimColors
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.steam_vent;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.eriksonn.aeronautics.content.blocks.hot_air.GasEmitterRenderHandler;
import dev.eriksonn.aeronautics.content.blocks.hot_air.steam_vent.SteamVentBlockEntity;
import dev.eriksonn.aeronautics.index.AeroPartialModels;
import dev.simulated_team.simulated.util.SimColors;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class SteamVentRenderer
extends SmartBlockEntityRenderer<SteamVentBlockEntity> {
    public SteamVentRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(SteamVentBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe((SmartBlockEntity)blockEntity, partialTicks, ms, buffer, light, overlay);
        VertexConsumer cutoutConsumer = buffer.getBuffer(RenderType.cutoutMipped());
        float signalStrength = Math.max(0.0f, (float)blockEntity.signalStrength / 15.0f);
        BlockState state = blockEntity.getBlockState();
        CachedBuffers.partial((PartialModel)AeroPartialModels.STEAM_VENT_REDSTONE, (BlockState)state).light(light).color(SimColors.redstone((float)signalStrength)).renderInto(ms, cutoutConsumer);
        GasEmitterRenderHandler renderHandler = blockEntity.getRenderHandler();
        int alpha = renderHandler.getAlpha(partialTicks);
        if (alpha > 2) {
            float position = renderHandler.getPosition(partialTicks);
            VertexConsumer translucentConsumer = buffer.getBuffer(RenderType.translucent());
            SuperByteBuffer base = CachedBuffers.partial((PartialModel)AeroPartialModels.STEAM_VENT_BASE, (BlockState)state);
            SuperByteBuffer jet = CachedBuffers.partial((PartialModel)AeroPartialModels.STEAM_VENT_JET, (BlockState)state);
            ms.pushPose();
            base.disableDiffuse().light(0xF000F0).color(255, 255, 255, alpha).renderInto(ms, translucentConsumer);
            ms.translate(0.0f, (position - 1.0f) / 3.0f, 0.0f);
            jet.disableDiffuse().light(0xF000F0).color(255, 255, 255, alpha).renderInto(ms, translucentConsumer);
            ms.popPose();
        }
    }
}
