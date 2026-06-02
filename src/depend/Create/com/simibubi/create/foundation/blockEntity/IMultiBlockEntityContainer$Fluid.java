/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.IFluidTank
 */
package com.simibubi.create.foundation.blockEntity;

import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;

public static interface IMultiBlockEntityContainer.Fluid
extends IMultiBlockEntityContainer {
    default public boolean hasTank() {
        return false;
    }

    default public int getTankSize(int tank) {
        return 0;
    }

    default public void setTankSize(int tank, int blocks) {
    }

    default public IFluidTank getTank(int tank) {
        return null;
    }

    default public FluidStack getFluid(int tank) {
        return FluidStack.EMPTY;
    }
}
