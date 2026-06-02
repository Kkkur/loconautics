/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.logistics.filter;

import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public static class FilterItemStack.PackageFilterItemStack
extends FilterItemStack {
    public String filterString;

    public FilterItemStack.PackageFilterItemStack(ItemStack filter) {
        super(filter);
        this.filterString = PackageItem.getAddress(filter);
    }

    @Override
    public boolean test(Level world, ItemStack stack, boolean matchNBT) {
        return this.filterString.isBlank() && super.test(world, stack, matchNBT) || PackageItem.isPackage(stack) && PackageItem.matchAddress(stack, this.filterString);
    }

    @Override
    public boolean test(Level world, FluidStack stack, boolean matchNBT) {
        return false;
    }
}
