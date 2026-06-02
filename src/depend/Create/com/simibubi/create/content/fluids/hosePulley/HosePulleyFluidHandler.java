/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids.hosePulley;

import com.simibubi.create.content.fluids.transfer.FluidDrainingBehaviour;
import com.simibubi.create.content.fluids.transfer.FluidFillingBehaviour;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class HosePulleyFluidHandler
implements IFluidHandler {
    private SmartFluidTank internalTank;
    private FluidFillingBehaviour filler;
    private FluidDrainingBehaviour drainer;
    private Supplier<BlockPos> rootPosGetter;
    private Supplier<Boolean> predicate;

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (!this.internalTank.isEmpty() && !FluidStack.isSameFluidSameComponents((FluidStack)resource, (FluidStack)this.internalTank.getFluid())) {
            return 0;
        }
        if (resource.isEmpty() || !FluidHelper.hasBlockState(resource.getFluid())) {
            return 0;
        }
        int diff = resource.getAmount();
        int totalAmountAfterFill = diff + this.internalTank.getFluidAmount();
        FluidStack remaining = resource.copy();
        boolean deposited = false;
        if (this.predicate.get().booleanValue() && totalAmountAfterFill >= 1000 && this.filler.tryDeposit(resource.getFluid(), this.rootPosGetter.get(), action.simulate())) {
            this.drainer.counterpartActed();
            remaining.shrink(1000);
            diff -= 1000;
            deposited = true;
        }
        if (action.simulate()) {
            return diff <= 0 ? resource.getAmount() : this.internalTank.fill(remaining, action);
        }
        if (diff <= 0) {
            this.internalTank.drain(-diff, IFluidHandler.FluidAction.EXECUTE);
            return resource.getAmount();
        }
        return this.internalTank.fill(remaining, action) + (deposited ? 1000 : 0);
    }

    public FluidStack getFluidInTank(int tank) {
        if (this.internalTank.isEmpty()) {
            return this.drainer.getDrainableFluid(this.rootPosGetter.get());
        }
        return this.internalTank.getFluidInTank(tank);
    }

    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return this.drainInternal(resource.getAmount(), resource, action);
    }

    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return this.drainInternal(maxDrain, null, action);
    }

    private FluidStack drainInternal(int maxDrain, @Nullable FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource != null && !this.internalTank.isEmpty() && !FluidStack.isSameFluidSameComponents((FluidStack)resource, (FluidStack)this.internalTank.getFluid())) {
            return FluidStack.EMPTY;
        }
        if (this.internalTank.getFluidAmount() >= 1000) {
            return this.internalTank.drain(maxDrain, action);
        }
        BlockPos pos = this.rootPosGetter.get();
        FluidStack returned = this.drainer.getDrainableFluid(pos);
        if (!this.predicate.get().booleanValue() || !this.drainer.pullNext(pos, action.simulate())) {
            return this.internalTank.drain(maxDrain, action);
        }
        this.filler.counterpartActed();
        FluidStack leftover = returned.copy();
        int available = 1000 + this.internalTank.getFluidAmount();
        if (!this.internalTank.isEmpty() && !FluidStack.isSameFluidSameComponents((FluidStack)this.internalTank.getFluid(), (FluidStack)returned) || returned.isEmpty()) {
            return this.internalTank.drain(maxDrain, action);
        }
        if (resource != null && !FluidStack.isSameFluidSameComponents((FluidStack)returned, (FluidStack)resource)) {
            return FluidStack.EMPTY;
        }
        int drained = Math.min(maxDrain, available);
        returned.setAmount(drained);
        leftover.setAmount(available - drained);
        if (action.execute() && !leftover.isEmpty()) {
            this.internalTank.setFluid(leftover);
        }
        return returned;
    }

    public HosePulleyFluidHandler(SmartFluidTank internalTank, FluidFillingBehaviour filler, FluidDrainingBehaviour drainer, Supplier<BlockPos> rootPosGetter, Supplier<Boolean> predicate) {
        this.internalTank = internalTank;
        this.filler = filler;
        this.drainer = drainer;
        this.rootPosGetter = rootPosGetter;
        this.predicate = predicate;
    }

    public int getTanks() {
        return this.internalTank.getTanks();
    }

    public int getTankCapacity(int tank) {
        return this.internalTank.getTankCapacity(tank);
    }

    public boolean isFluidValid(int tank, FluidStack stack) {
        return this.internalTank.isFluidValid(tank, stack);
    }

    public SmartFluidTank getInternalTank() {
        return this.internalTank;
    }
}
