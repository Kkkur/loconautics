/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.WrittenBookContent
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.logistics.item.filter.attribute.attributes;

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
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record BookCopyAttribute(int generation) implements ItemAttribute
{
    public static final MapCodec<BookCopyAttribute> CODEC = ExtraCodecs.NON_NEGATIVE_INT.xmap(BookCopyAttribute::new, BookCopyAttribute::generation).fieldOf("value");
    public static final StreamCodec<ByteBuf, BookCopyAttribute> STREAM_CODEC = ByteBufCodecs.INT.map(BookCopyAttribute::new, BookCopyAttribute::generation);

    private static int extractGeneration(ItemStack stack) {
        if (stack.has(DataComponents.WRITTEN_BOOK_CONTENT)) {
            return ((WrittenBookContent)stack.get(DataComponents.WRITTEN_BOOK_CONTENT)).generation();
        }
        return -1;
    }

    @Override
    public boolean appliesTo(ItemStack itemStack, Level level) {
        return BookCopyAttribute.extractGeneration(itemStack) == this.generation;
    }

    @Override
    public String getTranslationKey() {
        return switch (this.generation) {
            case 0 -> "book_copy_original";
            case 1 -> "book_copy_first";
            case 2 -> "book_copy_second";
            default -> "book_copy_tattered";
        };
    }

    @Override
    public ItemAttributeType getType() {
        return AllItemAttributeTypes.BOOK_COPY;
    }

    public static class Type
    implements ItemAttributeType {
        @Override
        @NotNull
        public ItemAttribute createAttribute() {
            return new BookCopyAttribute(-1);
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            ArrayList<ItemAttribute> list = new ArrayList<ItemAttribute>();
            int generation = BookCopyAttribute.extractGeneration(stack);
            if (generation >= 0) {
                list.add(new BookCopyAttribute(generation));
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
