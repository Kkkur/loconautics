package com.lycoris.loconautics.client;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.lycoris.loconautics.core.PhysicsTrainTag;

/**
 * Client-side mirror of {@link com.lycoris.loconautics.server.PhysicsTrainRegistry}, populated by
 * {@code PhysicsTrainSyncPacket}. Lets client code (e.g. render suppression) know which trains are
 * physics trains without a server round-trip.
 *
 * <p>Concurrent map: written from the network/main thread and read from the render thread.
 */
public final class ClientPhysicsTrainRegistry {

    private static final Map<UUID, PhysicsTrainTag> TAGS = new ConcurrentHashMap<>();

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
