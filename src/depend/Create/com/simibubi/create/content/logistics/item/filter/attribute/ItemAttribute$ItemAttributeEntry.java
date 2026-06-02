/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.logistics.item.filter.attribute;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ItemAttribute.ItemAttributeEntry(ItemAttribute attribute, boolean inverted) {
    public static final Codec<ItemAttribute.ItemAttributeEntry> CODEC = RecordCodecBuilder.create(i -> i.group((App)CODEC.fieldOf("attribute").forGetter(ItemAttribute.ItemAttributeEntry::attribute), (App)Codec.BOOL.fieldOf("inverted").forGetter(ItemAttribute.ItemAttributeEntry::inverted)).apply((Applicative)i, ItemAttribute.ItemAttributeEntry::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttribute.ItemAttributeEntry> STREAM_CODEC = StreamCodec.composite(STREAM_CODEC, ItemAttribute.ItemAttributeEntry::attribute, (StreamCodec)ByteBufCodecs.BOOL, ItemAttribute.ItemAttributeEntry::inverted, ItemAttribute.ItemAttributeEntry::new);
}
