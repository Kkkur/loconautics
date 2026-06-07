package com.lycoris.loconautics.server.assembly;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.content.bearingaxle.BearingAxleBlock;
import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.core.PhysicsTrainTag;
import com.lycoris.loconautics.core.PhysicsTrainTag.CarriageEntry;
import com.lycoris.loconautics.network.LoconauticsNetwork;
import com.lycoris.loconautics.network.PhysicsTrainSyncPacket;
import com.lycoris.loconautics.mixin.client.StationBlockEntityAccessor;
import com.lycoris.loconautics.server.PhysicsTrainRegistry;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.station.StationBlockEntity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class PhysicsAssemblyOrchestrator {

    private PhysicsAssemblyOrchestrator() {}

    public static void assemble(ServerPlayer player, StationBlockEntity station) {
        if (!Config.ENABLE_SABLE_MODE.getAsBoolean()) {
            station.assemble(player.getUUID());
            return;
        }

        // Gate: scan the assembly area for a Bearing Axle BEFORE touching station.assemble()
        if (!hasBearingAxleInAssemblyArea(player.serverLevel(), station)) {
            player.displayClientMessage(
                    Component.literal("! ")
                            .append(Component.translatable("loconautics.assembly.missing_bearing_axle"))
                            .withStyle(ChatFormatting.RED),
                    false
            );
            LoconauticsConstants.LOGGER.info("Assembly rejected at {}: no Bearing Axle in assembly area", station.getBlockPos());
            return;
        }

        Set<UUID> before = new HashSet<>(Create.RAILWAYS.trains.keySet());
        PhysicsAssemblyContext.begin();
        List<CarriageEntry> entries;
        try {
            station.assemble(player.getUUID());
        } finally {
            entries = PhysicsAssemblyContext.drain();
            PhysicsAssemblyContext.end();
        }

        if (entries.isEmpty()) {
            LoconauticsConstants.LOGGER.info("Physics assembly produced no sub-levels at {}", station.getBlockPos());
            return;
        }

        UUID newTrainId = findNewTrain(before);
        if (newTrainId == null) {
            LoconauticsConstants.LOGGER.warn("Created {} sub-levels but could not identify new train", entries.size());
            return;
        }

        ServerLevel level = player.serverLevel();
        PhysicsTrainTag tag = new PhysicsTrainTag(newTrainId, entries);
        PhysicsTrainRegistry.get(level).register(tag);
        LoconauticsNetwork.sendToAll(new PhysicsTrainSyncPacket(tag, false));
        LoconauticsConstants.LOGGER.info("Assembled physics train {} with {} carriage(s)", newTrainId, entries.size());
    }

    @Nullable
    private static UUID findNewTrain(Set<UUID> before) {
        for (UUID id : Create.RAILWAYS.trains.keySet()) {
            if (!before.contains(id)) return id;
        }
        return null;
    }

    /**
     * Scans the station's assembly area for a Bearing Axle block.
     * Uses the station's own assemblyDirection and assemblyLength fields — the same area
     * Create highlights in orange — so the scan is exact, not a guess.
     */
    private static boolean hasBearingAxleInAssemblyArea(ServerLevel level, StationBlockEntity station) {
        StationBlockEntityAccessor accessor = (StationBlockEntityAccessor) station;
        Direction assemblyDirection = accessor.loconautics$getAssemblyDirection();
        int assemblyLength = accessor.loconautics$getAssemblyLength();

        if (assemblyDirection == null || assemblyLength == 0) {
            return true;
        }

        BlockPos origin = station.getBlockPos();
        BlockPos end = origin.relative(assemblyDirection, assemblyLength);

        // Expand by 2 in each perpendicular axis to catch blocks placed slightly off the track centerline
        BlockPos min = new BlockPos(
                Math.min(origin.getX(), end.getX()) - 2,
                Math.min(origin.getY(), end.getY()) - 2,
                Math.min(origin.getZ(), end.getZ()) - 2
        );
        BlockPos max = new BlockPos(
                Math.max(origin.getX(), end.getX()) + 2,
                Math.max(origin.getY(), end.getY()) + 4,
                Math.max(origin.getZ(), end.getZ()) + 2
        );

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (level.getBlockState(pos).getBlock() instanceof BearingAxleBlock) {
                return true;
            }
        }
        return false;
    }
}