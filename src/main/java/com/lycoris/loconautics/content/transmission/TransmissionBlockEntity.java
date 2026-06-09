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
 * Two redstone inputs:
 *   Speed face  (minus side) → controls output RPM (0–15 → 0–256)
 *   Dir face    (plus side)  → when ON, reverses output direction vs input
 *
 * When speed signal == 0 → output = 0 (disengaged, shafts hidden).
 * When directionActive → output direction is flipped relative to input shaft.
 */
public class TransmissionBlockEntity extends GeneratingKineticBlockEntity {

    // ------------------------------------------------------------------ state

    /** Current speed redstone signal (0–15). */
    private int redstonePower = 0;

    /** Whether the direction-override signal is active. */
    private boolean directionActive = false;

    /** Cached sign of the input shaft's speed (+1 or -1). */
    private int inputDirection = 1;

    /** Whether a live kinetic source was found on the input face. */
    private boolean hasLiveInput = false;

    // ------------------------------------------------------------------ constructor

    public TransmissionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ------------------------------------------------------------------ GeneratingKineticBlockEntity

    @Override
    public float getGeneratedSpeed() {
        if (redstonePower == 0) return 0f;
        if (!hasLiveInput) return 0f;
        float rpm = (float) Math.ceil(redstonePower * 256.0 / 15.0);
        // Apply direction: normal = follow input, active = reverse vs input
        int effectiveDir = directionActive ? -inputDirection : inputDirection;
        return effectiveDir >= 0 ? rpm : -rpm;
    }

    @Override
    public float calculateStressApplied() {
        return Config.TRANSMISSION_SU_IMPACT.get().floatValue();
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = Config.TRANSMISSION_SU_CAPACITY.get().floatValue();
        this.lastCapacityProvided = capacity;
        return capacity;
    }

    // ------------------------------------------------------------------ tick

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;
        boolean wasLive = hasLiveInput;
        readInputDirection();
        if (hasLiveInput != wasLive) {
            updateGeneratedRotation();
            setChanged();
        }
    }

    // ------------------------------------------------------------------ API

    private boolean updatingPower = false;

    /**
     * Called by TransmissionBlock#neighborChanged with both signals.
     */
    public void setRedstonePower(int speedPower, boolean dirActive) {
        if (updatingPower) return;
        updatingPower = true;
        try {
            boolean wasDisengaged = this.redstonePower == 0;
            boolean dirChanged = this.directionActive != dirActive;

            this.redstonePower = speedPower;
            this.directionActive = dirActive;

            readInputDirection();
            updateStageBlockstate(speedPower, dirActive);

            if (wasDisengaged && speedPower != 0) {
                detachKinetics();
            } else if (dirChanged) {
                // Direction flip needs a full kinetic rebuild so Create re-propagates signs
                detachKinetics();
            }

            updateGeneratedRotation();
            setChanged();
        } finally {
            updatingPower = false;
        }
    }

    // ------------------------------------------------------------------ internals

    private void readInputDirection() {
        if (level == null || level.isClientSide) return;

        BlockState state = getBlockState();
        Direction.Axis axis = state.getValue(TransmissionBlock.FACING).getAxis();

        for (Direction dir : Direction.values()) {
            if (dir.getAxis() != axis) continue;

            BlockPos neighbourPos = worldPosition.relative(dir);
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

    private void updateStageBlockstate(int power, boolean dirActive) {
        if (level == null || level.isClientSide) return;
        BlockState current = getBlockState();
        int stage = TransmissionBlock.powerToStage(power);
        BlockState updated = current
                .setValue(TransmissionBlock.STAGE, stage)
                .setValue(TransmissionBlock.DIRECTION_ACTIVE, dirActive);
        if (!updated.equals(current)) {
            level.setBlock(worldPosition, updated, 2);
        }
    }

    // ------------------------------------------------------------------ NBT

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("RedstonePower", redstonePower);
        tag.putInt("InputDirection", inputDirection);
        tag.putBoolean("DirectionActive", directionActive);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        redstonePower = tag.getInt("RedstonePower");
        inputDirection = tag.contains("InputDirection") ? tag.getInt("InputDirection") : 1;
        directionActive = tag.contains("DirectionActive") && tag.getBoolean("DirectionActive");
    }
}