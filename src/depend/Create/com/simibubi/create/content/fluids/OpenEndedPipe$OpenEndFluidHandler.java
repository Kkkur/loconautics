/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

private class OpenEndedPipe.OpenEndFluidHandler
extends FluidTank {
    public OpenEndedPipe.OpenEndFluidHandler() {
        super(1000);
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        OpenPipeEffectHandler effectHandler;
        if (OpenEndedPipe.this.world == null) {
            return 0;
        }
        if (!OpenEndedPipe.this.world.isLoaded(OpenEndedPipe.this.outputPos)) {
            return 0;
        }
        if (resource.isEmpty()) {
            return 0;
        }
        if (!OpenEndedPipe.this.provideFluidToSpace(resource, true)) {
            return 0;
        }
        FluidStack containedFluidStack = this.getFluid();
        boolean hasBlockState = FluidHelper.hasBlockState(containedFluidStack.getFluid());
        if (!containedFluidStack.isEmpty() && !FluidStack.isSameFluidSameComponents((FluidStack)containedFluidStack, (FluidStack)resource)) {
            this.setFluid(FluidStack.EMPTY);
        }
        if (OpenEndedPipe.this.wasPulling) {
            OpenEndedPipe.this.wasPulling = false;
        }
        if ((effectHandler = OpenPipeEffectHandler.REGISTRY.get(resource.getFluid())) != null && !hasBlockState) {
            resource = FluidHelper.copyStackWithAmount(resource, 1);
        }
        int fill = super.fill(resource, action);
        if (action.simulate()) {
            return fill;
        }
        if (effectHandler != null && !resource.isEmpty()) {
            FluidStack exposed = hasBlockState ? resource.copy() : resource;
            effectHandler.apply(OpenEndedPipe.this.world, OpenEndedPipe.this.aoe, exposed);
        }
        if ((this.getFluidAmount() == 1000 || !hasBlockState) && OpenEndedPipe.this.provideFluidToSpace(containedFluidStack, false)) {
            this.setFluid(FluidStack.EMPTY);
        }
        return fill;
    }

    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return this.drainInner(resource.getAmount(), resource, action);
    }

    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return this.drainInner(maxDrain, null, action);
    }

    private FluidStack drainInner(int amount, @Nullable FluidStack filter, IFluidHandler.FluidAction action) {
        FluidStack drainedFromInternal;
        boolean filterPresent;
        FluidStack empty = FluidStack.EMPTY;
        boolean bl = filterPresent = filter != null;
        if (OpenEndedPipe.this.world == null) {
            return empty;
        }
        if (!OpenEndedPipe.this.world.isLoaded(OpenEndedPipe.this.outputPos)) {
            return empty;
        }
        if (amount == 0) {
            return empty;
        }
        if (amount > 1000) {
            amount = 1000;
            if (filterPresent) {
                filter = FluidHelper.copyStackWithAmount(filter, amount);
            }
        }
        if (!OpenEndedPipe.this.wasPulling) {
            OpenEndedPipe.this.wasPulling = true;
        }
        FluidStack fluidStack = drainedFromInternal = filterPresent ? super.drain(filter, action) : super.drain(amount, action);
        if (!drainedFromInternal.isEmpty()) {
            return drainedFromInternal;
        }
        FluidStack drainedFromWorld = OpenEndedPipe.this.removeFluidFromSpace(action.simulate());
        if (drainedFromWorld.isEmpty()) {
            return FluidStack.EMPTY;
        }
        if (filterPresent && !FluidStack.isSameFluidSameComponents((FluidStack)drainedFromWorld, (FluidStack)filter)) {
            return FluidStack.EMPTY;
        }
        int remainder = drainedFromWorld.getAmount() - amount;
        drainedFromWorld.setAmount(amount);
        if (!action.simulate() && remainder > 0) {
            if (!this.getFluid().isEmpty() && !FluidStack.isSameFluidSameComponents((FluidStack)this.getFluid(), (FluidStack)drainedFromWorld)) {
                this.setFluid(FluidStack.EMPTY);
            }
            super.fill(FluidHelper.copyStackWithAmount(drainedFromWorld, remainder), IFluidHandler.FluidAction.EXECUTE);
        }
        return drainedFromWorld;
    }
}
