package com.lycoris.loconautics.server.assembly;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.content.bearingaxle.BearingAxleBlock;
import com.lycoris.loconautics.content.bearingaxle.BearingAxleBlockEntity;
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

import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class PhysicsAssemblyOrchestrator {

    private PhysicsAssemblyOrchestrator() {}

    public static void assemble(ServerPlayer player, StationBlockEntity station) {
        if (!Config.ENABLE_SABLE_MODE.getAsBoolean()) {
            station.assemble(player.getUUID());
            return;
        }

        if (!hasBearingAxleInAssemblyArea(player.serverLevel(), station)) {
            StationBlockEntityAccessor accessor = (StationBlockEntityAccessor) station;
            accessor.loconautics$setLastException(
                    new AssemblyException(
                            CreateLang.translateDirect("loconautics.assembly.missing_bearing_axle")
                    )
            );
            accessor.loconautics$setFailedCarriageIndex(-1);
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

        // Phase 5: wire mass from Sable sub-levels into the Bearing Axle.
        wireMassToAxle(level, entries);
    }

    /**
     * After assembly, sum the mass of every carriage's sub-level and push the
     * total into the BearingAxleBlockEntity so it can compute correct stress.
     *
     * <p>The blocks are already inside Sable sub-levels by the time this runs —
     * they are no longer in the main world — so we scan each sub-level's own
     * ServerLevel to find the axle, not the main world.
     *
     * <p>If a sub-level's MassTracker is null (buildMassTracker hasn't run yet),
     * that carriage contributes 0 kg and a warning is logged. The periodic
     * stress re-tick in BearingAxleBlockEntity (every 20 ticks) will pick up
     * the correct mass once the tracker becomes available.
     */
    private static void wireMassToAxle(ServerLevel level, List<CarriageEntry> entries) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            LoconauticsConstants.LOGGER.warn("wireMassToAxle: Sable container not found on level");
            return;
        }

        double totalMassKg = 0.0;
        BearingAxleBlockEntity axle = null;

        for (CarriageEntry entry : entries) {
            if (!(container.getSubLevel(entry.subLevelId()) instanceof ServerSubLevel serverSub)) {
                LoconauticsConstants.LOGGER.warn(
                        "wireMassToAxle: sub-level {} not found", entry.subLevelId());
                continue;
            }

            // Sum mass — null-check: buildMassTracker() may not have run yet.
            MassData massTracker = serverSub.getMassTracker();
            if (massTracker == null) {
                LoconauticsConstants.LOGGER.warn(
                        "wireMassToAxle: MassTracker not ready for sub-level {}, contributing 0 kg",
                        entry.subLevelId());
            } else {
                totalMassKg += massTracker.getMass();
            }

            // Scan the sub-level's own ServerLevel for the axle — blocks live there after assembly.
            if (axle == null) {
                axle = findBearingAxleInSubLevel(serverSub);
            }
        }

        if (axle == null) {
            LoconauticsConstants.LOGGER.warn(
                    "wireMassToAxle: could not find BearingAxleBlockEntity in assembled train");
            return;
        }

        axle.setTrainMass(totalMassKg);
        LoconauticsConstants.LOGGER.info(
                "wireMassToAxle: set train mass to {}kg on axle at {}",
                totalMassKg, axle.getBlockPos());
    }

    /**
     * Scans the sub-level's own ServerLevel for a {@link BearingAxleBlockEntity}.
     * After assembly, carriage blocks live inside the Sable sub-level's level,
     * not in the main world.
     */
    @Nullable
    private static BearingAxleBlockEntity findBearingAxleInSubLevel(ServerSubLevel serverSub) {
        var bounds = serverSub.getPlot().getBoundingBox();
        ServerLevel subLevel = serverSub.getLevel();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    pos.set(x, y, z);
                    if (!(subLevel.getBlockState(pos).getBlock() instanceof BearingAxleBlock)) continue;
                    BlockEntity be = subLevel.getBlockEntity(pos);
                    if (be instanceof BearingAxleBlockEntity axleBlockEntity) return axleBlockEntity;
                }
            }
        }
        return null;
    }

    @Nullable
    private static UUID findNewTrain(Set<UUID> before) {
        for (UUID id : Create.RAILWAYS.trains.keySet()) {
            if (!before.contains(id)) return id;
        }
        return null;
    }

    private static boolean hasBearingAxleInAssemblyArea(ServerLevel level, StationBlockEntity station) {
        StationBlockEntityAccessor accessor = (StationBlockEntityAccessor) station;
        Direction assemblyDirection = accessor.loconautics$getAssemblyDirection();
        int assemblyLength = accessor.loconautics$getAssemblyLength();

        if (assemblyDirection == null || assemblyLength == 0) {
            return true;
        }

        TrackTargetingBehaviour<?> edgePoint = accessor.loconautics$getEdgePoint();
        BlockPos trackOrigin = edgePoint.getGlobalPosition();
        BlockPos end = trackOrigin.relative(assemblyDirection, assemblyLength);

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