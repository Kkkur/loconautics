package com.lycoris.loconautics.content.boiler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Boiler Controller Block Entity — the brain of the steam boiler multiblock.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Triggers and caches the multiblock scan ({@link BoilerMultiblockValidator})</li>
 *   <li>Owns the boiler state machine ({@link BoilerState})</li>
 *   <li>Drives the tick loop: warmup timer, fuel consumption delegation to fireboxes,
 *       SU output calculation (Phase 3 / 4)</li>
 *   <li>Tracks water level and damage counter (Phase 4)</li>
 * </ul>
 *
 * <h3>Warmup</h3>
 * {@code COLD → WARMING} when a firebox begins burning.
 * {@code WARMING → HOT} after {@link #WARMUP_TICKS} ticks of continuous burn.
 * Progress is linear; SU output scales from {@link #COLD_OUTPUT_FRACTION}
 * to 1.0 over the warmup window.
 *
 * <h3>Starvation</h3>
 * If all fireboxes run out of fuel the state transitions to {@code STARVED} and
 * then cools back to {@code COLD} over {@link #COOLDOWN_TICKS}.
 * Water starvation is handled in Phase 4.
 */
public class BoilerControllerBlockEntity extends BlockEntity {

    // ------------------------------------------------------------------ constants

    /** Ticks to warm from COLD to HOT (~90 seconds at 20 tps). */
    public static final int WARMUP_TICKS = 1800;

    /** Ticks to cool from HOT/WARMING back to COLD after fuel runs out. */
    public static final int COOLDOWN_TICKS = 1200;

    /** SU output fraction while COLD (20%). */
    public static final float COLD_OUTPUT_FRACTION = 0.2f;

    // ------------------------------------------------------------------ state

    private BoilerState boilerState = BoilerState.COLD;

    /**
     * Warmup progress in ticks. Counts up from 0 → {@link #WARMUP_TICKS} while WARMING.
     * Counts down from current value → 0 while cooling (STARVED with no water, or COLD).
     */
    private int warmupTicks = 0;

    /** Cached structure from the last successful multiblock scan. Null if invalid. */
    private BoilerStructure structure = null;

    // ------------------------------------------------------------------ structure cache (NBT-friendly)

    private int cachedBodyCount    = 0;
    private int cachedFireboxCount = 0;
    private float cachedEfficiency = 0f;
    private List<BlockPos> cachedBodyPositions    = new ArrayList<>();
    private List<BlockPos> cachedFireboxPositions = new ArrayList<>();

    // ------------------------------------------------------------------ constructor

    public BoilerControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ------------------------------------------------------------------ multiblock

    /**
     * Called by {@link BoilerControllerBlock} when the block is placed or a neighbour changes.
     * Re-runs the multiblock scan and updates cached structure data.
     */
    public void onStructureChanged() {
        if (level == null || level.isClientSide) return;

        Optional<BoilerStructure> result = BoilerMultiblockValidator.validate(level, worldPosition);
        if (result.isPresent()) {
            structure              = result.get();
            cachedBodyCount        = structure.bodyCount();
            cachedFireboxCount     = structure.fireboxCount();
            cachedEfficiency       = structure.ratioEfficiency();
            cachedBodyPositions    = new ArrayList<>(structure.bodyPositions());
            cachedFireboxPositions = new ArrayList<>(structure.fireboxPositions());
        } else {
            structure              = null;
            cachedBodyCount        = 0;
            cachedFireboxCount     = 0;
            cachedEfficiency       = 0f;
            cachedBodyPositions    = new ArrayList<>();
            cachedFireboxPositions = new ArrayList<>();
            // No valid structure — transition to COLD immediately
            boilerState = BoilerState.COLD;
            warmupTicks = 0;
        }
        setChanged();
        sendData();
    }

    /** Returns true when the multiblock is fully formed and viable. */
    public boolean hasValidStructure() {
        return structure != null;
    }

    // ------------------------------------------------------------------ tick

    public void tick() {
        if (level == null || level.isClientSide) return;
        if (!hasValidStructure()) return;

        boolean anyFireboxBurning = tickFireboxes();
        tickStateMachine(anyFireboxBurning);
    }

    /**
     * Asks each firebox BE to consume one tick of fuel.
     * Returns true if at least one firebox is actively burning.
     */
    private boolean tickFireboxes() {
        boolean anyBurning = false;
        for (BlockPos fireboxPos : cachedFireboxPositions) {
            BlockEntity be = level.getBlockEntity(fireboxPos);
            if (be instanceof FireboxBlockEntity firebox) {
                if (firebox.consumeFuel()) {
                    anyBurning = true;
                }
            }
        }
        return anyBurning;
    }

    /**
     * Advances the boiler state machine one tick.
     *
     * <pre>
     *   COLD    + burning → WARMING
     *   WARMING + burning → warmupTicks++; if full → HOT
     *   WARMING + no fuel → warmupTicks--; if 0 → COLD
     *   HOT     + burning → stay HOT
     *   HOT     + no fuel → STARVED
     *   STARVED + burning → WARMING (resume from current warmupTicks)
     *   STARVED + no fuel → warmupTicks--; if 0 → COLD
     * </pre>
     */
    private void tickStateMachine(boolean burning) {
        BoilerState prev = boilerState;

        switch (boilerState) {
            case COLD -> {
                if (burning) {
                    boilerState = BoilerState.WARMING;
                    warmupTicks = 0;
                }
            }
            case WARMING -> {
                if (burning) {
                    warmupTicks++;
                    if (warmupTicks >= WARMUP_TICKS) {
                        warmupTicks = WARMUP_TICKS;
                        boilerState = BoilerState.HOT;
                    }
                } else {
                    warmupTicks = Math.max(0, warmupTicks - 1);
                    if (warmupTicks == 0) boilerState = BoilerState.COLD;
                }
            }
            case HOT -> {
                if (!burning) boilerState = BoilerState.STARVED;
            }
            case STARVED -> {
                if (burning) {
                    // Resume warming from current heat rather than resetting to 0
                    boilerState = BoilerState.WARMING;
                } else {
                    warmupTicks = Math.max(0, warmupTicks - 1);
                    if (warmupTicks == 0) boilerState = BoilerState.COLD;
                }
            }
        }

        if (boilerState != prev) {
            setChanged();
            sendData();
        }
    }

    // ------------------------------------------------------------------ SU output

    /**
     * Current SU output fraction (0.0–1.0) based on boiler state and warmup progress.
     * Phase 4 will multiply this by the base SU capacity derived from body block count.
     */
    public float getOutputFraction() {
        return switch (boilerState) {
            case COLD    -> 0f;
            case WARMING -> COLD_OUTPUT_FRACTION + (1f - COLD_OUTPUT_FRACTION) * (warmupTicks / (float) WARMUP_TICKS);
            case HOT     -> 1f;
            case STARVED -> COLD_OUTPUT_FRACTION * ((float) warmupTicks / WARMUP_TICKS);
        };
    }

    // ------------------------------------------------------------------ accessors

    public BoilerState getBoilerState()  { return boilerState; }
    public int getWarmupTicks()          { return warmupTicks; }
    public int getCachedBodyCount()      { return cachedBodyCount; }
    public int getCachedFireboxCount()   { return cachedFireboxCount; }
    public float getCachedEfficiency()   { return cachedEfficiency; }

    // ------------------------------------------------------------------ NBT

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("BoilerState", boilerState.name());
        tag.putInt("WarmupTicks", warmupTicks);
        tag.putInt("BodyCount", cachedBodyCount);
        tag.putInt("FireboxCount", cachedFireboxCount);
        tag.putFloat("Efficiency", cachedEfficiency);
        tag.put("BodyPositions",    posListToNbt(cachedBodyPositions));
        tag.put("FireboxPositions", posListToNbt(cachedFireboxPositions));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        try {
            boilerState = BoilerState.valueOf(tag.getString("BoilerState"));
        } catch (IllegalArgumentException e) {
            boilerState = BoilerState.COLD;
        }
        warmupTicks        = tag.getInt("WarmupTicks");
        cachedBodyCount    = tag.getInt("BodyCount");
        cachedFireboxCount = tag.getInt("FireboxCount");
        cachedEfficiency   = tag.getFloat("Efficiency");
        cachedBodyPositions    = posListFromNbt(tag.getList("BodyPositions",    Tag.TAG_LONG));
        cachedFireboxPositions = posListFromNbt(tag.getList("FireboxPositions", Tag.TAG_LONG));

        // Rebuild the structure record from cached data (no level access needed here)
        if (cachedBodyCount >= 3 && cachedFireboxCount >= 1) {
            structure = new BoilerStructure(
                    worldPosition,
                    List.copyOf(cachedBodyPositions),
                    List.copyOf(cachedFireboxPositions),
                    cachedBodyCount,
                    cachedFireboxCount,
                    cachedEfficiency
            );
        }
    }

    // ------------------------------------------------------------------ NBT helpers

    private static ListTag posListToNbt(List<BlockPos> positions) {
        ListTag list = new ListTag();
        for (BlockPos pos : positions) {
            list.add(LongTag.valueOf(pos.asLong()));
        }
        return list;
    }

    private static List<BlockPos> posListFromNbt(ListTag list) {
        List<BlockPos> result = new ArrayList<>(list.size());
        for (Tag tag : list) {
            result.add(BlockPos.of(((LongTag) tag).getAsLong()));
        }
        return result;
    }

    /** Sends a data packet to the client (used for state change sync). */
    private void sendData() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}