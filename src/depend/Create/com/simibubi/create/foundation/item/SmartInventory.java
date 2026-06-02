/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.common.util.INBTSerializable
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.item;

import com.simibubi.create.foundation.blockEntity.ItemHandlerContainer;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class SmartInventory
extends ItemHandlerContainer
implements IItemHandlerModifiable,
INBTSerializable<CompoundTag> {
    protected boolean extractionAllowed;
    protected boolean insertionAllowed;
    protected boolean stackNonStackables;
    protected SyncedStackHandler wrapped;
    protected int stackSize;

    public SmartInventory(int slots, SyncedBlockEntity be) {
        this(slots, be, 64, false);
    }

    public SmartInventory(int slots, SyncedBlockEntity be, BiPredicate<Integer, ItemStack> isValid) {
        this(slots, be, 64, false, isValid);
    }

    public SmartInventory(int slots, SyncedBlockEntity be, int stackSize, boolean stackNonStackables) {
        this((IItemHandlerModifiable)new SyncedStackHandler(slots, be, stackNonStackables, stackSize), stackSize, stackNonStackables);
    }

    public SmartInventory(int slots, SyncedBlockEntity be, int stackSize, boolean stackNonStackables, BiPredicate<Integer, ItemStack> isValid) {
        this((IItemHandlerModifiable)new SyncedStackHandler(slots, be, stackNonStackables, stackSize, isValid), stackSize, stackNonStackables);
    }

    public SmartInventory(IItemHandlerModifiable inv, int stackSize, boolean stackNonStackables) {
        super(inv);
        this.stackNonStackables = stackNonStackables;
        this.insertionAllowed = true;
        this.extractionAllowed = true;
        this.stackSize = stackSize;
        this.wrapped = (SyncedStackHandler)inv;
    }

    public SmartInventory withMaxStackSize(int maxStackSize) {
        this.stackSize = maxStackSize;
        this.wrapped.stackSize = maxStackSize;
        return this;
    }

    public SmartInventory whenContentsChanged(Consumer<Integer> updateCallback) {
        ((SyncedStackHandler)this.inv).whenContentsChange(updateCallback);
        return this;
    }

    public SmartInventory allowInsertion() {
        this.insertionAllowed = true;
        return this;
    }

    public SmartInventory allowExtraction() {
        this.extractionAllowed = true;
        return this;
    }

    public SmartInventory forbidInsertion() {
        this.insertionAllowed = false;
        return this;
    }

    public SmartInventory forbidExtraction() {
        this.extractionAllowed = false;
        return this;
    }

    public int getSlots() {
        return this.inv.getSlots();
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!this.insertionAllowed) {
            return stack;
        }
        return this.inv.insertItem(slot, stack, simulate);
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack extractItem;
        if (!this.extractionAllowed) {
            return ItemStack.EMPTY;
        }
        if (this.stackNonStackables && !(extractItem = this.inv.extractItem(slot, amount, true)).isEmpty() && extractItem.getMaxStackSize() < extractItem.getCount()) {
            amount = extractItem.getMaxStackSize();
        }
        return this.inv.extractItem(slot, amount, simulate);
    }

    public int getSlotLimit(int slot) {
        return Math.min(this.inv.getSlotLimit(slot), this.stackSize);
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return this.inv.isItemValid(slot, stack);
    }

    public ItemStack getStackInSlot(int slot) {
        return this.inv.getStackInSlot(slot);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        ((SyncedStackHandler)this.inv).setStackInSlot(slot, stack);
    }

    public int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }

    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        return this.getInv().serializeNBT(registries);
    }

    public void deserializeNBT(HolderLookup.Provider registries, CompoundTag nbt) {
        this.getInv().deserializeNBT(registries, nbt);
    }

    private SyncedStackHandler getInv() {
        return (SyncedStackHandler)this.inv;
    }

    protected static class SyncedStackHandler
    extends ItemStackHandler {
        private SyncedBlockEntity blockEntity;
        private boolean stackNonStackables;
        private int stackSize;
        private BiPredicate<Integer, ItemStack> isValid = (x$0, x$1) -> super.isItemValid(x$0, x$1);
        private Consumer<Integer> updateCallback;

        public SyncedStackHandler(int slots, SyncedBlockEntity be, boolean stackNonStackables, int stackSize, BiPredicate<Integer, ItemStack> isValid) {
            this(slots, be, stackNonStackables, stackSize);
            this.isValid = isValid;
        }

        public SyncedStackHandler(int slots, SyncedBlockEntity be, boolean stackNonStackables, int stackSize) {
            super(slots);
            this.blockEntity = be;
            this.stackNonStackables = stackNonStackables;
            this.stackSize = stackSize;
        }

        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (this.updateCallback != null) {
                this.updateCallback.accept(slot);
            }
            this.blockEntity.notifyUpdate();
        }

        public int getSlotLimit(int slot) {
            return Math.min(this.stackNonStackables ? 64 : super.getSlotLimit(slot), this.stackSize);
        }

        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return this.isValid.test(slot, stack);
        }

        public void whenContentsChange(Consumer<Integer> updateCallback) {
            this.updateCallback = updateCallback;
        }
    }
}
