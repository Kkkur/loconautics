/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.ItemContainerContents
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.logistics.filter;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.filter.FilterMenu;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ListFilterItem
extends FilterItem {
    protected ListFilterItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public List<Component> makeSummary(ItemStack filter) {
        ArrayList<Component> list = new ArrayList<Component>();
        ItemStackHandler filterItems = this.getFilterItemHandler(filter);
        boolean blacklist = (Boolean)filter.getOrDefault(AllDataComponents.FILTER_ITEMS_BLACKLIST, (Object)false);
        list.add((Component)(blacklist ? CreateLang.translateDirect("gui.filter.deny_list", new Object[0]) : CreateLang.translateDirect("gui.filter.allow_list", new Object[0])).withStyle(ChatFormatting.GOLD));
        int count = 0;
        for (int i = 0; i < filterItems.getSlots(); ++i) {
            if (count > 3) {
                list.add((Component)Component.literal((String)"- ...").withStyle(ChatFormatting.DARK_GRAY));
                break;
            }
            ItemStack filterStack = filterItems.getStackInSlot(i);
            if (filterStack.isEmpty()) continue;
            list.add((Component)Component.literal((String)"- ").append(filterStack.getHoverName()).withStyle(ChatFormatting.GRAY));
            ++count;
        }
        if (count == 0) {
            return Collections.emptyList();
        }
        return list;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return FilterMenu.create(id, inv, player.getMainHandItem());
    }

    @Override
    public DataComponentType<?> getComponentType() {
        return AllDataComponents.FILTER_ITEMS;
    }

    @Override
    public FilterItemStack makeStackWrapper(ItemStack filter) {
        return new FilterItemStack.ListFilterItemStack(filter);
    }

    public ItemStackHandler getFilterItemHandler(ItemStack stack) {
        ItemStackHandler newInv = new ItemStackHandler(18);
        ItemContainerContents contents = (ItemContainerContents)stack.getOrDefault(AllDataComponents.FILTER_ITEMS, (Object)ItemContainerContents.EMPTY);
        ItemHelper.fillItemStackHandler(contents, newInv);
        return newInv;
    }

    @Override
    public ItemStack[] getFilterItems(ItemStack stack) {
        if (((Boolean)stack.getOrDefault(AllDataComponents.FILTER_ITEMS_BLACKLIST, (Object)false)).booleanValue()) {
            return new ItemStack[0];
        }
        return (ItemStack[])ItemHelper.getNonEmptyStacks(this.getFilterItemHandler(stack)).toArray(ItemStack[]::new);
    }
}
