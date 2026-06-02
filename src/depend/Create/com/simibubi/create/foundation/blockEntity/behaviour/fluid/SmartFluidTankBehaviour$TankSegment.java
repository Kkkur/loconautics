/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.foundation.blockEntity.behaviour.fluid;

import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;

public class SmartFluidTankBehaviour.TankSegment {
    protected SmartFluidTank tank;
    protected LerpedFloat fluidLevel;
    protected FluidStack renderedFluid;

    public SmartFluidTankBehaviour.TankSegment(int capacity) {
        this.tank = new SmartFluidTank(capacity, f -> this.onFluidStackChanged());
        this.fluidLevel = LerpedFloat.linear().startWithValue(0.0).chase(0.0, 0.25, LerpedFloat.Chaser.EXP);
        this.renderedFluid = FluidStack.EMPTY;
    }

    public void onFluidStackChanged() {
        if (!SmartFluidTankBehaviour.this.blockEntity.hasLevel()) {
            return;
        }
        this.fluidLevel.chase((double)((float)this.tank.getFluidAmount() / (float)this.tank.getCapacity()), 0.25, LerpedFloat.Chaser.EXP);
        if (!SmartFluidTankBehaviour.this.getWorld().isClientSide) {
            SmartFluidTankBehaviour.this.sendDataLazily();
        }
        if (SmartFluidTankBehaviour.this.blockEntity.isVirtual() && !this.tank.getFluid().isEmpty()) {
            this.renderedFluid = this.tank.getFluid();
        }
    }

    public FluidStack getRenderedFluid() {
        return this.renderedFluid;
    }

    public LerpedFloat getFluidLevel() {
        return this.fluidLevel;
    }

    public float getTotalUnits(float partialTicks) {
        return this.fluidLevel.getValue(partialTicks) * (float)this.tank.getCapacity();
    }

    public CompoundTag writeNBT(HolderLookup.Provider registries) {
        CompoundTag compound = new CompoundTag();
        compound.put("TankContent", (Tag)this.tank.writeToNBT(registries, new CompoundTag()));
        compound.put("Level", (Tag)this.fluidLevel.writeNBT());
        return compound;
    }

    public void readNBT(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.tank.readFromNBT(registries, compound.getCompound("TankContent"));
        this.fluidLevel.readNBT(compound.getCompound("Level"), clientPacket);
        if (!this.tank.getFluid().isEmpty()) {
            this.renderedFluid = this.tank.getFluid();
        }
    }

    public boolean isEmpty(float partialTicks) {
        FluidStack renderedFluid = this.getRenderedFluid();
        if (renderedFluid.isEmpty()) {
            return true;
        }
        float units = this.getTotalUnits(partialTicks);
        return units < 1.0f;
    }
}
