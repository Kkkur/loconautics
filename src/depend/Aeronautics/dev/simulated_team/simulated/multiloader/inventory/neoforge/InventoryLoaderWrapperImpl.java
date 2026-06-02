/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.item.ItemHelper
 *  com.simibubi.create.foundation.item.ItemHelper$ExtractionCountMode
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.multiloader.inventory.neoforge;

import com.simibubi.create.foundation.item.ItemHelper;
import dev.simulated_team.simulated.multiloader.inventory.InventoryLoaderWrapper;
import dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class InventoryLoaderWrapperImpl
extends InventoryLoaderWrapper {
    private final IItemHandler attachedInventory;

    public InventoryLoaderWrapperImpl(IItemHandler attachedInventory) {
        this.attachedInventory = attachedInventory;
    }

    @Override
    public ItemStack extractAny(int maxAmount, boolean simulate, boolean exact) {
        ItemStack extracted = ItemHelper.extract((IItemHandler)this.attachedInventory, $ -> true, (ItemHelper.ExtractionCountMode)(exact ? ItemHelper.ExtractionCountMode.EXACTLY : ItemHelper.ExtractionCountMode.UPTO), (int)maxAmount, (boolean)simulate);
        if (this.callback != null && !extracted.isEmpty() && !simulate) {
            this.callback.accept(true);
        }
        return extracted;
    }

    @Override
    public int insertGeneral(ItemInfoWrapper info, int amountToInsert, boolean simulate) {
        ItemStack is = ItemInfoWrapper.generateFromInfo(info);
        is.setCount(amountToInsert);
        int amountInserted = amountToInsert - ItemHandlerHelper.insertItem((IItemHandler)this.attachedInventory, (ItemStack)is, (boolean)simulate).getCount();
        if (this.callback != null && amountInserted > 0 && !simulate) {
            this.callback.accept(false);
        }
        return amountInserted;
    }

    @Override
    public ItemStack insertSlot(ItemStack stack, int slot, boolean simulate) {
        ItemStack inserted = this.attachedInventory.insertItem(slot, stack, simulate);
        if (this.callback != null && !stack.equals(inserted) && !simulate) {
            this.callback.accept(false);
        }
        return inserted;
    }

    @Override
    public int extractGeneral(ItemInfoWrapper info, int amountToExtract, boolean simulate) {
        int extractAmount = ItemHelper.extract((IItemHandler)this.attachedInventory, $ -> $.getItem() == info.type(), (ItemHelper.ExtractionCountMode)ItemHelper.ExtractionCountMode.UPTO, (int)amountToExtract, (boolean)simulate).getCount();
        if (this.callback != null && extractAmount > 0 && !simulate) {
            this.callback.accept(true);
        }
        return extractAmount;
    }

    @Override
    public ItemStack extractSlot(int index, int amountToExtract, boolean simulate) {
        ItemStack extracted = this.attachedInventory.extractItem(index, amountToExtract, simulate);
        if (this.callback != null && !extracted.isEmpty() && !simulate) {
            this.callback.accept(true);
        }
        return extracted;
    }

    @Override
    public int getContainerSize() {
        return this.attachedInventory.getSlots();
    }

    @Override
    public int getMaxStackSize() {
        return this.attachedInventory.getSlotLimit(0);
    }

    @Override
    @NotNull
    public ItemStack getItem(int slot) {
        return this.attachedInventory.getStackInSlot(slot);
    }
}
