/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.logistics.item.filter.attribute.attributes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.AllItemAttributeTypes;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.stream.Collectors;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record InTagAttribute(TagKey<Item> tag) implements ItemAttribute
{
    public static final MapCodec<InTagAttribute> CODEC = TagKey.codec((ResourceKey)Registries.ITEM).xmap(InTagAttribute::new, InTagAttribute::tag).fieldOf("value");
    public static final StreamCodec<ByteBuf, InTagAttribute> STREAM_CODEC = CatnipStreamCodecBuilders.tagKey((ResourceKey)Registries.ITEM).map(InTagAttribute::new, InTagAttribute::tag);

    @Override
    public boolean appliesTo(ItemStack stack, Level level) {
        return stack.is(this.tag);
    }

    @Override
    public String getTranslationKey() {
        return "in_tag";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{"#" + String.valueOf(this.tag.location())};
    }

    @Override
    public ItemAttributeType getType() {
        return AllItemAttributeTypes.IN_TAG;
    }

    public static class Type
    implements ItemAttributeType {
        @Override
        @NotNull
        public ItemAttribute createAttribute() {
            return new InTagAttribute((TagKey<Item>)ItemTags.LOGS);
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            return stack.getTags().map(InTagAttribute::new).collect(Collectors.toList());
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
}
