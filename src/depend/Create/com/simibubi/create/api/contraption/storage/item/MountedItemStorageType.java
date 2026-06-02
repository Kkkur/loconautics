/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.util.entry.RegistryEntry
 *  com.tterrag.registrate.util.nullness.NonNullUnaryOperator
 *  net.minecraft.Util
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.contraption.storage.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.impl.contraption.storage.MountedItemStorageFallbackProvider;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class MountedItemStorageType<T extends MountedItemStorage> {
    public static final Codec<MountedItemStorageType<?>> CODEC = CreateBuiltInRegistries.MOUNTED_ITEM_STORAGE_TYPE.byNameCodec();
    public static final SimpleRegistry<Block, MountedItemStorageType<?>> REGISTRY = (SimpleRegistry)Util.make(() -> {
        SimpleRegistry registry = SimpleRegistry.create();
        registry.registerProvider(MountedItemStorageFallbackProvider.INSTANCE);
        return registry;
    });
    public final MapCodec<? extends T> codec;
    public final Holder.Reference<MountedItemStorageType<?>> holder = CreateBuiltInRegistries.MOUNTED_ITEM_STORAGE_TYPE.createIntrusiveHolder((Object)this);

    protected MountedItemStorageType(MapCodec<? extends T> codec) {
        this.codec = codec;
    }

    public final boolean is(TagKey<MountedItemStorageType<?>> tag) {
        return this.holder.is(tag);
    }

    @Nullable
    public abstract T mount(Level var1, BlockState var2, BlockPos var3, @Nullable BlockEntity var4);

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> mountedItemStorage(RegistryEntry<MountedItemStorageType<?>, ? extends MountedItemStorageType<?>> type) {
        return builder -> (BlockBuilder)builder.onRegisterAfter(CreateRegistries.MOUNTED_ITEM_STORAGE_TYPE, block -> REGISTRY.register((Block)block, (MountedItemStorageType)type.get()));
    }
}
