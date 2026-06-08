package com.lycoris.loconautics.allsable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Runtime registry of active {@link SableTrain}s (Option B custom trains).
 *
 * <p>Layer 1 keeps this in-memory only (cleared on restart). Persistence as a {@code SavedData} — mirroring
 * {@code PhysicsTrainRegistry} — is a later layer, once assembly creates these for real instead of a debug
 * command.
 */
public final class SableTrainRegistry {

    private static final Map<UUID, SableTrain> TRAINS = new ConcurrentHashMap<>();

    private SableTrainRegistry() {
    }

    public static void register(SableTrain train) {
        TRAINS.put(train.id(), train);
    }

    public static SableTrain get(UUID id) {
        return TRAINS.get(id);
    }

    public static SableTrain remove(UUID id) {
        return TRAINS.remove(id);
    }

    public static void clear() {
        TRAINS.clear();
    }

    public static boolean isEmpty() {
        return TRAINS.isEmpty();
    }

    /** Snapshot copy, safe to iterate while trains are added/removed. */
    public static Collection<SableTrain> all() {
        return new ArrayList<>(TRAINS.values());
    }
}
