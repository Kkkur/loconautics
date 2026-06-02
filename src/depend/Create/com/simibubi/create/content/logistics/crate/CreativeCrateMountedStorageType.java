/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.crate;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.content.logistics.crate.CreativeCrateBlockEntity;
import com.simibubi.create.content.logistics.crate.CreativeCrateMountedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CreativeCrateMountedStorageType
extends MountedItemStorageType<CreativeCrateMountedStorage> {
    public CreativeCrateMountedStorageType() {
        super(CreativeCrateMountedStorage.CODEC);
    }

    @Override
    @Nullable
    public CreativeCrateMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof CreativeCrateBlockEntity) {
            CreativeCrateBlockEntity crate = (CreativeCrateBlockEntity)be;
            ItemStack supplied = crate.filtering.getFilter();
            return new CreativeCrateMountedStorage(supplied);
        }
        return null;
    }
}
