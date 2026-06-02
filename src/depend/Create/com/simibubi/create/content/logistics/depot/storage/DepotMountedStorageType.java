/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.depot.storage;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.content.logistics.depot.storage.DepotMountedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DepotMountedStorageType
extends MountedItemStorageType<DepotMountedStorage> {
    public DepotMountedStorageType() {
        super(DepotMountedStorage.CODEC);
    }

    @Override
    @Nullable
    public DepotMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof DepotBlockEntity) {
            DepotBlockEntity depot = (DepotBlockEntity)be;
            return DepotMountedStorage.fromDepot(depot);
        }
        return null;
    }
}
