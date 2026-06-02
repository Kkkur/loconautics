/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.Holder
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package com.simibubi.create.content.logistics.filter;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.filter.AttributeFilterMenu;
import com.simibubi.create.content.logistics.filter.AttributeFilterWhitelistMode;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.InTagAttribute;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class AttributeFilterItem
extends FilterItem {
    protected AttributeFilterItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public List<Component> makeSummary(ItemStack filter) {
        ArrayList<Component> list = new ArrayList<Component>();
        AttributeFilterWhitelistMode whitelistMode = (AttributeFilterWhitelistMode)((Object)filter.get(AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE));
        list.add((Component)(whitelistMode == AttributeFilterWhitelistMode.WHITELIST_CONJ ? CreateLang.translateDirect("gui.attribute_filter.allow_list_conjunctive", new Object[0]) : (whitelistMode == AttributeFilterWhitelistMode.WHITELIST_DISJ ? CreateLang.translateDirect("gui.attribute_filter.allow_list_disjunctive", new Object[0]) : CreateLang.translateDirect("gui.attribute_filter.deny_list", new Object[0]))).withStyle(ChatFormatting.GOLD));
        int count = 0;
        List attributes = (List)filter.getOrDefault(AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES, List.of());
        for (ItemAttribute.ItemAttributeEntry attributeEntry : attributes) {
            ItemAttribute attribute = attributeEntry.attribute();
            if (attribute == null) continue;
            boolean inverted = attributeEntry.inverted();
            if (count > 3) {
                list.add((Component)Component.literal((String)"- ...").withStyle(ChatFormatting.DARK_GRAY));
                break;
            }
            list.add((Component)Component.literal((String)"- ").append((Component)attribute.format(inverted)));
            ++count;
        }
        if (count == 0) {
            return Collections.emptyList();
        }
        return list;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return AttributeFilterMenu.create(id, inv, player.getMainHandItem());
    }

    @Override
    public DataComponentType<?> getComponentType() {
        return AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES;
    }

    @Override
    public FilterItemStack makeStackWrapper(ItemStack filter) {
        return new FilterItemStack.AttributeFilterItemStack(filter);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public ItemStack[] getFilterItems(ItemStack stack) {
        ArrayList<ItemStack> stacks;
        TagKey<Item> tag;
        ItemAttribute attribute;
        AttributeFilterWhitelistMode whitelistMode = (AttributeFilterWhitelistMode)((Object)stack.get(AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE));
        List attributes = (List)stack.getOrDefault(AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES, List.of());
        if (whitelistMode != AttributeFilterWhitelistMode.WHITELIST_DISJ || attributes.size() != 1 || !((attribute = ((ItemAttribute.ItemAttributeEntry)attributes.getFirst()).attribute()) instanceof InTagAttribute)) return new ItemStack[0];
        InTagAttribute inTagAttribute = (InTagAttribute)attribute;
        try {
            TagKey<Item> tagKey;
            tag = tagKey = inTagAttribute.tag();
            stacks = new ArrayList<ItemStack>();
        }
        catch (Throwable throwable) {
            throw new MatchException(throwable.toString(), throwable);
        }
        for (Holder holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            stacks.add(new ItemStack((ItemLike)holder.value()));
        }
        return (ItemStack[])stacks.toArray(ItemStack[]::new);
    }
}
