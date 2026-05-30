package com.lycoris.loconautics.server.assembly;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Carries state across the two halves of a physics assembly that happen at different points in
 * Create's {@code StationBlockEntity.assemble}:
 *
 * <ol>
 *   <li>the {@code removeBlocksFromWorld} redirect (per carriage) creates sub-levels and records
 *       their ids here;</li>
 *   <li>the orchestrator, after {@code assemble} returns, reads those ids back to build the
 *       {@link com.lycoris.loconautics.core.PhysicsTrainTag}.</li>
 * </ol>
 *
 * <p>Train assembly runs synchronously on the server thread and is bracketed by
 * {@link #begin()}/{@link #end()}, so simple static state is sufficient and safe.
 */
public final class PhysicsAssemblyContext {

    private static boolean pending = false;
    private static final List<UUID> createdSubLevels = new ArrayList<>();

    private PhysicsAssemblyContext() {
    }

    /** Marks the start of a physics assembly. Call right before {@code station.assemble(...)}. */
    public static void begin() {
        pending = true;
        createdSubLevels.clear();
    }

    /** True while a physics assembly is in progress (read by the mixin). */
    public static boolean isPending() {
        return pending;
    }

    /** Records a sub-level created for one carriage. */
    public static void addSubLevel(UUID subLevelId) {
        createdSubLevels.add(subLevelId);
    }

    /** Returns a copy of the sub-levels created during this assembly. */
    public static List<UUID> drain() {
        return new ArrayList<>(createdSubLevels);
    }

    /** Ends the physics assembly and clears state. Always call in a finally block. */
    public static void end() {
        pending = false;
        createdSubLevels.clear();
    }
}
