/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.minecart;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class TrainCargoManager
extends MountedStorageManager {
    int ticksSinceLastExchange = 0;
    AtomicInteger version = new AtomicInteger();

    @Override
    public void initialize() {
        super.initialize();
        this.items = new CargoInvWrapper(this.items);
        this.allItems = this.items;
        if (this.fuelItems != null) {
            this.fuelItems = new CargoInvWrapper(this.fuelItems);
        }
        this.fluids = new CargoTankWrapper(this.fluids);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        nbt.putInt("TicksSinceLastExchange", this.ticksSinceLastExchange);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, @Nullable Contraption contraption) {
        super.read(nbt, registries, clientPacket, contraption);
        this.ticksSinceLastExchange = nbt.getInt("TicksSinceLastExchange");
    }

    public void resetIdleCargoTracker() {
        this.ticksSinceLastExchange = 0;
    }

    public void tickIdleCargoTracker() {
        ++this.ticksSinceLastExchange;
    }

    public int getTicksSinceLastExchange() {
        return this.ticksSinceLastExchange;
    }

    public int getVersion() {
        return this.version.get();
    }

    void changeDetected() {
        this.version.incrementAndGet();
        this.resetIdleCargoTracker();
    }

    class CargoInvWrapper
    extends MountedItemStorageWrapper {
        CargoInvWrapper(MountedItemStorageWrapper wrapped) {
            super(wrapped.storages);
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            ItemStack remainder = super.insertItem(slot, stack, simulate);
            if (!simulate && stack.getCount() != remainder.getCount()) {
                TrainCargoManager.this.changeDetected();
            }
            return remainder;
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack extracted = super.extractItem(slot, amount, simulate);
            if (!simulate && !extracted.isEmpty()) {
                TrainCargoManager.this.changeDetected();
            }
            return extracted;
        }

        public void setStackInSlot(int slot, ItemStack stack) {
            if (!stack.equals(this.getStackInSlot(slot))) {
                TrainCargoManager.this.changeDetected();
            }
            super.setStackInSlot(slot, stack);
        }
    }

    class CargoTankWrapper
    extends MountedFluidStorageWrapper {
        CargoTankWrapper(MountedFluidStorageWrapper wrapped) {
            super(wrapped.storages);
        }

        @Override
        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            int filled = super.fill(resource, action);
            if (action.execute() && filled > 0) {
                TrainCargoManager.this.changeDetected();
            }
            return filled;
        }

        @Override
        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            FluidStack drained = super.drain(resource, action);
            if (action.execute() && !drained.isEmpty()) {
                TrainCargoManager.this.changeDetected();
            }
            return drained;
        }

        @Override
        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            FluidStack drained = super.drain(maxDrain, action);
            if (action.execute() && !drained.isEmpty()) {
                TrainCargoManager.this.changeDetected();
            }
            return drained;
        }
    }
}
