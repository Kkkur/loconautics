/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.util.ExtraCodecs
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 */
package com.simibubi.create.content.fluids.tank;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import java.util.function.Consumer;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public static class CreativeFluidTankBlockEntity.CreativeSmartFluidTank
extends SmartFluidTank {
    public static final Codec<CreativeFluidTankBlockEntity.CreativeSmartFluidTank> CODEC = RecordCodecBuilder.create(i -> i.group((App)FluidStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(FluidTank::getFluid), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("capacity").forGetter(FluidTank::getCapacity)).apply((Applicative)i, (fluid, capacity) -> {
        CreativeFluidTankBlockEntity.CreativeSmartFluidTank tank = new CreativeFluidTankBlockEntity.CreativeSmartFluidTank((int)capacity, $ -> {});
        tank.setFluid((FluidStack)fluid);
        return tank;
    }));

    public CreativeFluidTankBlockEntity.CreativeSmartFluidTank(int capacity, Consumer<FluidStack> updateCallback) {
        super(capacity, updateCallback);
    }

    public int getFluidAmount() {
        return this.getFluid().isEmpty() ? 0 : this.getTankCapacity(0);
    }

    public void setContainedFluid(FluidStack fluidStack) {
        this.fluid = fluidStack.copy();
        if (!fluidStack.isEmpty()) {
            this.fluid.setAmount(this.getTankCapacity(0));
        }
        this.onContentsChanged();
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return resource.getAmount();
    }

    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return super.drain(resource, IFluidHandler.FluidAction.SIMULATE);
    }

    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return super.drain(maxDrain, IFluidHandler.FluidAction.SIMULATE);
    }
}
