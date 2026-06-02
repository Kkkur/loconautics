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
package com.simibubi.create.content.equipment.toolbox;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxMountedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ToolboxMountedStorageType
extends MountedItemStorageType<ToolboxMountedStorage> {
    public ToolboxMountedStorageType() {
        super(ToolboxMountedStorage.CODEC);
    }

    @Override
    @Nullable
    public ToolboxMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        ToolboxMountedStorage toolboxMountedStorage;
        if (be instanceof ToolboxBlockEntity) {
            ToolboxBlockEntity toolbox = (ToolboxBlockEntity)be;
            toolboxMountedStorage = ToolboxMountedStorage.fromToolbox(toolbox);
        } else {
            toolboxMountedStorage = null;
        }
        return toolboxMountedStorage;
    }
}
