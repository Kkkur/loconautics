package com.lycoris.loconautics.content.boiler;

import net.minecraft.core.BlockPos;

import java.util.List;

/**
 * Immutable snapshot of a validated boiler multiblock structure, produced by
 * {@link BoilerMultiblockValidator}. Passed to the controller BE after each
 * successful scan so it can cache counts without re-scanning every tick.
 *
 * @param controllerPos  Position of the controller block that owns this structure.
 * @param bodyPositions  All body block positions found in the structure.
 * @param fireboxPositions All firebox block positions found in the structure.
 * @param bodyCount      Number of boiler body blocks.
 * @param fireboxCount   Number of firebox blocks.
 * @param ratioEfficiency Combustion efficiency (0.0–1.0) derived from the firebox ratio.
 *                        1.0 = optimal (1 firebox per 3 body blocks).
 *                        <1.0 = underfired (too few fireboxes, output capped).
 *                        Overfired ratios still return 1.0 — no output benefit,
 *                        but coal waste is handled separately via extra firebox burn.
 */
public record BoilerStructure(
        BlockPos controllerPos,
        List<BlockPos> bodyPositions,
        List<BlockPos> fireboxPositions,
        int bodyCount,
        int fireboxCount,
        float ratioEfficiency
) {
    /** Optimal firebox-to-body ratio: 1 firebox per 3 body blocks. */
    public static final int OPTIMAL_BODY_PER_FIREBOX = 3;

    /**
     * Computes the ratio efficiency for given counts.
     *
     * <ul>
     *   <li>Optimal (1:3 or better fireboxes) → 1.0</li>
     *   <li>Underfired → actual / optimal, clamped to [0, 1]</li>
     * </ul>
     */
    public static float computeEfficiency(int fireboxCount, int bodyCount) {
        if (fireboxCount <= 0 || bodyCount <= 0) return 0f;
        float optimal = bodyCount / (float) OPTIMAL_BODY_PER_FIREBOX;
        if (fireboxCount >= optimal) return 1.0f;
        return fireboxCount / optimal;
    }

    /** Returns true when this is a minimum viable build (at least 1 firebox, 3 body blocks). */
    public boolean isViable() {
        return fireboxCount >= 1 && bodyCount >= 3;
    }

    /** Returns true when there are more fireboxes than the optimal ratio requires (wasteful but not harmful). */
    public boolean isOverfired() {
        return fireboxCount > (float) bodyCount / OPTIMAL_BODY_PER_FIREBOX;
    }
}