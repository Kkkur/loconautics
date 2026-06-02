/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 */
package com.simibubi.create.api.connectivity;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

private static class ConnectivityHandler.SearchCache<T extends BlockEntity> {
    Map<BlockPos, Optional<T>> controllerMap = new HashMap<BlockPos, Optional<T>>();

    void put(BlockPos pos, T target) {
        this.controllerMap.put(pos, Optional.of(target));
    }

    void putEmpty(BlockPos pos) {
        this.controllerMap.put(pos, Optional.empty());
    }

    boolean hasVisited(BlockPos pos) {
        return this.controllerMap.containsKey(pos);
    }

    Optional<T> getOrCache(BlockEntityType<?> type, BlockGetter level, BlockPos pos) {
        if (this.hasVisited(pos)) {
            return this.controllerMap.get(pos);
        }
        Object partAt = ConnectivityHandler.partAt(type, level, pos);
        if (partAt == null) {
            this.putEmpty(pos);
            return Optional.empty();
        }
        Object controller = ConnectivityHandler.checked(level.getBlockEntity(((IMultiBlockEntityContainer)partAt).getController()));
        if (controller == null) {
            this.putEmpty(pos);
            return Optional.empty();
        }
        this.put(pos, controller);
        return Optional.of(controller);
    }
}
