/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTab$Type
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.logistics.item.filter.attribute.attributes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.InItemGroupAttribute;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public static class InItemGroupAttribute.Type
implements ItemAttributeType {
    @Override
    @NotNull
    public ItemAttribute createAttribute() {
        return new InItemGroupAttribute(null);
    }

    @Override
    public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
        ArrayList<ItemAttribute> list = new ArrayList<ItemAttribute>();
        for (CreativeModeTab tab : CreativeModeTabs.tabs()) {
            if (tab.getType() != CreativeModeTab.Type.CATEGORY || !InItemGroupAttribute.tabContainsItem(tab, stack)) continue;
            list.add(new InItemGroupAttribute(tab));
        }
        return list;
    }

    @Override
    public MapCodec<? extends ItemAttribute> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ? extends ItemAttribute> streamCodec() {
        return STREAM_CODEC;
    }
}
