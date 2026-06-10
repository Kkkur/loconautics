/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
 *  net.neoforged.neoforge.client.event.EntityRenderersEvent$RegisterRenderers
 */
package com.bearing.linearbearing;

import com.bearing.linearbearing.AxisRender;
import com.bearing.linearbearing.ModBlocks;
import com.bearing.linearbearing.RedstoneConverterRenderer;
import com.bearing.linearbearing.TorsionalAnchorRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class LinearBearingClient {
    public static void registerClient(IEventBus modEventBus) {
        modEventBus.addListener(LinearBearingClient::onRegisterRenderers);
        modEventBus.addListener(LinearBearingClient::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.MAGNETIC_PORT.get(), (RenderType)RenderType.cutout()));
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer((BlockEntityType)ModBlocks.MAGNETIC_PORT_BE.get(), AxisRender::new);
        event.registerBlockEntityRenderer((BlockEntityType)ModBlocks.REDSTONE_CONVERTER_BE.get(), RedstoneConverterRenderer::new);
        event.registerBlockEntityRenderer((BlockEntityType)ModBlocks.TORSIONAL_ANCHOR_BE.get(), TorsionalAnchorRenderer::new);
    }
}
