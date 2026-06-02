/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.NonNullList
 *  net.minecraft.util.Mth
 *  net.minecraft.world.Containers
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.ItemContainerContents
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.item;

import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.mixin.accessor.ItemStackHandlerAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public class ItemHelper {
    public static boolean sameItem(ItemStack stack, ItemStack otherStack) {
        return !otherStack.isEmpty() && stack.is(otherStack.getItem());
    }

    public static Predicate<ItemStack> sameItemPredicate(ItemStack stack) {
        return s -> ItemHelper.sameItem(stack, s);
    }

    public static void dropContents(Level world, BlockPos pos, IItemHandler inv) {
        for (int slot = 0; slot < inv.getSlots(); ++slot) {
            Containers.dropItemStack((Level)world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (ItemStack)inv.getStackInSlot(slot));
        }
    }

    public static List<ItemStack> multipliedOutput(ItemStack in, ItemStack out) {
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        ItemStack result = out.copy();
        result.setCount(in.getCount() * out.getCount());
        while (result.getCount() > result.getMaxStackSize()) {
            stacks.add(result.split(result.getMaxStackSize()));
        }
        stacks.add(result);
        return stacks;
    }

    public static void addToList(ItemStack stack, List<ItemStack> stacks) {
        for (ItemStack s : stacks) {
            if (!ItemStack.isSameItemSameComponents((ItemStack)stack, (ItemStack)s)) continue;
            int transferred = Math.min(s.getMaxStackSize() - s.getCount(), stack.getCount());
            s.grow(transferred);
            stack.shrink(transferred);
        }
        if (stack.getCount() > 0) {
            stacks.add(stack);
        }
    }

    public static boolean isSameInventory(IItemHandler h1, IItemHandler h2) {
        if (h1 == null || h2 == null) {
            return false;
        }
        if (h1.getSlots() != h2.getSlots()) {
            return false;
        }
        for (int slot = 0; slot < h1.getSlots(); ++slot) {
            if (h1.getStackInSlot(slot) == h2.getStackInSlot(slot)) continue;
            return false;
        }
        return true;
    }

    public static <T extends IBE<? extends BlockEntity>> int calcRedstoneFromBlockEntity(T ibe, Level level, BlockPos pos) {
        return ibe.getBlockEntityOptional((BlockGetter)level, pos).map(be -> (IItemHandler)level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null)).map(ItemHelper::calcRedstoneFromInventory).orElse(0);
    }

    public static int calcRedstoneFromInventory(@Nullable IItemHandler inv) {
        if (inv == null) {
            return 0;
        }
        int i = 0;
        float f = 0.0f;
        int totalSlots = inv.getSlots();
        for (int j = 0; j < inv.getSlots(); ++j) {
            int slotLimit = inv.getSlotLimit(j);
            if (slotLimit == 0) {
                --totalSlots;
                continue;
            }
            ItemStack itemstack = inv.getStackInSlot(j);
            if (itemstack.isEmpty()) continue;
            f += (float)itemstack.getCount() / (float)Math.min(slotLimit, itemstack.getMaxStackSize());
            ++i;
        }
        if (totalSlots == 0) {
            return 0;
        }
        return Mth.floor((float)((f /= (float)totalSlots) * 14.0f)) + (i > 0 ? 1 : 0);
    }

    public static List<Pair<Ingredient, MutableInt>> condenseIngredients(NonNullList<Ingredient> recipeIngredients) {
        ArrayList<Pair<Ingredient, MutableInt>> actualIngredients = new ArrayList<Pair<Ingredient, MutableInt>>();
        block0: for (Ingredient igd : recipeIngredients) {
            block1: for (Pair pair : actualIngredients) {
                ItemStack[] stacks2;
                ItemStack[] stacks1 = ((Ingredient)pair.getFirst()).getItems();
                if (stacks1.length != (stacks2 = igd.getItems()).length) continue;
                for (int i = 0; i <= stacks1.length; ++i) {
                    if (i == stacks1.length) {
                        ((MutableInt)pair.getSecond()).increment();
                        continue block0;
                    }
                    if (!ItemStack.matches((ItemStack)stacks1[i], (ItemStack)stacks2[i])) continue block1;
                }
            }
            actualIngredients.add((Pair<Ingredient, MutableInt>)Pair.of((Object)igd, (Object)new MutableInt(1)));
        }
        return actualIngredients;
    }

    public static boolean matchIngredients(Ingredient i1, Ingredient i2) {
        ItemStack[] stacks2;
        if (i1 == i2) {
            return true;
        }
        ItemStack[] stacks1 = i1.getItems();
        if (stacks1 == (stacks2 = i2.getItems())) {
            return true;
        }
        if (stacks1.length == stacks2.length) {
            for (int i = 0; i < stacks1.length; ++i) {
                if (ItemStack.isSameItem((ItemStack)stacks1[i], (ItemStack)stacks2[i])) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean matchAllIngredients(NonNullList<Ingredient> ingredients) {
        if (ingredients.size() <= 1) {
            return true;
        }
        Ingredient firstIngredient = (Ingredient)ingredients.get(0);
        for (int i = 1; i < ingredients.size(); ++i) {
            if (ItemHelper.matchIngredients(firstIngredient, (Ingredient)ingredients.get(i))) continue;
            return false;
        }
        return true;
    }

    public static ItemStack extract(IItemHandler inv, Predicate<ItemStack> test, boolean simulate) {
        return ItemHelper.extract(inv, test, ExtractionCountMode.UPTO, 64, simulate);
    }

    public static ItemStack extract(IItemHandler inv, Predicate<ItemStack> test, int exactAmount, boolean simulate) {
        return ItemHelper.extract(inv, test, ExtractionCountMode.EXACTLY, exactAmount, simulate);
    }

    public static ItemStack extract(IItemHandler inv, Predicate<ItemStack> test, ExtractionCountMode mode, int amount, boolean simulate) {
        boolean amountRequired;
        ItemStack extracting = ItemStack.EMPTY;
        boolean checkHasEnoughItems = amountRequired = mode == ExtractionCountMode.EXACTLY;
        boolean hasEnoughItems = !checkHasEnoughItems;
        boolean potentialOtherMatch = false;
        int maxExtractionCount = amount;
        block0: while (true) {
            extracting = ItemStack.EMPTY;
            for (int slot = 0; slot < inv.getSlots(); ++slot) {
                int amountToExtractFromThisSlot;
                ItemStack stack;
                ItemStack slotStack = inv.getStackInSlot(slot);
                if (slotStack.isEmpty() || (stack = inv.extractItem(slot, amountToExtractFromThisSlot = Math.min(maxExtractionCount - extracting.getCount(), slotStack.getMaxStackSize()), true)).isEmpty() || !test.test(stack)) continue;
                if (!extracting.isEmpty() && !ItemHelper.canItemStackAmountsStack(stack, extracting)) {
                    potentialOtherMatch = true;
                    continue;
                }
                if (extracting.isEmpty()) {
                    extracting = stack.copy();
                } else {
                    extracting.grow(stack.getCount());
                }
                if (!simulate && hasEnoughItems) {
                    inv.extractItem(slot, stack.getCount(), false);
                }
                if (extracting.getCount() < maxExtractionCount) continue;
                if (!checkHasEnoughItems) break block0;
                hasEnoughItems = true;
                checkHasEnoughItems = false;
                continue block0;
            }
            if (!extracting.isEmpty() && !hasEnoughItems && potentialOtherMatch) {
                ItemStack blackListed = extracting.copy();
                test = test.and(i -> !ItemStack.isSameItemSameComponents((ItemStack)i, (ItemStack)blackListed));
                continue;
            }
            if (!checkHasEnoughItems) break;
            checkHasEnoughItems = false;
        }
        if (amountRequired && extracting.getCount() < amount) {
            return ItemStack.EMPTY;
        }
        return extracting;
    }

    public static ItemStack extract(IItemHandler inv, Predicate<ItemStack> test, Function<ItemStack, Integer> amountFunction, boolean simulate) {
        ItemStack extracting = ItemStack.EMPTY;
        int maxExtractionCount = 64;
        for (int slot = 0; slot < inv.getSlots(); ++slot) {
            ItemStack stack;
            if (extracting.isEmpty()) {
                int maxExtractionCountForItem;
                ItemStack stackInSlot = inv.getStackInSlot(slot);
                if (stackInSlot.isEmpty() || !test.test(stackInSlot) || (maxExtractionCountForItem = amountFunction.apply(stackInSlot).intValue()) == 0) continue;
                maxExtractionCount = Math.min(maxExtractionCount, maxExtractionCountForItem);
            }
            if (!test.test(stack = inv.extractItem(slot, maxExtractionCount - extracting.getCount(), true)) || !extracting.isEmpty() && !ItemHelper.canItemStackAmountsStack(stack, extracting)) continue;
            if (extracting.isEmpty()) {
                extracting = stack.copy();
            } else {
                extracting.grow(stack.getCount());
            }
            if (!simulate) {
                inv.extractItem(slot, stack.getCount(), false);
            }
            if (extracting.getCount() >= maxExtractionCount) break;
        }
        return extracting;
    }

    public static boolean canItemStackAmountsStack(ItemStack a, ItemStack b) {
        return ItemStack.isSameItemSameComponents((ItemStack)a, (ItemStack)b) && a.getCount() + b.getCount() <= a.getMaxStackSize();
    }

    public static ItemStack findFirstMatch(IItemHandler inv, Predicate<ItemStack> test) {
        int slot = ItemHelper.findFirstMatchingSlotIndex(inv, test);
        if (slot == -1) {
            return ItemStack.EMPTY;
        }
        return inv.getStackInSlot(slot);
    }

    public static int findFirstMatchingSlotIndex(IItemHandler inv, Predicate<ItemStack> test) {
        for (int slot = 0; slot < inv.getSlots(); ++slot) {
            ItemStack toTest = inv.getStackInSlot(slot);
            if (!test.test(toTest)) continue;
            return slot;
        }
        return -1;
    }

    public static ItemStack fromItemEntity(Entity entityIn) {
        ItemStack itemStack;
        if (!entityIn.isAlive()) {
            return ItemStack.EMPTY;
        }
        if (entityIn instanceof PackageEntity) {
            PackageEntity packageEntity = (PackageEntity)entityIn;
            return packageEntity.getBox();
        }
        if (entityIn instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity)entityIn;
            itemStack = itemEntity.getItem();
        } else {
            itemStack = ItemStack.EMPTY;
        }
        return itemStack;
    }

    public static void fillItemStackHandler(ItemContainerContents contents, ItemStackHandler inv) {
        List itemStacks = contents.stream().toList();
        for (int i = 0; i < itemStacks.size(); ++i) {
            inv.setStackInSlot(i, (ItemStack)itemStacks.get(i));
        }
    }

    public static ItemContainerContents containerContentsFromHandler(ItemStackHandler handler) {
        return ItemContainerContents.fromItems(((ItemStackHandlerAccessor)handler).create$getStacks());
    }

    public static ItemStack limitCountToMaxStackSize(ItemStack stack, boolean simulate) {
        int max;
        int count = stack.getCount();
        if (count <= (max = stack.getMaxStackSize())) {
            return ItemStack.EMPTY;
        }
        ItemStack remainder = stack.copyWithCount(count - max);
        if (!simulate) {
            stack.setCount(max);
        }
        return remainder;
    }

    public static void copyContents(IItemHandler from, IItemHandlerModifiable to) {
        if (from.getSlots() != to.getSlots()) {
            throw new IllegalArgumentException("Slot count mismatch");
        }
        for (int slot = to.getSlots() - 1; slot >= 0; --slot) {
            to.setStackInSlot(slot, ItemStack.EMPTY);
        }
        for (int i = 0; i < from.getSlots(); ++i) {
            to.setStackInSlot(i, from.getStackInSlot(i).copy());
        }
    }

    public static List<ItemStack> getNonEmptyStacks(ItemStackHandler handler) {
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            stacks.add(stack);
        }
        return stacks;
    }

    public static enum ExtractionCountMode {
        EXACTLY,
        UPTO;

    }
}
