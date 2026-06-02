/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.material.Fluid
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.registry;

import com.simibubi.create.impl.registry.TagProviderImpl;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public static interface SimpleRegistry.Provider<K, V> {
    @Nullable
    public V get(K var1);

    default public void onRegister(Runnable invalidate) {
    }

    public static <K, V> SimpleRegistry.Provider<K, V> forTag(TagKey<K> tag, Function<K, Holder<K>> holderGetter, V value) {
        return new TagProviderImpl<K, V>(tag, holderGetter, value);
    }

    public static <V> SimpleRegistry.Provider<Block, V> forBlockTag(TagKey<Block> tag, V value) {
        return new TagProviderImpl<Block, V>(tag, Block::builtInRegistryHolder, value);
    }

    public static <V> SimpleRegistry.Provider<BlockEntityType<?>, V> forBlockEntityTag(TagKey<BlockEntityType<?>> tag, V value) {
        return new TagProviderImpl<BlockEntityType, V>(tag, TagProviderImpl::getBeHolder, value);
    }

    public static <V> SimpleRegistry.Provider<Item, V> forItemTag(TagKey<Item> tag, V value) {
        return new TagProviderImpl<Item, V>(tag, Item::builtInRegistryHolder, value);
    }

    public static <V> SimpleRegistry.Provider<EntityType<?>, V> forEntityTag(TagKey<EntityType<?>> tag, V value) {
        return new TagProviderImpl<EntityType, V>(tag, EntityType::builtInRegistryHolder, value);
    }

    public static <V> SimpleRegistry.Provider<Fluid, V> forFluidTag(TagKey<Fluid> tag, V value) {
        return new TagProviderImpl<Fluid, V>(tag, Fluid::builtInRegistryHolder, value);
    }
}
