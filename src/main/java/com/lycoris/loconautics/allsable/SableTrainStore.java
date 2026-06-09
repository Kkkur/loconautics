package com.lycoris.loconautics.allsable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

/**
 * On-disk persistence for the all-Sable custom trains ({@link SableTrain}). Sable already persists each car's
 * <i>blocks</i> (the sub-level survives a restart on disk), but the {@link SableTrainRegistry} that drives them
 * is in-memory only — so without this the cars come back as un-driven orphans. This {@link SavedData} stores,
 * per train, everything needed to rebuild the {@link RailCarriage}s and re-seat them on the rail
 * ({@link SableTrainPersistence} does the (de)serialisation and the restore).
 *
 * <p>Like Create's railway data (and our hybrid {@code PhysicsTrainRegistry}), it lives on the overworld's data
 * storage so it is shared across dimensions. Each entry is a fully-serialised train compound keyed by train id.
 */
public final class SableTrainStore extends SavedData {

    private static final String DATA_NAME = "loconautics_sable_trains";

    /** trainId -> serialised train compound (see {@link SableTrainPersistence#serialize}). */
    private final Map<UUID, CompoundTag> records = new HashMap<>();

    public SableTrainStore() {
    }

    /** The global store, backed by the overworld's data storage (created on first access). */
    public static SableTrainStore get(MinecraftServer server) {
        DimensionDataStorage storage = server.overworld().getDataStorage();
        return storage.computeIfAbsent(
                new SavedData.Factory<>(SableTrainStore::new, SableTrainStore::load, null), DATA_NAME);
    }

    /** Inserts/updates a train's serialised record. */
    public void put(UUID id, CompoundTag record) {
        records.put(id, record);
        setDirty();
    }

    /** Drops a train's record (when it's cleared or its last car is destroyed). */
    public void remove(UUID id) {
        if (records.remove(id) != null) {
            setDirty();
        }
    }

    /** Live view of all stored records (trainId -> compound). */
    public Map<UUID, CompoundTag> records() {
        return records;
    }

    // ----- Persistence -----

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (CompoundTag record : records.values()) {
            list.add(record.copy());
        }
        tag.put("Trains", list);
        return tag;
    }

    private static SableTrainStore load(CompoundTag tag, HolderLookup.Provider registries) {
        SableTrainStore store = new SableTrainStore();
        ListTag list = tag.getList("Trains", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag record = list.getCompound(i);
            if (record.hasUUID("Id")) {
                store.records.put(record.getUUID("Id"), record);
            }
        }
        return store;
    }
}
