/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.Portal
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.portal.DimensionTransition
 *  net.minecraft.world.phys.AABB
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.train.PortalTrackProvider;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;

public class AllPortalTracks {
    public static void tryRegisterIntegration(ResourceLocation id, PortalTrackProvider provider) {
        if (BuiltInRegistries.BLOCK.containsKey(id)) {
            Block block = (Block)BuiltInRegistries.BLOCK.get(id);
            PortalTrackProvider.REGISTRY.register(block, provider);
        } else {
            Create.LOGGER.warn("Portal for integration wasn't found: {}. Compat outdated?", (Object)id);
        }
    }

    private static void tryRegisterSimpleInteraction(ResourceLocation portalBlockId, ResourceLocation dimensionId) {
        ResourceKey levelKey = ResourceKey.create((ResourceKey)Registries.DIMENSION, (ResourceLocation)dimensionId);
        AllPortalTracks.tryRegisterSimpleInteraction(portalBlockId, (ResourceKey<Level>)levelKey);
    }

    private static void tryRegisterSimpleInteraction(ResourceLocation portalBlockId, ResourceKey<Level> levelKey) {
        AllPortalTracks.tryRegisterSimpleInteraction((Block)BuiltInRegistries.BLOCK.get(portalBlockId), levelKey);
    }

    private static void tryRegisterSimpleInteraction(Block portalBlock, ResourceKey<Level> levelKey) {
        PortalTrackProvider p = (level, face) -> PortalTrackProvider.fromPortal(level, face, (ResourceKey<Level>)Level.OVERWORLD, levelKey, (Portal)portalBlock);
        PortalTrackProvider.REGISTRY.register(portalBlock, p);
    }

    public static void registerDefaults() {
        AllPortalTracks.tryRegisterSimpleInteraction(Blocks.NETHER_PORTAL, (ResourceKey<Level>)Level.NETHER);
        if (Mods.AETHER.isLoaded()) {
            AllPortalTracks.tryRegisterSimpleInteraction(Mods.AETHER.rl("aether_portal"), Mods.AETHER.rl("the_aether"));
        }
        if (Mods.AETHER_II.isLoaded()) {
            AllPortalTracks.tryRegisterSimpleInteraction(Mods.AETHER_II.rl("aether_portal"), Mods.AETHER_II.rl("aether_highlands"));
        }
        if (Mods.BETTEREND.isLoaded()) {
            AllPortalTracks.tryRegisterSimpleInteraction(Mods.BETTEREND.rl("end_portal_block"), (ResourceKey<Level>)Level.END);
        }
    }

    public static PortalTrackProvider.Exit fromPortal(ServerLevel level, BlockFace inboundTrack, ResourceKey<Level> firstDimension, ResourceKey<Level> secondDimension, Portal portal) {
        ResourceKey<Level> resourceKey = level.dimension() == secondDimension ? firstDimension : secondDimension;
        MinecraftServer minecraftServer = level.getServer();
        ServerLevel otherLevel = minecraftServer.getLevel(resourceKey);
        if (otherLevel == null) {
            return null;
        }
        BlockPos portalPos = inboundTrack.getConnectedPos();
        BlockState portalState = level.getBlockState(portalPos);
        SuperGlueEntity probe = new SuperGlueEntity((Level)level, new AABB(portalPos));
        probe.setYRot(inboundTrack.getFace().toYRot());
        DimensionTransition dimensiontransition = portal.getPortalDestination(level, (Entity)probe, probe.blockPosition());
        if (dimensiontransition == null) {
            return null;
        }
        if (!minecraftServer.isLevelEnabled((Level)dimensiontransition.newLevel())) {
            return null;
        }
        BlockPos otherPortalPos = BlockPos.containing((Position)dimensiontransition.pos());
        BlockState otherPortalState = otherLevel.getBlockState(otherPortalPos);
        if (!otherPortalState.is(portalState.getBlock())) {
            return null;
        }
        Direction targetDirection = inboundTrack.getFace();
        if (targetDirection.getAxis() == otherPortalState.getValue((Property)BlockStateProperties.HORIZONTAL_AXIS)) {
            targetDirection = targetDirection.getClockWise();
        }
        BlockPos otherPos = otherPortalPos.relative(targetDirection);
        return new PortalTrackProvider.Exit(otherLevel, new BlockFace(otherPos, targetDirection.getOpposite()));
    }
}
