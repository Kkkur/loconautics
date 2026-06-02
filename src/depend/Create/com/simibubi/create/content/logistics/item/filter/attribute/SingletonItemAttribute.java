/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.logistics.item.filter.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public final class SingletonItemAttribute
implements ItemAttribute {
    private final Type type;
    private final BiPredicate<ItemStack, Level> predicate;
    private final String translationKey;

    public SingletonItemAttribute(Type type, BiPredicate<ItemStack, Level> predicate, String translationKey) {
        this.type = type;
        this.predicate = predicate;
        this.translationKey = translationKey;
    }

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return this.predicate.test(stack, world);
    }

    @Override
    public ItemAttributeType getType() {
        return this.type;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    public static final class Type
    implements ItemAttributeType {
        private final SingletonItemAttribute attribute;

        public Type(Function<Type, SingletonItemAttribute> singletonFunc) {
            this.attribute = singletonFunc.apply(this);
        }

        @Override
        @NotNull
        public ItemAttribute createAttribute() {
            return this.attribute;
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            if (this.attribute.appliesTo(stack, level)) {
                return List.of(this.attribute);
            }
            return List.of();
        }

        @Override
        public MapCodec<? extends ItemAttribute> codec() {
            return Codec.unit((Object)this.attribute).fieldOf("value");
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ? extends ItemAttribute> streamCodec() {
            return StreamCodec.unit((Object)this.attribute);
        }
    }
}
