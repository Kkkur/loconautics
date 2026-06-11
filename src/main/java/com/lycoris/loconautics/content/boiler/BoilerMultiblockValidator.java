package com.lycoris.loconautics.content.boiler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

/**
 * Validates whether a steam boiler multiblock is correctly formed around a given
 * controller position, and returns a {@link BoilerStructure} snapshot if so.
 *
 * <h3>Rules</h3>
 * <ul>
 *   <li>The controller must be adjacent (face-to-face) to at least one boiler body block.</li>
 *   <li>Body blocks form a connected group (6-connected flood-fill, max {@link #MAX_BODY_BLOCKS}).</li>
 *   <li>Firebox blocks must be directly adjacent to at least one body block in the group.</li>
 *   <li>Minimum viable build: 1 firebox + 3 body blocks.</li>
 *   <li>No hard upper limit on size, but the scan caps at {@link #MAX_BODY_BLOCKS} to prevent
 *       runaway world iteration.</li>
 * </ul>
 *
 * <h3>Scan algorithm</h3>
 * Starting from the controller, do a BFS/flood-fill through adjacent blocks:
 * body blocks are added to the body set; firebox blocks adjacent to any body block
 * are collected into the firebox set. Other block types break connectivity (the body
 * group is only contiguous body blocks).
 */
public final class BoilerMultiblockValidator {

    /** Safety cap: never scan more than this many body blocks to avoid lag. */
    public static final int MAX_BODY_BLOCKS = 128;

    private BoilerMultiblockValidator() {}

    /**
     * Validates the boiler multiblock rooted at {@code controllerPos}.
     *
     * @param level         The server-side level.
     * @param controllerPos Position of the boiler controller block.
     * @return An {@link Optional} containing the structure if valid, empty otherwise.
     */
    public static Optional<BoilerStructure> validate(Level level, BlockPos controllerPos) {
        if (level == null || level.isClientSide) return Optional.empty();

        List<BlockPos> bodyPositions = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> fireboxSet = new HashSet<>();

        // BFS flood-fill: starting from the controller's neighbours, collect all
        // directly-connected body blocks. Firebox blocks adjacent to any body block
        // are harvested at each step.
        Queue<BlockPos> frontier = new ArrayDeque<>();

        // Seed: neighbours of the controller that are body blocks
        for (Direction dir : Direction.values()) {
            BlockPos neighbour = controllerPos.relative(dir);
            BlockState ns = level.getBlockState(neighbour);
            if (ns.is(BoilerBlocks.BOILER_BODY.get()) && visited.add(neighbour)) {
                frontier.add(neighbour);
            }
        }

        if (frontier.isEmpty()) {
            // Controller has no adjacent body block — not a valid multiblock
            return Optional.empty();
        }

        while (!frontier.isEmpty() && bodyPositions.size() < MAX_BODY_BLOCKS) {
            BlockPos current = frontier.poll();
            bodyPositions.add(current);

            for (Direction dir : Direction.values()) {
                BlockPos neighbour = current.relative(dir);
                if (visited.contains(neighbour)) continue;
                // Skip the controller position itself
                if (neighbour.equals(controllerPos)) continue;

                BlockState ns = level.getBlockState(neighbour);

                if (ns.is(BoilerBlocks.BOILER_BODY.get())) {
                    visited.add(neighbour);
                    frontier.add(neighbour);
                } else if (ns.is(BoilerBlocks.FIREBOX.get())) {
                    // Firebox adjacent to a body block — collect it (don't flood into it)
                    fireboxSet.add(neighbour);
                    visited.add(neighbour);
                }
            }
        }

        List<BlockPos> fireboxPositions = new ArrayList<>(fireboxSet);

        int bodyCount    = bodyPositions.size();
        int fireboxCount = fireboxPositions.size();

        // Minimum viable structure: at least 1 firebox, 3 body blocks
        if (bodyCount < 3 || fireboxCount < 1) {
            return Optional.empty();
        }

        float efficiency = BoilerStructure.computeEfficiency(fireboxCount, bodyCount);

        return Optional.of(new BoilerStructure(
                controllerPos,
                List.copyOf(bodyPositions),
                List.copyOf(fireboxPositions),
                bodyCount,
                fireboxCount,
                efficiency
        ));
    }
}