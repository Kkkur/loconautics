package com.lycoris.loconautics.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.lycoris.loconautics.core.PhysicsTrainTag;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

/**
 * Server-side, world-global registry of which trains are in Sable physics mode.
 *
 * <p>Create's train data is global (managed by {@code Create.RAILWAYS}), so we mirror that: this
 * {@link SavedData} is attached to the overworld's data storage and shared across dimensions.
 * It persists the {@code trainId -> PhysicsTrainTag} mapping so physics trains survive restarts.
 */
public final class PhysicsTrainRegistry extends SavedData {

    private static final String DATA_NAME = "loconautics_physics_trains";

    private final Map<UUID, PhysicsTrainTag> tags = new HashMap<>();

    public PhysicsTrainRegistry() {
    }

    // ----- Access -----

    /** Returns the global registry, creating it if needed. Always backed by the overworld storage. */
    public static PhysicsTrainRegistry get(MinecraftServer server) {
        DimensionDataStorage storage = server.overworld().getDataStorage();
        return storage.computeIfAbsent(factory(), DATA_NAME);
    }

    public static PhysicsTrainRegistry get(ServerLevel level) {
        return get(level.getServer());
    }

    private static SavedData.Factory<PhysicsTrainRegistry> factory() {
        return new SavedData.Factory<>(PhysicsTrainRegistry::new, PhysicsTrainRegistry::load, null);
    }

    // ----- Mutation -----

    public void register(PhysicsTrainTag tag) {
        tags.put(tag.trainId(), tag);
        setDirty();
    }

    @Nullable
    public PhysicsTrainTag unregister(UUID trainId) {
        PhysicsTrainTag removed = tags.remove(trainId);
        if (removed != null) {
            setDirty();
        }
        return removed;
    }

    // ----- Queries -----

    @Nullable
    public PhysicsTrainTag get(UUID trainId) {
        return tags.get(trainId);
    }

    public boolean isPhysicsTrain(UUID trainId) {
        return tags.containsKey(trainId);
    }

    public Collection<PhysicsTrainTag> all() {
        return tags.values();
    }

    // ----- Persistence -----

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (PhysicsTrainTag t : tags.values()) {
            list.add(t.toNbt());
        }
        tag.put("Trains", list);
        return tag;
    }

    private static PhysicsTrainRegistry load(CompoundTag tag, HolderLookup.Provider registries) {
        PhysicsTrainRegistry registry = new PhysicsTrainRegistry();
        ListTag list = tag.getList("Trains", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            PhysicsTrainTag t = PhysicsTrainTag.fromNbt(list.getCompound(i));
            registry.tags.put(t.trainId(), t);
        }
        return registry;
    }
}
