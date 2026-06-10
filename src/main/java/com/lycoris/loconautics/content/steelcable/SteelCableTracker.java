package com.lycoris.loconautics.content.steelcable;

import com.lycoris.loconautics.network.packets.SteelCableStrandPacket;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which rope strand UUIDs were created by the Steel Cable item.
 *
 * SERVER: persisted as a {@link SavedData} in the overworld so UUIDs survive restarts.
 * CLIENT: populated by {@link SteelCableStrandPacket} sent on login and on chunk watch.
 *
 * Both sides share the same static set for fast lookup during rendering/logic.
 */
public final class SteelCableTracker extends SavedData {

    private static final String SAVE_ID = "loconautics_steel_cables";
    private static final String TAG_UUIDS = "uuids";

    // Shared set used on both sides — client populates from packets, server from SavedData.
    private static final Set<UUID> STEEL_CABLE_STRANDS =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static SteelCableTracker serverInstance = null;

    private SteelCableTracker() {}

    // -------------------------------------------------------------------------
    // Static API — used everywhere
    // -------------------------------------------------------------------------

    public static void register(UUID strandUUID) {
        STEEL_CABLE_STRANDS.add(strandUUID);
    }

    public static void unregister(UUID strandUUID) {
        STEEL_CABLE_STRANDS.remove(strandUUID);
    }

    public static boolean isSteelCable(UUID strandUUID) {
        return STEEL_CABLE_STRANDS.contains(strandUUID);
    }

    public static Set<UUID> getAll() {
        return Collections.unmodifiableSet(STEEL_CABLE_STRANDS);
    }

    /** Clear the client-side set (call on disconnect so stale UUIDs don't bleed into the next session). */
    public static void clearClient() {
        STEEL_CABLE_STRANDS.clear();
    }

    // -------------------------------------------------------------------------
    // Server-side persistence
    // -------------------------------------------------------------------------

    /**
     * Called from the server once the overworld is available.
     * Loads the SavedData and populates the static set.
     */
    public static SteelCableTracker getOrCreate(ServerLevel overworld) {
        if (serverInstance == null) {
            SavedData.Factory<SteelCableTracker> factory = new SavedData.Factory<>(
                    SteelCableTracker::new,
                    SteelCableTracker::load,
                    null
            );
            serverInstance = overworld.getDataStorage().computeIfAbsent(factory, SAVE_ID);
        }
        return serverInstance;
    }

    /** Called when the server stops to drop the cached instance. */
    public static void invalidateServer() {
        serverInstance = null;
        STEEL_CABLE_STRANDS.clear();
    }

    /**
     * Register a UUID on the server: adds to the in-memory set, marks dirty for saving,
     * and sends the packet to all tracking players.
     */
    public static void registerServer(ServerLevel overworld, UUID strandUUID) {
        STEEL_CABLE_STRANDS.add(strandUUID);
        SteelCableTracker instance = getOrCreate(overworld);
        instance.setDirty();
        PacketDistributor.sendToAllPlayers(new SteelCableStrandPacket(strandUUID));
    }

    /**
     * Unregister a UUID on the server: removes from the set and marks dirty.
     */
    public static void unregisterServer(ServerLevel overworld, UUID strandUUID) {
        STEEL_CABLE_STRANDS.remove(strandUUID);
        SteelCableTracker instance = getOrCreate(overworld);
        instance.setDirty();
    }

    /**
     * Send the full set of steel cable UUIDs to a single player (called on login).
     */
    public static void syncToPlayer(ServerPlayer player) {
        for (UUID uuid : STEEL_CABLE_STRANDS) {
            PacketDistributor.sendToPlayer(player, new SteelCableStrandPacket(uuid));
        }
    }

    // -------------------------------------------------------------------------
    // SavedData serialization
    // -------------------------------------------------------------------------

    private static SteelCableTracker load(CompoundTag tag, HolderLookup.Provider registries) {
        SteelCableTracker tracker = new SteelCableTracker();
        ListTag list = tag.getList(TAG_UUIDS, Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            try {
                STEEL_CABLE_STRANDS.add(UUID.fromString(list.getString(i)));
            } catch (IllegalArgumentException ignored) {}
        }
        return tracker;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (UUID uuid : STEEL_CABLE_STRANDS) {
            list.add(StringTag.valueOf(uuid.toString()));
        }
        tag.put(TAG_UUIDS, list);
        return tag;
    }
}