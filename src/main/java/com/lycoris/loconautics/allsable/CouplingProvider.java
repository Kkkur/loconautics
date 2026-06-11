package com.lycoris.loconautics.allsable;

import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.server.level.ServerLevel;

/**
 * One coupling mechanism's view of which train sub-levels are directly joined to a given sub-level. Implementations
 * report the immediate neighbours linked by their kind of coupler (steel-cable/rope strands today, knuckle couplers
 * or other devices in future), and {@link TrainConsist} stitches every provider's edges together into the full
 * consist for weight (and any future) calculations.
 *
 * <p>Edges are treated as undirected: a coupler between sub-levels A and B should report B when asked about A and A
 * when asked about B. Providers must never report {@code null} or the queried sub-level itself; both are ignored.
 *
 * <p>Register a provider once at setup via {@link TrainConsist#registerProvider}.
 */
@FunctionalInterface
public interface CouplingProvider {

    /**
     * Reports every sub-level directly coupled to {@code subLevelId} by this mechanism, passing each neighbour's
     * UUID to {@code neighbours}. Called on the server thread; should be cheap (it runs during consist traversal).
     *
     * @param level       the server level the sub-levels live in
     * @param subLevelId  the sub-level whose direct couplings are being queried
     * @param neighbours  sink for each directly-coupled neighbour's sub-level UUID
     */
    void collectCoupledSubLevels(ServerLevel level, UUID subLevelId, Consumer<UUID> neighbours);
}
