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
 */
package com.simibubi.create.content.logistics.filter;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.filter.PackageFilterMenu;
import com.simibubi.create.foundation.utility.CreateLang;
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

public class PackageFilterItem
extends FilterItem {
    protected PackageFilterItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public List<Component> makeSummary(ItemStack filter) {
        String address = PackageItem.getAddress(filter);
        if (address.isBlank()) {
            return Collections.emptyList();
        }
        return List.of(CreateLang.text("-> ").style(ChatFormatting.GRAY).add(CreateLang.text(address).style(ChatFormatting.GOLD)).component());
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return PackageFilterMenu.create(id, inv, player.getMainHandItem());
    }

    @Override
    public DataComponentType<?> getComponentType() {
        return AllDataComponents.PACKAGE_ADDRESS;
    }

    @Override
    public FilterItemStack makeStackWrapper(ItemStack filter) {
        return new FilterItemStack.PackageFilterItemStack(filter);
    }

    @Override
    public ItemStack[] getFilterItems(ItemStack stack) {
        return new ItemStack[0];
    }
}
