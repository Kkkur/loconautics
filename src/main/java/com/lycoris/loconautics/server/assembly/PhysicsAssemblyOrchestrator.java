package com.lycoris.loconautics.server.assembly;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.core.PhysicsTrainTag;
import com.lycoris.loconautics.network.LoconauticsNetwork;
import com.lycoris.loconautics.network.PhysicsTrainSyncPacket;
import com.lycoris.loconautics.server.PhysicsTrainRegistry;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.station.StationBlockEntity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Drives a physics-train assembly.
 *
 * <p>It reuses Create's own assembly end to end ({@code StationBlockEntity.assemble}); the
 * {@link com.lycoris.loconautics.mixin.StationBlockEntityAssembleMixin} hijacks each carriage's
 * {@code removeBlocksFromWorld} to divert blocks into Sable sub-levels instead. After assembly we
 * diff Create's train registry to find the freshly created train and tag it as a physics train.
 */
public final class PhysicsAssemblyOrchestrator {

    private PhysicsAssemblyOrchestrator() {
    }

    public static void assemble(ServerPlayer player, StationBlockEntity station) {
        UUID playerUUID = player.getUUID();
        ServerLevel level = player.serverLevel();

        // If the addon is disabled, fall back to an ordinary Create assembly.
        if (!Config.ENABLE_SABLE_MODE.getAsBoolean()) {
            station.assemble(playerUUID);
            return;
        }

        Set<UUID> trainsBefore = new HashSet<>(Create.RAILWAYS.trains.keySet());

        PhysicsAssemblyContext.begin();
        List<UUID> subLevels;
        try {
            station.assemble(playerUUID);
        } finally {
            subLevels = PhysicsAssemblyContext.drain();
            PhysicsAssemblyContext.end();
        }

        if (subLevels.isEmpty()) {
            // Assembly failed (Create reported an AssemblyException) or nothing was captured.
            LoconauticsConstants.LOGGER.info("Physics assembly produced no sub-levels at {}", station.getBlockPos());
            return;
        }

        UUID newTrainId = findNewTrain(trainsBefore);
        if (newTrainId == null) {
            LoconauticsConstants.LOGGER.warn(
                    "Created {} sub-levels but could not identify the new train; aborting tag", subLevels.size());
            return;
        }

        PhysicsTrainTag tag = new PhysicsTrainTag(newTrainId, subLevels);
        PhysicsTrainRegistry.get(level).register(tag);
        LoconauticsNetwork.sendToAll(new PhysicsTrainSyncPacket(tag, false));

        LoconauticsConstants.LOGGER.info(
                "Assembled physics train {} with {} carriage sub-level(s)", newTrainId, subLevels.size());
    }

    @Nullable
    private static UUID findNewTrain(Set<UUID> before) {
        for (UUID id : Create.RAILWAYS.trains.keySet()) {
            if (!before.contains(id)) {
                return id;
            }
        }
        return null;
    }
}
