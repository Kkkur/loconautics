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
        float rpm = (float) Math.ceil(redstonePower * 256.0 / 15.0);
        return inputDirection >= 0 ? rpm : -rpm;
    }

    /**
     * Stress impact for the output shaft network.
     * Create multiplies this by abs(outputRPM) to get the full SU cost.
     */
    @Override
    public float calculateStressApplied() {
        return (float) Config.TRANSMISSION_SU_IMPACT.get();
    }

    // ------------------------------------------------------------------ API

    /**
     * Called by {@link TransmissionBlock#neighborChanged} when the surrounding redstone
     * signal changes. Updates stored power, reads input direction, refreshes the kinetic
     * network, and syncs the STAGE blockstate property for the visual variant.
     *
     * Must only run on the server (caller guards this).
     */
    public void setRedstonePower(int power) {
        this.redstonePower = power;
        readInputDirection();
        updateGeneratedRotation();  // notify Create the output speed changed
        updateStageBlockstate(power);
        setChanged();
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

        // Check both faces on the axis; the one with a kinetic neighbour is the input.
        for (Direction dir : Direction.values()) {
            if (dir.getAxis() != axis) continue;

            BlockPos neighbourPos = worldPosition.relative(dir);
            if (level.getBlockEntity(neighbourPos) instanceof com.simibubi.create.content.kinetics.base.KineticBlockEntity kbe) {
                float neighbourSpeed = kbe.getSpeed();
                if (neighbourSpeed != 0) {
                    inputDirection = neighbourSpeed > 0 ? 1 : -1;
                    return;
                }
            }
        }
        // No spinning neighbour found — keep last known direction (or default +1).
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
            level.setBlock(worldPosition, current.setValue(TransmissionBlock.STAGE, stage), 3);
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