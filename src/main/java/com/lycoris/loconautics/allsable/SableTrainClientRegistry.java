package com.lycoris.loconautics.allsable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Client-side mirror of the server's {@link SableTrainRegistry}, holding just enough about each active train
 * sub-level for the wrench-relocation client flow to work without re-scanning blocks: which sub-level UUIDs are
 * train sub-levels, the carriage's bogey spacing (the relocation ghost-rail length), and whether the train is
 * currently derailed (gates the {@code derailedOnly} config).
 *
 * <p>This is the runtime marker the feature spec calls for: a sub-level qualifies as a train sub-level only when
 * it was assembled as one (a glued cluster carrying a Create bogey), and the server announces each such sub-level
 * to clients via {@link com.lycoris.loconautics.network.packets.SableTrainSyncPacket}. Generic Sable sub-levels
 * (ships, platforms, …) are never announced, so they never appear here and are completely unaffected.
 */
@OnlyIn(Dist.CLIENT)
public final class SableTrainClientRegistry {

    /** One train sub-level's facts: its carriage span, derail state, and current speed (blocks/tick, signed). */
    public record TrainMarker(double bogeySpacing, boolean derailed, double speed) {}

    private static final Map<UUID, TrainMarker> TRAINS = new ConcurrentHashMap<>();

    /** Station block positions where a Sable train is currently parked (synced via StationParkedSyncPacket). */
    private static final Set<BlockPos> PARKED_STATIONS = ConcurrentHashMap.newKeySet();

    private SableTrainClientRegistry() {
    }

    /** Marks (or unmarks) a station position as having a parked Sable train. */
    public static void setStationParked(BlockPos pos, boolean parked) {
        if (parked) {
            PARKED_STATIONS.add(pos.immutable());
        } else {
            PARKED_STATIONS.remove(pos);
        }
    }

    /** True if a Sable train is parked at the station at {@code pos}. */
    public static boolean isStationParked(BlockPos pos) {
        return PARKED_STATIONS.contains(pos);
    }

    /** Snapshot of every station position with a parked Sable train (for per-tick flag driving). */
    public static Set<BlockPos> parkedStations() {
        return Set.copyOf(PARKED_STATIONS);
    }

    /** Adds or updates the marker for a train sub-level (sent on assembly/derail/relocate/login/speed change). */
    public static void put(UUID subLevelId, double bogeySpacing, boolean derailed, double speed) {
        TRAINS.put(subLevelId, new TrainMarker(bogeySpacing, derailed, speed));
    }

    /** Forgets a train sub-level (sent by the server when the train is removed/destroyed). */
    public static void remove(UUID subLevelId) {
        TRAINS.remove(subLevelId);
    }

    /** Drops every marker (on disconnect, so a fresh server re-syncs from scratch). */
    public static void clear() {
        TRAINS.clear();
        PARKED_STATIONS.clear();
    }

    /** The marker for this sub-level, or {@code null} if it is not a (known) train sub-level. */
    public static TrainMarker get(UUID subLevelId) {
        return TRAINS.get(subLevelId);
    }

    /** True if the given sub-level UUID is a known train sub-level. */
    public static boolean isTrain(UUID subLevelId) {
        return subLevelId != null && TRAINS.containsKey(subLevelId);
    }

    /** Snapshot of every known train sub-level id (for per-tick iteration, e.g. the sound handler). */
    public static java.util.Set<UUID> ids() {
        return java.util.Set.copyOf(TRAINS.keySet());
    }
}
