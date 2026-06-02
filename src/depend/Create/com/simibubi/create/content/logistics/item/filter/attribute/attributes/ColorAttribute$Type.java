/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.logistics.item.filter.attribute.attributes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.ColorAttribute;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public static class ColorAttribute.Type
implements ItemAttributeType {
    @Override
    @NotNull
    public ItemAttribute createAttribute() {
        return new ColorAttribute(DyeColor.PURPLE);
    }

    @Override
    public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
        ArrayList<ItemAttribute> list = new ArrayList<ItemAttribute>();
        for (DyeColor color : ColorAttribute.findMatchingDyeColors(stack)) {
            list.add(new ColorAttribute(color));
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
