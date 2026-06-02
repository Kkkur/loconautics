/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Portal
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateHolder
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.contraption.train;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.trains.track.AllPortalTracks;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PortalTrackProvider {
    public static final SimpleRegistry<Block, PortalTrackProvider> REGISTRY = SimpleRegistry.create();

    public Exit findExit(ServerLevel var1, BlockFace var2);

    public static boolean isSupportedPortal(BlockState state) {
        return REGISTRY.get((StateHolder<Block, ?>)state) != null;
    }

    @Nullable
    public static Exit getOtherSide(ServerLevel level, BlockFace inboundTrack) {
        BlockPos portalPos = inboundTrack.getConnectedPos();
        BlockState portalState = level.getBlockState(portalPos);
        PortalTrackProvider provider = REGISTRY.get((StateHolder<Block, ?>)portalState);
        return provider == null ? null : provider.findExit(level, inboundTrack);
    }

    public static Exit fromPortal(ServerLevel level, BlockFace face, ResourceKey<Level> firstDimension, ResourceKey<Level> secondDimension, Portal portal) {
        return AllPortalTracks.fromPortal(level, face, firstDimension, secondDimension, portal);
    }

    public record Exit(ServerLevel level, BlockFace face) {
    }
}
