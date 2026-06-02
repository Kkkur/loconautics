/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.WrittenBookContent
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.logistics.item.filter.attribute.attributes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.AllItemAttributeTypes;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record BookAuthorAttribute(String author) implements ItemAttribute
{
    public static final MapCodec<BookAuthorAttribute> CODEC = Codec.STRING.xmap(BookAuthorAttribute::new, BookAuthorAttribute::author).fieldOf("value");
    public static final StreamCodec<ByteBuf, BookAuthorAttribute> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(BookAuthorAttribute::new, BookAuthorAttribute::author);

    private static String extractAuthor(ItemStack stack) {
        if (stack.has(DataComponents.WRITTEN_BOOK_CONTENT)) {
            return ((WrittenBookContent)stack.get(DataComponents.WRITTEN_BOOK_CONTENT)).author();
        }
        return "";
    }

    @Override
    public boolean appliesTo(ItemStack itemStack, Level level) {
        return BookAuthorAttribute.extractAuthor(itemStack).equals(this.author);
    }

    @Override
    public String getTranslationKey() {
        return "book_author";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{this.author};
    }

    @Override
    public ItemAttributeType getType() {
        return AllItemAttributeTypes.BOOK_AUTHOR;
    }

    public static class Type
    implements ItemAttributeType {
        @Override
        @NotNull
        public ItemAttribute createAttribute() {
            return new BookAuthorAttribute("dummy");
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            ArrayList<ItemAttribute> list = new ArrayList<ItemAttribute>();
            String name = BookAuthorAttribute.extractAuthor(stack);
            if (!name.isEmpty()) {
                list.add(new BookAuthorAttribute(name));
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
