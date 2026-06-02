/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 */
package com.simibubi.create.content.fluids.tank;

import com.simibubi.create.foundation.fluid.FluidHelper;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class BoilerData.BoilerFluidHandler
implements IFluidHandler {
    public int getTanks() {
        return 1;
    }

    public FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    }

    public int getTankCapacity(int tank) {
        return 10000;
    }

    public boolean isFluidValid(int tank, FluidStack stack) {
        return FluidHelper.isWater(stack.getFluid());
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (!this.isFluidValid(0, resource)) {
            return 0;
        }
        int amount = resource.getAmount();
        if (action.execute()) {
            BoilerData.this.gatheredSupply += amount;
        }
        return amount;
    }

    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return FluidStack.EMPTY;
    }

    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return FluidStack.EMPTY;
    }
}
