/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.logistics.item.filter.attribute.attributes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.AddedByAttribute;
import java.util.Collections;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public static class AddedByAttribute.Type
implements ItemAttributeType {
    @Override
    @NotNull
    public ItemAttribute createAttribute() {
        return new AddedByAttribute("dummy");
    }

    @Override
    public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
        String id = stack.getItem().getCreatorModId(stack);
        return id == null ? Collections.emptyList() : List.of(new AddedByAttribute(id));
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
