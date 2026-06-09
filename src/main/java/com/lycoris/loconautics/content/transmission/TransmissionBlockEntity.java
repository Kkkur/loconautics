package com.lycoris.loconautics.content.transmission;

import com.lycoris.loconautics.Config;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity for the Transmission.
 *
 * Extends {@link GeneratingKineticBlockEntity} so Create treats the output shaft as
 * a speed source (generator side). The input shaft is read purely for its direction
 * sign and contributes no stress of its own.
 *
 * Speed formula:
 *   signal == 0  → output speed = 0 (disengaged)
 *   signal 1–15 → outputRPM = ceil(signal * 256.0 / 15.0)
 *   sign of outputRPM = sign of input shaft speed (carries direction through)
 *
 * Stress:
 *   SU = Config.TRANSMISSION_SU_IMPACT (returned from calculateStressApplied).
 *   Create multiplies this by abs(outputRPM) automatically.
 */
public class TransmissionBlockEntity extends GeneratingKineticBlockEntity {

    // ------------------------------------------------------------------ state

    /** Current redstone signal received by the block (0–15). Persisted to NBT. */
    private int redstonePower = 0;

    /**
     * Cached sign of the input shaft's speed (+1 or -1, never 0).
     * Defaults to +1 when no source is connected.
     * Updated each time redstone changes and on tick via {@link #readInputDirection()}.
     */
    private int inputDirection = 1;

    /**
     * Whether a live (spinning) kinetic source was found on the input face during the
     * last {@link #readInputDirection()} call. When false and redstone is on, we still
     * return speed 0 — the block won't self-stress the network without an actual input.
     */
    private boolean hasLiveInput = false;

    // ------------------------------------------------------------------ constructor

    public TransmissionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ------------------------------------------------------------------ GeneratingKineticBlockEntity

    /**
     * Returns the signed output RPM that Create will push into the output kinetic network.
     *
     * Called by Create's kinetic engine whenever it needs to know the current speed.
     * Must be fast and side-effect-free.
     */
    @Override
    public float getGeneratedSpeed() {
        if (redstonePower == 0) return 0f;
        if (!hasLiveInput) return 0f;   // no spinning input → don't self-generate
        float rpm = (float) Math.ceil(redstonePower * 256.0 / 15.0);
        return inputDirection >= 0 ? rpm : -rpm;
    }

    /**
     * Stress impact for the output shaft network.
     * Create multiplies this by abs(outputRPM) to get the full SU cost.
     */
    @Override
    public float calculateStressApplied() {
        return Config.TRANSMISSION_SU_IMPACT.get().floatValue();
    }

    /**
     * SU capacity provided to the output network.
     * Without this, GeneratingKineticBlockEntity adds zero capacity — any stress
     * at all (including its own) immediately overstresses the network.
     * We pass through the capacity from config so the Transmission acts as a
     * proper power relay, not a dead-end consumer.
     */
    @Override
    public float calculateAddedStressCapacity() {
        float capacity = Config.TRANSMISSION_SU_CAPACITY.get().floatValue();
        this.lastCapacityProvided = capacity;
        return capacity;
    }

    // ------------------------------------------------------------------ API

    /**
     * Called by {@link TransmissionBlock#neighborChanged} when the surrounding redstone
     * signal changes. Updates stored power, reads input direction, refreshes the kinetic
     * network, and syncs the STAGE blockstate property for the visual variant.
     *
     * Must only run on the server (caller guards this).
     */
    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;
        // Keep hasLiveInput current every tick so getGeneratedSpeed() is always accurate.
        // Without this, loading a world with a motor already running would leave hasLiveInput=false.
        boolean wasLive = hasLiveInput;
        readInputDirection();
        if (hasLiveInput != wasLive) {
            updateGeneratedRotation();
            setChanged();
        }
    }

    private boolean updatingPower = false;

    public void setRedstonePower(int power) {
        if (updatingPower) return; // guard against recursive neighborChanged re-entry
        updatingPower = true;
        try {
            boolean wasDisengaged = this.redstonePower == 0;
            this.redstonePower = power;
            readInputDirection();
            updateStageBlockstate(power);   // expose/hide shafts FIRST so Create sees correct topology
            if (wasDisengaged && power != 0) {
                // Shafts just became visible — force Create to rebuild the kinetic network
                // from scratch so it picks up the now-connected neighbours.
                detachKinetics();
            }
            updateGeneratedRotation();      // notify Create the output speed changed
            setChanged();
        } finally {
            updatingPower = false;
        }
    }

    // ------------------------------------------------------------------ internals

    /**
     * Reads the speed of the block on the input (back) face of this transmission.
     *
     * The input face is the side opposite the output face.
     * We determine the axis from the blockstate, then look at both ends:
     *   - one end is the output (front), the other is the input (back).
     * We read the adjacent BE's getSpeed() to get the raw network speed there,
     * which gives us direction without being affected by our own getGeneratedSpeed().
     *
     * Falls back to the current inputDirection (keeping last known) if no source found.
     */
    private void readInputDirection() {
        if (level == null || level.isClientSide) return;

        BlockState state = getBlockState();
        Direction.Axis axis = state.getValue(TransmissionBlock.AXIS);

        for (Direction dir : Direction.values()) {
            if (dir.getAxis() != axis) continue;

            BlockPos neighbourPos = worldPosition.relative(dir);

            // If this block has a kinetic source assigned, only read from that side.
            // This prevents the output network's speed from bleeding back in as "input".
            if (source != null && !neighbourPos.equals(source)) continue;

            if (level.getBlockEntity(neighbourPos) instanceof com.simibubi.create.content.kinetics.base.KineticBlockEntity kbe) {
                float neighbourSpeed = kbe.getSpeed();
                if (neighbourSpeed != 0) {
                    inputDirection = neighbourSpeed > 0 ? 1 : -1;
                    hasLiveInput = true;
                    return;
                }
            }
        }
        hasLiveInput = false;
    }

    /**
     * Writes the STAGE property into the blockstate so the correct texture variant renders.
     * Flag 3 = update neighbours + send to clients.
     */
    private void updateStageBlockstate(int power) {
        if (level == null || level.isClientSide) return;
        BlockState current = getBlockState();
        int stage = TransmissionBlock.powerToStage(power);
        if (current.getValue(TransmissionBlock.STAGE) != stage) {
            level.setBlock(worldPosition, current.setValue(TransmissionBlock.STAGE, stage), 2);
        }
    }

    // ------------------------------------------------------------------ NBT

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("RedstonePower", redstonePower);
        tag.putInt("InputDirection", inputDirection);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        redstonePower = tag.getInt("RedstonePower");
        inputDirection = tag.contains("InputDirection") ? tag.getInt("InputDirection") : 1;
    }
}