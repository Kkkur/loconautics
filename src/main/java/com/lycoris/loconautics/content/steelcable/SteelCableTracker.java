package com.lycoris.loconautics.content.steelcable;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which rope strand UUIDs were created by the Steel Cable item.
 *
 * On the server side, UUIDs are added by {@link SteelCableItem} at strand creation.
 * On the client side, the mixin {@code RopeConnectorRendererMixin} reads this set to
 * decide whether to delegate rendering to {@link SteelCableStrandRenderer}.
 *
 * This is intentionally a simple in-memory set — it does not survive server restarts,
 * which is fine because the client re-receives strand data on world join anyway.
 */
public final class SteelCableTracker {

    private static final Set<UUID> STEEL_CABLE_STRANDS =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    private SteelCableTracker() {}

    public static void register(UUID strandUUID) {
        STEEL_CABLE_STRANDS.add(strandUUID);
    }

    public static void unregister(UUID strandUUID) {
        STEEL_CABLE_STRANDS.remove(strandUUID);
    }

    public static boolean isSteelCable(UUID strandUUID) {
        return STEEL_CABLE_STRANDS.contains(strandUUID);
    }
}