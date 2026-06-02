/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.logistics.filter;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.filter.AttributeFilterWhitelistMode;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.data.Pair;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public static class FilterItemStack.AttributeFilterItemStack
extends FilterItemStack {
    public AttributeFilterWhitelistMode whitelistMode;
    public List<Pair<ItemAttribute, Boolean>> attributeTests;

    public FilterItemStack.AttributeFilterItemStack(ItemStack filter) {
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
