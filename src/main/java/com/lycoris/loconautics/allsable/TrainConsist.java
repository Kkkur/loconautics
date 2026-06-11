package com.lycoris.loconautics.allsable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyHelper;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Resolves the full <b>consist</b> (the chain of coupled train sub-levels) starting from any one sub-level, and sums
 * its weight. This is what lets a Bearing Axle on the frontmost carriage account for everything it is hauling: the
 * train's own blocks <i>plus</i> every carriage coupled behind it, however many and however coupled.
 *
 * <p>Coupling mechanisms are <b>modular</b>: each registers a {@link CouplingProvider} (steel-cable/rope strands via
 * {@link RopeCouplingProvider}; future knuckle/other couplers just add their own provider). Traversal unions every
 * provider's edges, so a consist held together by a mix of coupler types is still resolved as one whole.
 *
 * <p>All methods are server-side and operate on a connected component, so the answer is independent of which member
 * you start the query from.
 */
public final class TrainConsist {

    /** Registered coupling mechanisms. Copy-on-write: written once at setup, read during traversal. */
    private static final List<CouplingProvider> PROVIDERS = new CopyOnWriteArrayList<>();

    private TrainConsist() {
    }

    /** Registers a coupling mechanism. Call once at common setup (order does not matter; edges are unioned). */
    public static void registerProvider(CouplingProvider provider) {
        if (provider != null) {
            PROVIDERS.add(provider);
        }
    }

    /**
     * Every train sub-level reachable from {@code start} through any registered coupler (the whole consist),
     * including {@code start} itself. A breadth-first flood over the union of all providers' edges.
     */
    public static Set<UUID> connectedSubLevels(ServerLevel level, UUID start) {
        Set<UUID> visited = new HashSet<>();
        if (start == null) {
            return visited;
        }
        visited.add(start);
        Deque<UUID> queue = new ArrayDeque<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            for (CouplingProvider provider : PROVIDERS) {
                provider.collectCoupledSubLevels(level, current, neighbour -> {
                    if (neighbour != null && visited.add(neighbour)) {
                        queue.add(neighbour);
                    }
                });
            }
        }
        return visited;
    }

    /**
     * Total mass (kg) of the whole consist reachable from {@code start}: the summed block mass of every coupled,
     * currently-loaded train sub-level. Unloaded carriages contribute 0 until they load (their mass folds in once
     * the next weight refresh runs while they are present).
     */
    public static double totalMass(ServerLevel level, UUID start) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return 0.0;
        }
        double total = 0.0;
        for (UUID id : connectedSubLevels(level, start)) {
            if (container.getSubLevel(id) instanceof ServerSubLevel sub && !sub.isRemoved()) {
                total += subLevelMass(sub);
            }
        }
        return total;
    }

    /** Summed physics block mass (kg) of a single sub-level — the canonical per-sub-level weight calculation. */
    public static double subLevelMass(ServerSubLevel sub) {
        ServerLevel subLevel = sub.getLevel();
        var bounds = sub.getPlot().getBoundingBox();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        double mass = 0.0;
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    mass += PhysicsBlockPropertyHelper.getMass(subLevel, pos.set(x, y, z),
                            subLevel.getBlockState(pos));
                }
            }
        }
        return mass;
    }
}
