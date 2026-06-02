/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
 */
package dev.simulated_team.simulated.neoforge.service;

import dev.simulated_team.simulated.service.SimFluidService;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class NeoForgeSimFluidService
implements SimFluidService {
    @Override
    public long mbToLoaderUnits(long mb) {
        return mb;
    }

    @Override
    public Fluid getFluidInItem(ItemStack stack) {
        FluidStack fluid;
        IFluidHandlerItem handler = (IFluidHandlerItem)stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler != null && !(fluid = handler.getFluidInTank(0)).isEmpty()) {
            return fluid.getFluid();
        }
        return null;
    }
}
