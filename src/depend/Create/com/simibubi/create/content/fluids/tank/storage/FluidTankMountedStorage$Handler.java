/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 */
package com.simibubi.create.content.fluids.tank.storage;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public static final class FluidTankMountedStorage.Handler
extends FluidTank {
    private Runnable onChange = () -> {};

    public FluidTankMountedStorage.Handler(int capacity, FluidStack stack) {
        super(capacity);
        this.setFluid(stack);
    }

    protected void onContentsChanged() {
        this.onChange.run();
    }
}
