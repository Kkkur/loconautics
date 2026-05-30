package com.lycoris.loconautics.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.lycoris.loconautics.core.PhysicsTrainTag;

/**
 * Client-side mirror of {@link com.lycoris.loconautics.server.PhysicsTrainRegistry}, populated by
 * {@code PhysicsTrainSyncPacket}. Lets client code (e.g. render suppression in Phase 4) know which
 * trains are physics trains without a server round-trip.
 */
public final class ClientPhysicsTrainRegistry {

    private static final Map<UUID, PhysicsTrainTag> TAGS = new HashMap<>();

    private ClientPhysicsTrainRegistry() {
    }

    public static void put(PhysicsTrainTag tag) {
        TAGS.put(tag.trainId(), tag);
    }

    public static void remove(UUID trainId) {
        TAGS.remove(trainId);
    }

    public static void clear() {
        TAGS.clear();
    }

    @Nullable
    public static PhysicsTrainTag get(UUID trainId) {
        return TAGS.get(trainId);
    }

    public static boolean isPhysicsTrain(UUID trainId) {
        return TAGS.containsKey(trainId);
    }
}
