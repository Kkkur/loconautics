/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.Containers
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.multiloader.inventory;

import dev.simulated_team.simulated.multiloader.inventory.AbstractContainer;
import dev.simulated_team.simulated.multiloader.inventory.ContainerSlot;
import dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MultiSlotContainer
implements AbstractContainer {
    private final List<ContainerSlot> inventory;
    private final Set<ContainerSlot> populatedSlots;
    private final Map<Item, Set<ContainerSlot>> filteredSlots;
    public int maxStackSize;
    public int storedItemCount;

    public MultiSlotContainer(int size) {
        this(size, 64);
    }

    public MultiSlotContainer(int size, int maxStackSize) {
        this.maxStackSize = maxStackSize;
        this.inventory = new ArrayList<ContainerSlot>(size);
        this.populatedSlots = new HashSet<ContainerSlot>();
        this.filteredSlots = new HashMap<Item, Set<ContainerSlot>>();
        for (int i = 0; i < size; ++i) {
            this.inventory.add(i, ContainerSlot.of(i, ItemStack.EMPTY, (AbstractContainer)this));
        }
        this.maxStackSize = maxStackSize;
    }

    @Override
    public int insertGeneral(ItemInfoWrapper info, int amountToInsert, boolean simulate) {
        int inserted = 0;
        for (ContainerSlot slot : this.getInsertableSlotsFor(info.type(), true)) {
            if ((inserted += this.commonInsert(info, slot, amountToInsert - inserted, simulate)) >= amountToInsert) break;
        }
        return inserted;
    }

    @Override
    public ItemStack insertSlot(ItemStack stack, int slot, boolean simulate) {
        int amountInserted = this.commonInsert(ItemInfoWrapper.generateFromStack(stack), this.inventory.get(slot), stack.getCount(), simulate);
        if (amountInserted > 0) {
            ItemStack copyIncoming = stack.copy();
            copyIncoming.shrink(amountInserted);
            return copyIncoming;
        }
        return stack;
    }

    @Override
    public int extractGeneral(ItemInfoWrapper info, int amountToExtract, boolean simulate) {
        Set<ContainerSlot> populatedSlots = this.getFilteredSlots(info.type());
        if (populatedSlots.isEmpty()) {
            return 0;
        }
        int extracted = 0;
        for (ContainerSlot slot : populatedSlots) {
            if ((extracted += this.commonExtract(info, slot, amountToExtract - extracted, simulate)) >= amountToExtract) break;
        }
        return extracted;
    }

    @Override
    public ItemStack extractSlot(int index, int amountToExtract, boolean simulate) {
        ContainerSlot slot = this.getSlot(index);
        if (slot.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack toExtract = slot.getStack().copy();
        long extracted = this.commonExtract(ItemInfoWrapper.generateFromStack(toExtract), slot, amountToExtract, simulate);
        if (extracted > 0L) {
            toExtract.setCount((int)extracted);
            return toExtract;
        }
        return ItemStack.EMPTY;
    }

    public Set<ItemStack> extractAny(int amountToExtract, boolean simulated) {
        HashSet<ItemStack> extracted = new HashSet<ItemStack>();
        for (ContainerSlot slot : this.populatedSlots) {
            if (amountToExtract <= 0) break;
            int amountExtracted = slot.extractStack(null, amountToExtract, simulated);
            if (amountExtracted <= 0) continue;
            amountToExtract -= amountExtracted;
            extracted.add(new ItemStack((ItemLike)slot.getType(), amountExtracted));
        }
        return extracted;
    }

    public ItemStack extractSingle(boolean entireStack, boolean simulated) {
        Optional first = this.populatedSlots.stream().findFirst();
        if (first.isPresent()) {
            ContainerSlot slot = (ContainerSlot)first.get();
            Item beforeType = slot.getType();
            long extracted = slot.extractStack(null, entireStack ? slot.getStack().getCount() : 1, simulated);
            if (extracted > 0L) {
                return new ItemStack((ItemLike)beforeType, (int)extracted);
            }
        }
        return ItemStack.EMPTY;
    }

    public void shiftSlots(int shiftBy, BiFunction<ContainerSlot, Integer, Boolean> onEnd) {
        if (shiftBy == 0) {
            return;
        }
        ArrayList<SlotAndItemHolder> holders = new ArrayList<SlotAndItemHolder>();
        int direction = Mth.sign((double)shiftBy);
        for (ContainerSlot slot : this.inventory) {
            if (slot.isEmpty()) continue;
            int newIndex = slot.getIndex() + shiftBy;
            if (newIndex > this.getContainerSize() - 1 || newIndex < 0) {
                if (onEnd.apply(slot, direction).booleanValue()) continue;
                newIndex = direction == 1 ? this.getContainerSize() - 1 : 0;
            }
            holders.add(new SlotAndItemHolder(slot.getIndex(), newIndex, slot.getStack()));
        }
        if (!holders.isEmpty() && holders.size() != this.getContainerSize()) {
            this.processHolders(holders, direction);
        }
    }

    private void processHolders(List<SlotAndItemHolder> holders, int direction) {
        if (direction == 1) {
            for (int i = holders.size() - 1; i >= 0; --i) {
                this.shiftSlot(holders.get(i));
            }
        } else {
            for (SlotAndItemHolder holder : holders) {
                this.shiftSlot(holder);
            }
        }
    }

    private void shiftSlot(SlotAndItemHolder holder) {
        ContainerSlot next = this.getSlot(holder.nextIndex());
        if (next.isEmpty()) {
            next.setStack(holder.stack());
            this.getSlot(holder.currentIndex()).setStack(ItemStack.EMPTY);
        }
    }

    public static void setOtherAndEmptyCurrent(ContainerSlot current, ContainerSlot other) {
        other.setStack(current.getStack());
        current.setStack(ItemStack.EMPTY);
    }

    @NotNull
    public Set<ContainerSlot> getFilteredSlots(@Nullable Item type) {
        if (type == null) {
            return this.populatedSlots;
        }
        if (this.filteredSlots.containsKey(type)) {
            return new HashSet<ContainerSlot>((Collection)this.filteredSlots.get(type));
        }
        return new HashSet<ContainerSlot>();
    }

    public Set<ContainerSlot> getInsertableSlotsFor(Item type, boolean shouldIncludeEmpty) {
        Set<ContainerSlot> filteredSlots = this.getFilteredSlots(type);
        if (shouldIncludeEmpty) {
            filteredSlots.addAll(this.getFilteredSlots(Items.AIR));
        }
        return filteredSlots;
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.populatedSlots.isEmpty();
    }

    @Override
    @NotNull
    public ItemStack getItem(int slot) {
        return this.inventory.get(slot).getStack();
    }

    public ContainerSlot getSlot(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.inventory.get(slot).setStack(stack);
    }

    public void clearAndDropContents(Level level, BlockPos dropPos) {
        for (ContainerSlot slot : this.populatedSlots) {
            Containers.dropItemStack((Level)level, (double)dropPos.getX(), (double)dropPos.getY(), (double)dropPos.getZ(), (ItemStack)slot.getStack());
        }
        this.clearContent();
    }

    @Override
    public void clearContent() {
        Collections.fill(this.inventory, ContainerSlot.EMPTY);
        this.populatedSlots.clear();
        this.filteredSlots.clear();
        this.setChanged();
    }

    @Override
    public void onStackItemChange(ContainerSlot slot, ItemStack oldSlotStack, ItemStack newSlotStack) {
        int oldcount = oldSlotStack.getCount();
        int newCount = newSlotStack.getCount();
        this.storedItemCount += newCount - oldcount;
        if (ItemStack.isSameItem((ItemStack)oldSlotStack, (ItemStack)newSlotStack)) {
            return;
        }
        Item newItem = newSlotStack.getItem();
        Item oldItem = oldSlotStack.getItem();
        this.filteredSlots.computeIfAbsent(newItem, $ -> new HashSet()).add(slot);
        if (this.filteredSlots.containsKey(oldItem)) {
            Set<ContainerSlot> oldFilteredSlot = this.filteredSlots.get(oldItem);
            oldFilteredSlot.remove(slot);
            if (oldFilteredSlot.isEmpty()) {
                this.filteredSlots.remove(oldItem);
            }
        }
        if (newSlotStack.isEmpty()) {
            this.populatedSlots.remove(slot);
        } else if (oldSlotStack.isEmpty() != newSlotStack.isEmpty()) {
            this.populatedSlots.add(slot);
        }
    }

    @Override
    public CompoundTag write(HolderLookup.Provider provider) {
        CompoundTag invCompound = new CompoundTag();
        invCompound.putInt("Stored Count", this.storedItemCount);
        ListTag inv = new ListTag();
        for (ContainerSlot slot : this.inventory) {
            inv.add((Object)slot.write(provider));
        }
        invCompound.put("Items", (Tag)inv);
        return invCompound;
    }

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag nbt) {
        this.storedItemCount = nbt.getInt("Stored Count");
        ListTag inv = nbt.getList("Items", 10);
        for (Tag tag : inv) {
            CompoundTag itemTag = (CompoundTag)tag;
            this.inventory.get(itemTag.getInt("index")).read(provider, itemTag);
        }
    }

    @Override
    public void populateFields(ContainerSlot slot) {
        this.filteredSlots.computeIfAbsent(slot.getType(), $ -> new HashSet()).add(slot);
        if (!slot.getStack().isEmpty()) {
            this.populatedSlots.add(slot);
        }
    }

    @Override
    public int getMaxStackSize() {
        return this.maxStackSize;
    }

    @Override
    public List<ContainerSlot> getInventoryAsList() {
        return this.inventory;
    }

    @Override
    public Set<ContainerSlot> getPopulatedSlots() {
        return this.populatedSlots;
    }

    public float getFillLevel() {
        return (float)(this.getContainerSize() * this.getMaxStackSize()) / (float)this.storedItemCount;
    }

    public boolean isFull() {
        return this.getFillLevel() == 1.0f;
    }

    public ContainerSlot getFirst() {
        return this.getSlot(0);
    }

    public ContainerSlot getLast() {
        return this.getSlot(this.getContainerSize() - 1);
    }

    public record SlotAndItemHolder(int currentIndex, int nextIndex, ItemStack stack) {
    }
}
