/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.chainDrive;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChainGearshiftBlockEntity
extends KineticBlockEntity {
    int signal = 0;
    boolean signalChanged;

    public ChainGearshiftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(40);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("Signal", this.signal);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.signal = compound.getInt("Signal");
        super.read(compound, registries, clientPacket);
    }

    public float getModifier() {
        return this.getModifierForSignal(this.signal);
    }

    public void neighbourChanged() {
        if (!this.hasLevel()) {
            return;
        }
        int power = this.level.getBestNeighborSignal(this.worldPosition);
        if (power != this.signal) {
            this.signalChanged = true;
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        this.neighbourChanged();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            return;
        }
        if (this.signalChanged) {
            this.signalChanged = false;
            this.analogSignalChanged(this.level.getBestNeighborSignal(this.worldPosition));
        }
    }

    protected void analogSignalChanged(int newSignal) {
        this.detachKinetics();
        this.removeSource();
        this.signal = newSignal;
        this.attachKinetics();
    }

    protected float getModifierForSignal(int newPower) {
        if (newPower == 0) {
            return 1.0f;
        }
        return 1.0f + (float)(newPower + 1) / 16.0f;
    }
}
