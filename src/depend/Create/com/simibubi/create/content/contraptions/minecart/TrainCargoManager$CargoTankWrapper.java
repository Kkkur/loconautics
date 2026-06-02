/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 */
package com.simibubi.create.content.contraptions.minecart;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

class TrainCargoManager.CargoTankWrapper
extends MountedFluidStorageWrapper {
    TrainCargoManager.CargoTankWrapper(MountedFluidStorageWrapper wrapped) {
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
