/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 */
package com.simibubi.create.foundation.blockEntity.behaviour.fluid;

import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class SmartFluidTankBehaviour.InternalFluidHandler
extends CombinedTankWrapper {
    public SmartFluidTankBehaviour.InternalFluidHandler(IFluidHandler[] handlers, boolean enforceVariety) {
        super(handlers);
        if (enforceVariety) {
            this.enforceVariety();
        }
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (!SmartFluidTankBehaviour.this.insertionAllowed) {
            return 0;
        }
        return super.fill(resource, action);
    }

    public int forceFill(FluidStack resource, IFluidHandler.FluidAction action) {
        return super.fill(resource, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (!SmartFluidTankBehaviour.this.extractionAllowed) {
            return FluidStack.EMPTY;
        }
        return super.drain(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (!SmartFluidTankBehaviour.this.extractionAllowed) {
            return FluidStack.EMPTY;
        }
        return super.drain(maxDrain, action);
    }
}
