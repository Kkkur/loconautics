/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package dev.ryanhcode.offroad.neoforge.service;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

class NeoForgeOffroadMountedStorageService.NeoforgeBoreheadBearingMountedStorage.NeoForgeBoreheadInvWrapper
extends MountedItemStorageWrapper {
    NeoForgeOffroadMountedStorageService.NeoforgeBoreheadBearingMountedStorage.NeoForgeBoreheadInvWrapper(MountedItemStorageWrapper wrapped) {
        super(wrapped.storages);
    }

    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (NeoforgeBoreheadBearingMountedStorage.this.insertAllowed) {
            return super.insertItem(slot, stack, simulate);
        }
        return stack;
    }

    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack extracted;
        BoreheadBearingBlockEntity bbe = (BoreheadBearingBlockEntity)NeoforgeBoreheadBearingMountedStorage.this.attachedBoreheadBearing.get();
        if (bbe != null && !(extracted = super.extractItem(slot, amount, simulate)).isEmpty()) {
            bbe.startUnstalling();
            return extracted;
        }
        return ItemStack.EMPTY;
    }

    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        super.setStackInSlot(slot, stack);
    }
}
