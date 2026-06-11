package com.lycoris.loconautics.allsable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    /** One train sub-level's relocation-relevant facts: its carriage span and current derail state. */
    public record TrainMarker(double bogeySpacing, boolean derailed) {}

    private static final Map<UUID, TrainMarker> TRAINS = new ConcurrentHashMap<>();

    private SableTrainClientRegistry() {
    }

    /** Adds or updates the marker for a train sub-level (sent by the server on assembly/derail/relocate/login). */
    public static void put(UUID subLevelId, double bogeySpacing, boolean derailed) {
        TRAINS.put(subLevelId, new TrainMarker(bogeySpacing, derailed));
    }

    /** Forgets a train sub-level (sent by the server when the train is removed/destroyed). */
    public static void remove(UUID subLevelId) {
        TRAINS.remove(subLevelId);
    }

    /** Drops every marker (on disconnect, so a fresh server re-syncs from scratch). */
    public static void clear() {
        TRAINS.clear();
    }

    /** The marker for this sub-level, or {@code null} if it is not a (known) train sub-level. */
    public static TrainMarker get(UUID subLevelId) {
        return TRAINS.get(subLevelId);
    }

    /** True if the given sub-level UUID is a known train sub-level. */
    public static boolean isTrain(UUID subLevelId) {
        return subLevelId != null && TRAINS.containsKey(subLevelId);
    }
}
