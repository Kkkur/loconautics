/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.contraption.storage.item.simple;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleMountedStorageType<T extends SimpleMountedStorage>
extends MountedItemStorageType<SimpleMountedStorage> {
    protected SimpleMountedStorageType(MapCodec<T> codec) {
        super(codec);
    }

    @Override
    @Nullable
    public SimpleMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        return Optional.ofNullable(be).map(b -> this.getHandler(level, (BlockEntity)b)).map(this::createStorage).orElse(null);
    }

    protected IItemHandler getHandler(Level level, BlockEntity be) {
        IItemHandlerModifiable modifiable;
        IItemHandler handler = (IItemHandler)level.getCapability(Capabilities.ItemHandler.BLOCK, be.getBlockPos(), null);
        return handler instanceof IItemHandlerModifiable ? (modifiable = (IItemHandlerModifiable)handler) : null;
    }

    protected SimpleMountedStorage createStorage(IItemHandler handler) {
        return new SimpleMountedStorage((MountedItemStorageType<?>)this, handler);
    }

    public static final class Impl
    extends SimpleMountedStorageType<SimpleMountedStorage> {
        public Impl() {
            super(SimpleMountedStorage.CODEC);
        }
    }
}
