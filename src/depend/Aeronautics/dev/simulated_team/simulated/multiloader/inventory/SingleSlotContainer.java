/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.multiloader.inventory;

import dev.simulated_team.simulated.multiloader.inventory.AbstractContainer;
import dev.simulated_team.simulated.multiloader.inventory.ContainerSlot;
import dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public abstract class SingleSlotContainer
implements AbstractContainer {
    public final ContainerSlot slot;
    public final int maxStackSize;

    public SingleSlotContainer(int maxStackSize) {
        this.maxStackSize = maxStackSize;
        this.slot = new ContainerSlot(0, ItemStack.EMPTY, Items.AIR, this);
    }

    @Override
    public int insertGeneral(ItemInfoWrapper item, int amountToInsert, boolean simulate) {
        return this.commonInsert(item, this.slot, amountToInsert, simulate);
    }

    @Override
    public ItemStack insertSlot(ItemStack stack, int slot, boolean simulate) {
        int inserted = this.commonInsert(ItemInfoWrapper.generateFromStack(stack), this.slot, stack.getCount(), simulate);
        if (inserted > 0) {
            ItemStack copied = stack.copy();
            copied.shrink(inserted);
            return copied;
        }
        return stack;
    }

    @Override
    public int extractGeneral(ItemInfoWrapper info, int amountToExtract, boolean simulate) {
        return this.commonExtract(info, this.slot, amountToExtract, simulate);
    }

    @Override
    public ItemStack extractSlot(int index, int amountToExtract, boolean simulate) {
        if (index != 0) {
            return ItemStack.EMPTY;
        }
        ItemStack newStack = this.slot.getStack().copy();
        long extracted = this.commonExtract(ItemInfoWrapper.generateFromStack(newStack), this.slot, amountToExtract, simulate);
        if (extracted > 0L) {
            newStack.setCount((int)extracted);
            return newStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public List<ContainerSlot> getInventoryAsList() {
        return List.of(this.slot);
    }

    @Override
    public Set<ContainerSlot> getPopulatedSlots() {
        if (!this.slot.isEmpty()) {
            return Set.of(this.slot);
        }
        return new HashSet<ContainerSlot>();
    }

    @Override
    public CompoundTag write(HolderLookup.Provider provider) {
        return this.slot.write(provider);
    }

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag nbt) {
        this.slot.read(provider, nbt);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean canInsertItem(ItemInfoWrapper info, ContainerSlot slot) {
        return this.canInsertItem(info);
    }

    public abstract boolean canInsertItem(ItemInfoWrapper var1);

    @Override
    public boolean isEmpty() {
        return this.slot.isEmpty();
    }

    @Override
    @NotNull
    public ItemStack getItem(int slot) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }
        return this.slot.getStack();
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        if (slot != 0) {
            return;
        }
        this.slot.setStack(stack);
    }

    @Override
    public void onStackItemChange(ContainerSlot slot, ItemStack oldSlotStack, ItemStack newSlotStack) {
        this.setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return this.maxStackSize;
    }

    @Override
    public void clearContent() {
        this.slot.clear();
    }

    @Override
    public void setChanged() {
    }
}
