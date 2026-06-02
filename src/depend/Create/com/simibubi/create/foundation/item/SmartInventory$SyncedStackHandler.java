/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.item;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

protected static class SmartInventory.SyncedStackHandler
extends ItemStackHandler {
    private SyncedBlockEntity blockEntity;
    private boolean stackNonStackables;
    private int stackSize;
    private BiPredicate<Integer, ItemStack> isValid = (x$0, x$1) -> super.isItemValid(x$0, x$1);
    private Consumer<Integer> updateCallback;

    public SmartInventory.SyncedStackHandler(int slots, SyncedBlockEntity be, boolean stackNonStackables, int stackSize, BiPredicate<Integer, ItemStack> isValid) {
        this(slots, be, stackNonStackables, stackSize);
        this.isValid = isValid;
    }

    public SmartInventory.SyncedStackHandler(int slots, SyncedBlockEntity be, boolean stackNonStackables, int stackSize) {
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
