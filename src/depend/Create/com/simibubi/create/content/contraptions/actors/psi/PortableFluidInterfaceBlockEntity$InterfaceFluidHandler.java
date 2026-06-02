/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 */
package com.simibubi.create.content.contraptions.actors.psi;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class PortableFluidInterfaceBlockEntity.InterfaceFluidHandler
implements IFluidHandler {
    private IFluidHandler wrapped;

    public PortableFluidInterfaceBlockEntity.InterfaceFluidHandler(IFluidHandler wrapped) {
        this.wrapped = wrapped;
    }

    public int getTanks() {
        return this.wrapped.getTanks();
    }

    public FluidStack getFluidInTank(int tank) {
        return this.wrapped.getFluidInTank(tank);
    }

    public int getTankCapacity(int tank) {
        return this.wrapped.getTankCapacity(tank);
    }

    public boolean isFluidValid(int tank, FluidStack stack) {
        return this.wrapped.isFluidValid(tank, stack);
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (!PortableFluidInterfaceBlockEntity.this.isConnected()) {
            return 0;
        }
        int fill = this.wrapped.fill(resource, action);
        if (fill > 0 && action.execute()) {
            this.keepAlive();
        }
        return fill;
    }

    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (!PortableFluidInterfaceBlockEntity.this.canTransfer()) {
            return FluidStack.EMPTY;
        }
        FluidStack drain = this.wrapped.drain(resource, action);
        if (!drain.isEmpty() && action.execute()) {
            this.keepAlive();
        }
        return drain;
    }

    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (!PortableFluidInterfaceBlockEntity.this.canTransfer()) {
            return FluidStack.EMPTY;
        }
        FluidStack drain = this.wrapped.drain(maxDrain, action);
        if (!drain.isEmpty() && action.execute()) {
            this.keepAlive();
        }
        return drain;
    }

    public void keepAlive() {
        PortableFluidInterfaceBlockEntity.this.onContentTransferred();
    }
}
