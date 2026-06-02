/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonParseException
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.logistics.item.filter.attribute.attributes;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.AllItemAttributeTypes;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ItemNameAttribute(String itemName) implements ItemAttribute
{
    public static final MapCodec<ItemNameAttribute> CODEC = Codec.STRING.xmap(ItemNameAttribute::new, ItemNameAttribute::itemName).fieldOf("value");
    public static final StreamCodec<ByteBuf, ItemNameAttribute> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(ItemNameAttribute::new, ItemNameAttribute::itemName);

    private static String extractCustomName(ItemStack stack, Level level) {
        if (stack.has(DataComponents.CUSTOM_NAME)) {
            try {
                String customName = ((Component)stack.getOrDefault(DataComponents.CUSTOM_NAME, (Object)Component.empty())).getString();
                MutableComponent component = Component.Serializer.fromJson((String)customName, (HolderLookup.Provider)level.registryAccess());
                if (component != null) {
                    return component.getString();
                }
            }
            catch (JsonParseException jsonParseException) {
                // empty catch block
            }
        }
        return "";
    }

    @Override
    public boolean appliesTo(ItemStack itemStack, Level level) {
        return ItemNameAttribute.extractCustomName(itemStack, level).equals(this.itemName);
    }

    @Override
    public String getTranslationKey() {
        return "has_name";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{this.itemName};
    }

    @Override
    public ItemAttributeType getType() {
        return AllItemAttributeTypes.HAS_NAME;
    }

    public static class Type
    implements ItemAttributeType {
        @Override
        @NotNull
        public ItemAttribute createAttribute() {
            return new ItemNameAttribute("dummy");
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            ArrayList<ItemAttribute> list = new ArrayList<ItemAttribute>();
            String name = ItemNameAttribute.extractCustomName(stack, level);
            if (!name.isEmpty()) {
                list.add(new ItemNameAttribute(name));
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
}
