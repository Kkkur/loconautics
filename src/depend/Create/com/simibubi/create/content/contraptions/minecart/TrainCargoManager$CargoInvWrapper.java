/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.contraptions.minecart;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import net.minecraft.world.item.ItemStack;

class TrainCargoManager.CargoInvWrapper
extends MountedItemStorageWrapper {
    TrainCargoManager.CargoInvWrapper(MountedItemStorageWrapper wrapped) {
        super(wrapped.storages);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        ItemStack remainder = super.insertItem(slot, stack, simulate);
        if (!simulate && stack.getCount() != remainder.getCount()) {
            TrainCargoManager.this.changeDetected();
        }
        return remainder;
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack extracted = super.extractItem(slot, amount, simulate);
        if (!simulate && !extracted.isEmpty()) {
            TrainCargoManager.this.changeDetected();
        }
        return extracted;
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        if (!stack.equals(this.getStackInSlot(slot))) {
            TrainCargoManager.this.changeDetected();
        }
        super.setStackInSlot(slot, stack);
    }
}
