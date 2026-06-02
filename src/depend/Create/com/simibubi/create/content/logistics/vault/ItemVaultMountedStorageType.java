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
package com.simibubi.create.content.logistics.vault;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import com.simibubi.create.content.logistics.vault.ItemVaultMountedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ItemVaultMountedStorageType
extends MountedItemStorageType<ItemVaultMountedStorage> {
    public ItemVaultMountedStorageType() {
        super(ItemVaultMountedStorage.CODEC);
    }

    @Override
    @Nullable
    public ItemVaultMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        ItemVaultMountedStorage itemVaultMountedStorage;
        if (be instanceof ItemVaultBlockEntity) {
            ItemVaultBlockEntity vault = (ItemVaultBlockEntity)be;
            itemVaultMountedStorage = ItemVaultMountedStorage.fromVault(vault);
        } else {
            itemVaultMountedStorage = null;
        }
        return itemVaultMountedStorage;
    }
}
