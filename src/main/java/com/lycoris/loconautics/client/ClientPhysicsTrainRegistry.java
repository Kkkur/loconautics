package com.lycoris.loconautics.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.lycoris.loconautics.core.PhysicsTrainTag;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;

/**
 * Client-side mirror of {@link com.lycoris.loconautics.server.PhysicsTrainRegistry}, populated by
 * {@code PhysicsTrainSyncPacket}. Lets client code (e.g. render suppression) know which trains are
 * physics trains without a server round-trip.
 *
 * <p>Concurrent map: written from the network/main thread and read from the render thread.
 */
public final class ClientPhysicsTrainRegistry {

    private static final Map<UUID, PhysicsTrainTag> TAGS = new ConcurrentHashMap<>();

    /** Flat set of every sub-level id belonging to a physics train, for O(1) lookup by render code. */
    private static final Set<UUID> PHYSICS_SUBLEVELS = ConcurrentHashMap.newKeySet();

    private ClientPhysicsTrainRegistry() {
    }

    public static void put(PhysicsTrainTag tag) {
        TAGS.put(tag.trainId(), tag);
        PHYSICS_SUBLEVELS.addAll(tag.subLevelIds());
    }

    public static void remove(UUID trainId) {
        PhysicsTrainTag removed = TAGS.remove(trainId);
        if (removed != null) {
            removed.subLevelIds().forEach(PHYSICS_SUBLEVELS::remove);
        }
    }

    public static void clear() {
        TAGS.clear();
        PHYSICS_SUBLEVELS.clear();
    }

    /** True if the given Sable sub-level id backs a carriage of some physics train. */
    public static boolean isPhysicsSubLevel(@Nullable UUID subLevelId) {
        return subLevelId != null && PHYSICS_SUBLEVELS.contains(subLevelId);
    }

    /**
     * The client carriage entity whose sub-level is {@code subLevelId}, or {@code null}. Resolves
     * sub-level -> train (via the tag's ordered carriage list) -> Create's client train -> entity.
     */
    @Nullable
    public static CarriageContraptionEntity findCarriage(@Nullable UUID subLevelId) {
        if (subLevelId == null) {
            return null;
        }
        for (PhysicsTrainTag tag : TAGS.values()) {
            int idx = tag.subLevelIds().indexOf(subLevelId);
            if (idx < 0) {
                continue;
            }
            Train train = Create.RAILWAYS.trains.get(tag.trainId());
            if (train == null || idx >= train.carriages.size()) {
                return null;
            }
            return train.carriages.get(idx).anyAvailableEntity();
        }
        return null;
    }

    @Nullable
    public static PhysicsTrainTag get(UUID trainId) {
        return TAGS.get(trainId);
    }

    public static boolean isPhysicsTrain(UUID trainId) {
        return TAGS.containsKey(trainId);
    }

    /** Every currently-available client carriage entity that belongs to a physics train. */
    public static List<CarriageContraptionEntity> physicsCarriages() {
        List<CarriageContraptionEntity> out = new ArrayList<>();
        for (PhysicsTrainTag tag : TAGS.values()) {
            Train train = Create.RAILWAYS.trains.get(tag.trainId());
            if (train == null) {
                continue;
            }
            for (Carriage carriage : train.carriages) {
                CarriageContraptionEntity entity = carriage.anyAvailableEntity();
                if (entity != null) {
                    out.add(entity);
                }
            }
        }
        return out;
    }
}
