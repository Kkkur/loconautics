/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.logistics.filter;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.filter.AttributeFilterWhitelistMode;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.ListFilterItem;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class FilterItemStack {
    private final ItemStack filterItemStack;
    private boolean fluidExtracted;
    private FluidStack filterFluidStack;

    public static FilterItemStack of(ItemStack filter) {
        Item item;
        if (!filter.isComponentsPatchEmpty() && (item = filter.getItem()) instanceof FilterItem) {
            FilterItem item2 = (FilterItem)item;
            FilterItemStack.trimFilterComponents(filter);
            return item2.makeStackWrapper(filter);
        }
        return new FilterItemStack(filter);
    }

    public static FilterItemStack of(HolderLookup.Provider registries, CompoundTag tag) {
        return FilterItemStack.of(ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)tag));
    }

    public static FilterItemStack empty() {
        return FilterItemStack.of(ItemStack.EMPTY);
    }

    private static void trimFilterComponents(ItemStack filter) {
        filter.remove(DataComponents.ENCHANTMENTS);
        filter.remove(DataComponents.ATTRIBUTE_MODIFIERS);
    }

    public boolean isEmpty() {
        return this.filterItemStack.isEmpty();
    }

    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        return (CompoundTag)this.filterItemStack.saveOptional(registries);
    }

    public ItemStack item() {
        return this.filterItemStack;
    }

    public FluidStack fluid(Level level) {
        this.resolveFluid(level);
        return this.filterFluidStack;
    }

    public boolean isFilterItem() {
        return this.filterItemStack.getItem() instanceof FilterItem;
    }

    public boolean test(Level world, ItemStack stack) {
        return this.test(world, stack, false);
    }

    public boolean test(Level world, FluidStack stack) {
        return this.test(world, stack, true);
    }

    public boolean test(Level world, ItemStack stack, boolean matchNBT) {
        if (this.isEmpty()) {
            return true;
        }
        return FilterItem.testDirect(this.filterItemStack, stack, matchNBT);
    }

    public boolean test(Level world, FluidStack stack, boolean matchNBT) {
        if (this.isEmpty()) {
            return true;
        }
        if (stack.isEmpty()) {
            return false;
        }
        this.resolveFluid(world);
        if (this.filterFluidStack.isEmpty()) {
            return false;
        }
        if (!matchNBT) {
            return this.filterFluidStack.getFluid().isSame(stack.getFluid());
        }
        return FluidStack.isSameFluidSameComponents((FluidStack)this.filterFluidStack, (FluidStack)stack);
    }

    private void resolveFluid(Level world) {
        if (!this.fluidExtracted) {
            this.fluidExtracted = true;
            if (GenericItemEmptying.canItemBeEmptied(world, this.filterItemStack)) {
                this.filterFluidStack = (FluidStack)GenericItemEmptying.emptyItem(world, this.filterItemStack, true).getFirst();
            }
        }
    }

    protected FilterItemStack(ItemStack filter) {
        this.filterItemStack = filter;
        this.filterFluidStack = FluidStack.EMPTY;
        this.fluidExtracted = false;
    }

    public static class PackageFilterItemStack
    extends FilterItemStack {
        public String filterString;

        public PackageFilterItemStack(ItemStack filter) {
            super(filter);
            this.filterString = PackageItem.getAddress(filter);
        }

        @Override
        public boolean test(Level world, ItemStack stack, boolean matchNBT) {
            return this.filterString.isBlank() && super.test(world, stack, matchNBT) || PackageItem.isPackage(stack) && PackageItem.matchAddress(stack, this.filterString);
        }

        @Override
        public boolean test(Level world, FluidStack stack, boolean matchNBT) {
            return false;
        }
    }

    public static class AttributeFilterItemStack
    extends FilterItemStack {
        public AttributeFilterWhitelistMode whitelistMode;
        public List<Pair<ItemAttribute, Boolean>> attributeTests;

        public AttributeFilterItemStack(ItemStack filter) {
            super(filter);
            boolean defaults = !filter.has(AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES);
            this.attributeTests = new ArrayList<Pair<ItemAttribute, Boolean>>();
            this.whitelistMode = (AttributeFilterWhitelistMode)((Object)filter.getOrDefault(AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE, (Object)AttributeFilterWhitelistMode.WHITELIST_DISJ));
            List attributes = defaults ? new ArrayList() : (List)filter.get(AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES);
            for (ItemAttribute.ItemAttributeEntry attributeEntry : attributes) {
                ItemAttribute attribute = attributeEntry.attribute();
                if (attribute == null) continue;
                this.attributeTests.add((Pair<ItemAttribute, Boolean>)Pair.of((Object)attribute, (Object)attributeEntry.inverted()));
            }
        }

        @Override
        public boolean test(Level world, FluidStack stack, boolean matchNBT) {
            return false;
        }

        @Override
        public boolean test(Level world, ItemStack stack, boolean matchNBT) {
            if (this.attributeTests.isEmpty()) {
                return super.test(world, stack, matchNBT);
            }
            block13: for (Pair<ItemAttribute, Boolean> test : this.attributeTests) {
                boolean matches;
                ItemAttribute attribute = (ItemAttribute)test.getFirst();
                boolean inverted = (Boolean)test.getSecond();
                boolean bl = matches = attribute.appliesTo(stack, world) != inverted;
                if (matches) {
                    switch (this.whitelistMode) {
                        case BLACKLIST: {
                            return false;
                        }
                        case WHITELIST_CONJ: {
                            continue block13;
                        }
                        case WHITELIST_DISJ: {
                            return true;
                        }
                    }
                    continue;
                }
                switch (this.whitelistMode) {
                    case BLACKLIST: 
                    case WHITELIST_DISJ: {
                        continue block13;
                    }
                    case WHITELIST_CONJ: {
                        return false;
                    }
                }
            }
            return switch (this.whitelistMode) {
                default -> throw new MatchException(null, null);
                case AttributeFilterWhitelistMode.BLACKLIST, AttributeFilterWhitelistMode.WHITELIST_CONJ -> true;
                case AttributeFilterWhitelistMode.WHITELIST_DISJ -> false;
            };
        }
    }

    public static class ListFilterItemStack
    extends FilterItemStack {
        public List<FilterItemStack> containedItems;
        public boolean shouldRespectNBT;
        public boolean isBlacklist;

        public ListFilterItemStack(ItemStack filter) {
            super(filter);
            boolean hasFilterItems = filter.has(AllDataComponents.FILTER_ITEMS);
            this.containedItems = new ArrayList<FilterItemStack>();
            ItemStackHandler items = ((ListFilterItem)filter.getItem()).getFilterItemHandler(filter);
            for (int i = 0; i < items.getSlots(); ++i) {
                ItemStack stackInSlot = items.getStackInSlot(i);
                if (stackInSlot.isEmpty()) continue;
                this.containedItems.add(FilterItemStack.of(stackInSlot));
            }
            this.shouldRespectNBT = hasFilterItems && (Boolean)filter.getOrDefault(AllDataComponents.FILTER_ITEMS_RESPECT_NBT, (Object)false) != false;
            this.isBlacklist = hasFilterItems && (Boolean)filter.getOrDefault(AllDataComponents.FILTER_ITEMS_BLACKLIST, (Object)false) != false;
        }

        @Override
        public boolean test(Level world, ItemStack stack, boolean matchNBT) {
            for (FilterItemStack filterItemStack : this.containedItems) {
                if (!filterItemStack.test(world, stack, this.shouldRespectNBT)) continue;
                return !this.isBlacklist;
            }
            return this.isBlacklist;
        }

        @Override
        public boolean test(Level world, FluidStack stack, boolean matchNBT) {
            for (FilterItemStack filterItemStack : this.containedItems) {
                if (!filterItemStack.test(world, stack, this.shouldRespectNBT)) continue;
                return !this.isBlacklist;
            }
            return this.isBlacklist;
        }
    }
}
