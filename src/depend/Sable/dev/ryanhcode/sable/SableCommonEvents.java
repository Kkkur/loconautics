/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.VeilPacketManager$PacketSink
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 */
package dev.ryanhcode.sable;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.network.packets.tcp.ClientboundFloatingBlockMaterialPacket;
import dev.ryanhcode.sable.network.packets.tcp.ClientboundPhysicsPropertyPacket;
import dev.ryanhcode.sable.physics.chunk.VoxelNeighborhoodState;
import dev.ryanhcode.sable.physics.config.FloatingBlockMaterialDataHandler;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertiesDefinitionLoader;
import dev.ryanhcode.sable.physics.floating_block.FloatingBlockController;
import dev.ryanhcode.sable.physics.floating_block.FloatingBlockMaterial;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import dev.ryanhcode.sable.sublevel.plot.heat.SubLevelHeatMapManager;
import dev.ryanhcode.sable.sublevel.water_occlusion.WaterOcclusionContainer;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class SableCommonEvents {
    public static void handleBlockChange(ServerLevel level, LevelChunk chunk, int x, int y, int z, BlockState oldState, BlockState newState) {
        ChunkPos chunkPos = chunk.getPos();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        PlotChunkHolder plotChunk = container.getChunkHolder(chunkPos);
        int localX = x & 0xF;
        int localZ = z & 0xF;
        if (plotChunk != null) {
            LevelPlot plot = container.getPlot(chunkPos);
            BlockPos blockPos = new BlockPos(x, y, z);
            plotChunk.handleBlockChange(localX, y, localZ, oldState, newState);
            plot.updateBoundingBox();
            plot.expandIfNecessary(blockPos);
            SubLevel subLevel = plot.getSubLevel();
            WaterOcclusionContainer<?> waterOcclusionContainer = WaterOcclusionContainer.getContainer((Level)level);
            if (waterOcclusionContainer != null && VoxelNeighborhoodState.isSolid((BlockGetter)level, blockPos, oldState) != VoxelNeighborhoodState.isSolid((BlockGetter)level, blockPos, newState)) {
                waterOcclusionContainer.markDirty(blockPos);
            }
            if (subLevel instanceof ServerSubLevel) {
                ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
                SubLevelHeatMapManager heatMapManager = serverSubLevel.getHeatMapManager();
                FloatingBlockController floatingBlockController = serverSubLevel.getFloatingBlockController();
                if (oldState != newState) {
                    floatingBlockController.queueRemoveFloatingBlock(oldState, blockPos);
                    floatingBlockController.queueAddFloatingBlock(newState, blockPos);
                }
                if (oldState.isAir() && !newState.isAir()) {
                    heatMapManager.onSolidAdded(blockPos);
                }
                if (!oldState.isAir() && newState.isAir()) {
                    heatMapManager.onSolidRemoved(blockPos);
                }
            }
            if (subLevel.isRemoved()) {
                return;
            }
        }
        int idx = chunk.getSectionIndex(y);
        LevelChunkSection section = chunk.getSection(idx);
        SectionPos sectionPos = SectionPos.of((ChunkPos)chunkPos, (int)chunk.getSectionYFromSectionIndex(idx));
        container.physicsSystem().handleBlockChange(sectionPos, section, localX, y & 0xF, localZ, oldState, newState);
    }

    public static void syncDataPacket(VeilPacketManager.PacketSink sink) {
        sink.sendPacket((CustomPacketPayload[])PhysicsBlockPropertiesDefinitionLoader.INSTANCE.getDefinitions().stream().map(ClientboundPhysicsPropertyPacket::new).toArray(CustomPacketPayload[]::new));
        sink.sendPacket((CustomPacketPayload[])FloatingBlockMaterialDataHandler.allMaterials.entrySet().stream().map(e -> new ClientboundFloatingBlockMaterialPacket((ResourceLocation)e.getKey(), (FloatingBlockMaterial)e.getValue())).toArray(CustomPacketPayload[]::new));
    }
}
