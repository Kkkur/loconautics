/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.foundation.blockEntity;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class CachedRenderBBBlockEntity
extends SyncedBlockEntity {
    private AABB renderBoundingBox;

    public CachedRenderBBBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @OnlyIn(value=Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        if (this.renderBoundingBox == null) {
            this.renderBoundingBox = this.createRenderBoundingBox();
        }
        return this.renderBoundingBox;
    }

    protected void invalidateRenderBoundingBox() {
        this.renderBoundingBox = null;
    }

    protected AABB createRenderBoundingBox() {
        return new AABB(this.getBlockPos());
    }
}
