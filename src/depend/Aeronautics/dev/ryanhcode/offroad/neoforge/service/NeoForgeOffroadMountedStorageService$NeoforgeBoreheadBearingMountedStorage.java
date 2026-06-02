/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper
 *  com.simibubi.create.content.contraptions.MountedStorageManager
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package dev.ryanhcode.offroad.neoforge.service;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadAttachedStorage;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import java.lang.ref.WeakReference;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public static class NeoForgeOffroadMountedStorageService.NeoforgeBoreheadBearingMountedStorage
extends MountedStorageManager
implements BoreheadAttachedStorage {
    public WeakReference<BoreheadBearingBlockEntity> attachedBoreheadBearing = new WeakReference<Object>(null);
    private boolean insertAllowed;

    public void initialize() {
        super.initialize();
        this.items = new NeoForgeBoreheadInvWrapper(this.items);
        this.allItems = this.items;
        if (this.fuelItems != null) {
            this.fuelItems = new NeoForgeBoreheadInvWrapper(this.fuelItems);
        }
    }

    @Override
    public void attachBlockEntity(BoreheadBearingBlockEntity be) {
        this.attachedBoreheadBearing = new WeakReference<BoreheadBearingBlockEntity>(be);
    }

    @Override
    public void setInsertAllowed(boolean insertionAllowed) {
        this.insertAllowed = insertionAllowed;
    }

    @Override
    public void invokeUnstall() {
        BoreheadBearingBlockEntity bbe = (BoreheadBearingBlockEntity)this.attachedBoreheadBearing.get();
        if (bbe != null) {
            bbe.startUnstalling();
        }
    }

    class NeoForgeBoreheadInvWrapper
    extends MountedItemStorageWrapper {
        NeoForgeBoreheadInvWrapper(MountedItemStorageWrapper wrapped) {
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
}
