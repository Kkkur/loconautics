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
import com.lycoris.loconautics.mixin.StationBlockEntityAccessor;
import com.lycoris.loconautics.network.LoconauticsNetwork;
import com.lycoris.loconautics.network.PhysicsTrainSyncPacket;
import com.lycoris.loconautics.server.PhysicsTrainRegistry;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class PhysicsAssemblyOrchestrator {

    private PhysicsAssemblyOrchestrator() {}

    public static void assemble(ServerPlayer player, StationBlockEntity station) {
        if (!Config.ENABLE_SABLE_MODE.getAsBoolean()) {
            station.assemble(player.getUUID());
            return;
        }

        // Gate: scan the assembly area for a Bearing Axle BEFORE touching station.assemble().
        // On failure we set the station's lastException so the error appears in the
        // AssemblyScreen UI exactly like Create's own assembly errors — no chat message needed.
        if (!hasBearingAxleInAssemblyArea(player.serverLevel(), station)) {
            StationBlockEntityAccessor accessor = (StationBlockEntityAccessor) station;
            accessor.loconautics$setLastException(
                    new AssemblyException(
                            CreateLang.translateDirect("loconautics.assembly.missing_bearing_axle")
                    )
            );
            accessor.loconautics$setFailedCarriageIndex(-1);
            // Force a block-entity update so the client sees the new lastException immediately.
            station.setChanged();
            station.sendData();
            LoconauticsConstants.LOGGER.info(
                    "Assembly rejected at {}: no Bearing Axle in assembly area",
                    station.getBlockPos()
            );
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
            LoconauticsConstants.LOGGER.info(
                    "Physics assembly produced no sub-levels at {}",
                    station.getBlockPos()
            );
            return;
        }

        UUID newTrainId = findNewTrain(before);
        if (newTrainId == null) {
            LoconauticsConstants.LOGGER.warn(
                    "Created {} sub-levels but could not identify new train",
                    entries.size()
            );
            return;
        }

        ServerLevel level = player.serverLevel();
        PhysicsTrainTag tag = new PhysicsTrainTag(newTrainId, entries);
        PhysicsTrainRegistry.get(level).register(tag);
        LoconauticsNetwork.sendToAll(new PhysicsTrainSyncPacket(tag, false));
        LoconauticsConstants.LOGGER.info(
                "Assembled physics train {} with {} carriage(s)",
                newTrainId,
                entries.size()
        );
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
     *
     * <p>Create's assembly highlight (orange overlay) is drawn starting from the track block
     * the station is targeting — {@code edgePoint.getGlobalPosition()} — not from the station
     * block itself. Using the station's own {@link StationBlockEntity#getBlockPos()} as the
     * origin means the AABB could miss the track entirely when the station is placed off to
     * the side. This method uses the same origin Create uses so the scan matches the visual.
     *
     * <p>The scan is expanded ±2 blocks perpendicular to the assembly direction (to catch
     * blocks placed slightly off the track centreline) and +4 blocks above (to handle tall
     * builds). The station block itself is included in the expansion so it is never missed
     * even when the station IS the origin.
     */
    private static boolean hasBearingAxleInAssemblyArea(ServerLevel level, StationBlockEntity station) {
        StationBlockEntityAccessor accessor = (StationBlockEntityAccessor) station;
        Direction assemblyDirection = accessor.loconautics$getAssemblyDirection();
        int assemblyLength = accessor.loconautics$getAssemblyLength();

        // If the station is not in assembly mode or hasn't refreshed yet, allow through —
        // Create will handle the non-assembly-mode case gracefully.
        if (assemblyDirection == null || assemblyLength == 0) {
            return true;
        }

        // Use the track block position as origin, matching the Create orange highlight.
        TrackTargetingBehaviour<?> edgePoint = accessor.loconautics$getEdgePoint();
        BlockPos trackOrigin = edgePoint.getGlobalPosition();

        BlockPos end = trackOrigin.relative(assemblyDirection, assemblyLength);

        // Expand ±2 perpendicular to catch off-centre placements; +4 above for tall builds.
        BlockPos min = new BlockPos(
                Math.min(trackOrigin.getX(), end.getX()) - 2,
                Math.min(trackOrigin.getY(), end.getY()) - 2,
                Math.min(trackOrigin.getZ(), end.getZ()) - 2
        );
        BlockPos max = new BlockPos(
                Math.max(trackOrigin.getX(), end.getX()) + 2,
                Math.max(trackOrigin.getY(), end.getY()) + 4,
                Math.max(trackOrigin.getZ(), end.getZ()) + 2
        );

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (level.getBlockState(pos).getBlock() instanceof BearingAxleBlock) {
                return true;
            }
        }
        return false;
    }
}