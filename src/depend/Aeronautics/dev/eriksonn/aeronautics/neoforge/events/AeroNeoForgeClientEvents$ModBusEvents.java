/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.world.level.block.Block
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
 *  net.neoforged.neoforge.client.ChunkRenderTypeSet
 *  net.neoforged.neoforge.client.event.RenderLevelStageEvent$RegisterStageEvent
 *  net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
 *  net.neoforged.neoforge.fluids.FluidType
 */
package dev.eriksonn.aeronautics.neoforge.events;

import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.eriksonn.aeronautics.index.client.AeroRenderTypes;
import dev.eriksonn.aeronautics.mixin.levitite.ChunkRenderTypeSetAccessor;
import dev.eriksonn.aeronautics.neoforge.content.fluids.AeroFluidType;
import dev.eriksonn.aeronautics.neoforge.index.AeroFluidsNeoForge;
import java.util.List;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;

@EventBusSubscriber(modid="aeronautics", value={Dist.CLIENT})
public static class AeroNeoForgeClientEvents.ModBusEvents {
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        AeroFluidType type = (AeroFluidType)AeroFluidsNeoForge.LEVITITE_BLEND.getType();
        event.registerFluidType((IClientFluidTypeExtensions)type, new FluidType[]{type});
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ChunkRenderTypeSet set = ChunkRenderTypeSet.of((RenderType[])new RenderType[]{RenderType.SOLID, AeroRenderTypes.levitite(), AeroRenderTypes.levititeGhosts()});
        ItemBlockRenderTypes.setRenderLayer((Block)((Block)AeroBlocks.LEVITITE.get()), (ChunkRenderTypeSet)set);
        ItemBlockRenderTypes.setRenderLayer((Block)((Block)AeroBlocks.PEARLESCENT_LEVITITE.get()), (ChunkRenderTypeSet)set);
        AeroNeoForgeClientEvents.ModBusEvents.fixChunkRenderTypeSet();
    }

    private static void fixChunkRenderTypeSet() {
        List list = RenderType.chunkBufferLayers();
        ChunkRenderTypeSetAccessor.setChunkRenderTypesList(list);
        ChunkRenderTypeSetAccessor.setChunkRenderTypes(list.toArray(new RenderType[0]));
        ((ChunkRenderTypeSetAccessor)ChunkRenderTypeSet.all()).getBits().set(0, list.size());
    }

    @SubscribeEvent
    public static void registerRegisterStageEvent(RenderLevelStageEvent.RegisterStageEvent event) {
        event.register(Aeronautics.path("levitite"), AeroRenderTypes.levitite());
        event.register(Aeronautics.path("levitite_ghosts"), AeroRenderTypes.levititeGhosts());
    }
}
