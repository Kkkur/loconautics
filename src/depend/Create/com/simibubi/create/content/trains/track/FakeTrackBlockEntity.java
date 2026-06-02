/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FakeTrackBlockEntity
extends SyncedBlockEntity {
    int keepAlive;

    public FakeTrackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.keepAlive();
    }

    public void randomTick() {
        --this.keepAlive;
        if (this.keepAlive > 0) {
            return;
        }
        this.level.removeBlock(this.worldPosition, false);
    }

    public void keepAlive() {
        this.keepAlive = 3;
    }
}
