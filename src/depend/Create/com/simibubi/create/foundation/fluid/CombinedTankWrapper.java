/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler
 */
package com.simibubi.create.foundation.fluid;

import net.createmod.catnip.data.Iterate;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;

public class CombinedTankWrapper
implements IFluidHandler {
    protected final IFluidHandler[] itemHandler;
    protected final int[] baseIndex;
    protected final int tankCount;
    protected boolean enforceVariety;

    public CombinedTankWrapper(IFluidHandler ... fluidHandlers) {
        this.itemHandler = fluidHandlers;
        this.baseIndex = new int[fluidHandlers.length];
        int index = 0;
        for (int i = 0; i < fluidHandlers.length; ++i) {
            this.baseIndex[i] = index += fluidHandlers[i].getTanks();
        }
        this.tankCount = index;
    }

    public CombinedTankWrapper enforceVariety() {
        this.enforceVariety = true;
        return this;
    }

    public int getTanks() {
        return this.tankCount;
    }

    public FluidStack getFluidInTank(int tank) {
        int index = this.getIndexForSlot(tank);
        IFluidHandler handler = this.getHandlerFromIndex(index);
        tank = this.getSlotFromIndex(tank, index);
        return handler.getFluidInTank(tank);
    }

    public int getTankCapacity(int tank) {
        int index = this.getIndexForSlot(tank);
        IFluidHandler handler = this.getHandlerFromIndex(index);
        int localSlot = this.getSlotFromIndex(tank, index);
        return handler.getTankCapacity(localSlot);
    }

    public boolean isFluidValid(int tank, FluidStack stack) {
        int index = this.getIndexForSlot(tank);
        IFluidHandler handler = this.getHandlerFromIndex(index);
        int localSlot = this.getSlotFromIndex(tank, index);
        return handler.isFluidValid(localSlot, stack);
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }
        int filled = 0;
        resource = resource.copy();
        boolean fittingHandlerFound = false;
        block0: for (boolean searchPass : Iterate.trueAndFalse) {
            for (IFluidHandler iFluidHandler : this.itemHandler) {
                for (int i = 0; i < iFluidHandler.getTanks(); ++i) {
                    if (!searchPass || !FluidStack.isSameFluidSameComponents((FluidStack)iFluidHandler.getFluidInTank(i), (FluidStack)resource)) continue;
                    fittingHandlerFound = true;
                }
                if (searchPass && !fittingHandlerFound) continue;
                int filledIntoCurrent = iFluidHandler.fill(resource, action);
                resource.shrink(filledIntoCurrent);
                filled += filledIntoCurrent;
                if (resource.isEmpty() || fittingHandlerFound && (this.enforceVariety || filledIntoCurrent != 0)) break block0;
            }
        }
        return filled;
    }

    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty()) {
            return resource;
        }
        FluidStack drained = FluidStack.EMPTY;
        resource = resource.copy();
        for (IFluidHandler iFluidHandler : this.itemHandler) {
            FluidStack drainedFromCurrent = iFluidHandler.drain(resource, action);
            int amount = drainedFromCurrent.getAmount();
            resource.shrink(amount);
            if (!drainedFromCurrent.isEmpty() && (drained.isEmpty() || FluidStack.isSameFluidSameComponents((FluidStack)drainedFromCurrent, (FluidStack)drained))) {
                drained = new FluidStack(drainedFromCurrent.getFluidHolder(), amount + drained.getAmount(), drainedFromCurrent.getComponentsPatch());
            }
            if (resource.isEmpty()) break;
        }
        return drained;
    }

    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        FluidStack drained = FluidStack.EMPTY;
        for (IFluidHandler iFluidHandler : this.itemHandler) {
            FluidStack drainedFromCurrent = iFluidHandler.drain(maxDrain, action);
            int amount = drainedFromCurrent.getAmount();
            maxDrain -= amount;
            if (!drainedFromCurrent.isEmpty() && (drained.isEmpty() || FluidStack.isSameFluidSameComponents((FluidStack)drainedFromCurrent, (FluidStack)drained))) {
                drained = new FluidStack(drainedFromCurrent.getFluidHolder(), amount + drained.getAmount(), drainedFromCurrent.getComponentsPatch());
            }
            if (maxDrain == 0) break;
        }
        return drained;
    }

    protected int getIndexForSlot(int slot) {
        if (slot < 0) {
            return -1;
        }
        for (int i = 0; i < this.baseIndex.length; ++i) {
            if (slot - this.baseIndex[i] >= 0) continue;
            return i;
        }
        return -1;
    }

    protected IFluidHandler getHandlerFromIndex(int index) {
        if (index < 0 || index >= this.itemHandler.length) {
            return EmptyFluidHandler.INSTANCE;
        }
        return this.itemHandler[index];
    }

    protected int getSlotFromIndex(int slot, int index) {
        if (index <= 0 || index >= this.baseIndex.length) {
            return slot;
        }
        return slot - this.baseIndex[index - 1];
    }
}
