/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.component.DataComponentMap
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.multiloader.tanks.neoforge;

import dev.simulated_team.simulated.multiloader.tanks.CFluidType;
import dev.simulated_team.simulated.multiloader.tanks.SingleTank;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class SingleTankWrapper
extends FluidTank {
    private final SingleTank tank;

    public SingleTankWrapper(SingleTank tank) {
        super((int)tank.capacity);
        this.tank = tank;
    }

    public static FluidStack fromCType(CFluidType type, int amount) {
        return new FluidStack((Holder)type.fluid.builtInRegistryHolder(), amount);
    }

    public static CFluidType toCType(FluidStack stack) {
        return new CFluidType(stack.getFluid(), (DataComponentMap)stack.getComponents());
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return (int)this.tank.insert(SingleTankWrapper.toCType(resource), resource.getAmount(), action.simulate());
    }

    @NotNull
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return SingleTankWrapper.fromCType(this.tank.type, (int)this.tank.extract(this.tank.type, maxDrain, action.simulate()));
    }

    @NotNull
    public FluidStack getFluid() {
        return SingleTankWrapper.fromCType(this.tank.type, (int)this.tank.amount);
    }
}
