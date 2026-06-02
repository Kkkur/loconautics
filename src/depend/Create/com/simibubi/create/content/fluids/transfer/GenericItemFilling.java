/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.world.item.BucketItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.MilkBucketItem
 *  net.minecraft.world.item.alchemy.PotionContents
 *  net.minecraft.world.item.alchemy.Potions
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
 *  net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper
 */
package com.simibubi.create.content.fluids.transfer;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

public class GenericItemFilling {
    public static boolean isFluidHandlerValid(ItemStack stack, IFluidHandlerItem fluidHandler) {
        Item item;
        return fluidHandler.getClass() != FluidBucketWrapper.class || (item = stack.getItem()).getClass() == BucketItem.class || item instanceof MilkBucketItem;
    }

    public static boolean canItemBeFilled(Level world, ItemStack stack) {
        if (stack.getItem() == Items.GLASS_BOTTLE) {
            return true;
        }
        if (stack.getItem() == Items.MILK_BUCKET) {
            return false;
        }
        IFluidHandlerItem capability = (IFluidHandlerItem)stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability == null) {
            return false;
        }
        if (!GenericItemFilling.isFluidHandlerValid(stack, capability)) {
            return false;
        }
        for (int i = 0; i < capability.getTanks(); ++i) {
            if (capability.getFluidInTank(i).getAmount() >= capability.getTankCapacity(i)) continue;
            return true;
        }
        return false;
    }

    public static int getRequiredAmountForItem(Level world, ItemStack stack, FluidStack availableFluid) {
        if (stack.getItem() == Items.GLASS_BOTTLE && GenericItemFilling.canFillGlassBottleInternally(availableFluid)) {
            return PotionFluidHandler.getRequiredAmountForFilledBottle(stack, availableFluid);
        }
        if (stack.getItem() == Items.BUCKET && GenericItemFilling.canFillBucketInternally(availableFluid)) {
            return 1000;
        }
        IFluidHandlerItem capability = (IFluidHandlerItem)stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability == null) {
            return -1;
        }
        if (capability instanceof FluidBucketWrapper) {
            Item filledBucket = availableFluid.getFluid().getBucket();
            if (filledBucket == null || filledBucket == Items.AIR) {
                return -1;
            }
            if (!((FluidBucketWrapper)capability).getFluid().isEmpty()) {
                return -1;
            }
            return 1000;
        }
        int filled = capability.fill(availableFluid, IFluidHandler.FluidAction.SIMULATE);
        return filled == 0 ? -1 : filled;
    }

    private static boolean canFillGlassBottleInternally(FluidStack availableFluid) {
        Fluid fluid = availableFluid.getFluid();
        if (fluid.isSame((Fluid)Fluids.WATER)) {
            return true;
        }
        if (fluid.isSame((Fluid)AllFluids.POTION.get())) {
            return true;
        }
        return fluid.isSame((Fluid)AllFluids.TEA.get());
    }

    private static boolean canFillBucketInternally(FluidStack availableFluid) {
        return false;
    }

    public static ItemStack fillItem(Level world, int requiredAmount, ItemStack stack, FluidStack availableFluid) {
        FluidStack toFill = availableFluid.copy();
        toFill.setAmount(requiredAmount);
        availableFluid.shrink(requiredAmount);
        if (stack.getItem() == Items.GLASS_BOTTLE && GenericItemFilling.canFillGlassBottleInternally(toFill)) {
            Fluid fluid = toFill.getFluid();
            ItemStack fillBottle = FluidHelper.isWater(fluid) ? PotionContents.createItemStack((Item)Items.POTION, (Holder)Potions.WATER) : (fluid.isSame((Fluid)AllFluids.TEA.get()) ? AllItems.BUILDERS_TEA.asStack() : PotionFluidHandler.fillBottle(stack, toFill));
            stack.shrink(1);
            return fillBottle;
        }
        ItemStack split = stack.copy();
        split.setCount(1);
        IFluidHandlerItem capability = (IFluidHandlerItem)split.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability == null) {
            return ItemStack.EMPTY;
        }
        capability.fill(toFill, IFluidHandler.FluidAction.EXECUTE);
        ItemStack container = capability.getContainer().copy();
        stack.shrink(1);
        return container;
    }
}
