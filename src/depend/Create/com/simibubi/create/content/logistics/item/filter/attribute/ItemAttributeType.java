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
package com.simibubi.create.content.logistics.item.filter.attribute;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface ItemAttributeType {
    @NotNull
    public ItemAttribute createAttribute();

    public List<ItemAttribute> getAllAttributes(ItemStack var1, Level var2);

    public MapCodec<? extends ItemAttribute> codec();

    public StreamCodec<? super RegistryFriendlyByteBuf, ? extends ItemAttribute> streamCodec();
}
