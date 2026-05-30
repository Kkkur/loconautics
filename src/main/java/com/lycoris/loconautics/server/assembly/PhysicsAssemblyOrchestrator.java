package com.lycoris.loconautics.server.assembly;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.core.PhysicsTrainTag;
import com.lycoris.loconautics.core.PhysicsTrainTag.CarriageEntry;
import com.lycoris.loconautics.network.LoconauticsNetwork;
import com.lycoris.loconautics.network.PhysicsTrainSyncPacket;
import com.lycoris.loconautics.server.PhysicsTrainRegistry;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.station.StationBlockEntity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class PhysicsAssemblyOrchestrator {

    private PhysicsAssemblyOrchestrator() {}

    public static void assemble(ServerPlayer player, StationBlockEntity station) {
        if (!Config.ENABLE_SABLE_MODE.getAsBoolean()) {
            station.assemble(player.getUUID());
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
}