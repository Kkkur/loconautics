/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.belt.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

public static class BeltMovementHandler.TransportedEntityInfo {
    int ticksSinceLastCollision;
    BlockPos lastCollidedPos;
    BlockState lastCollidedState;

    public BeltMovementHandler.TransportedEntityInfo(BlockPos collision, BlockState belt) {
        this.refresh(collision, belt);
    }

    public void refresh(BlockPos collision, BlockState belt) {
        this.ticksSinceLastCollision = 0;
        this.lastCollidedPos = new BlockPos((Vec3i)collision).immutable();
        this.lastCollidedState = belt;
    }

    public BeltMovementHandler.TransportedEntityInfo tick() {
        ++this.ticksSinceLastCollision;
        return this;
    }

    public int getTicksSinceLastCollision() {
        return this.ticksSinceLastCollision;
    }
}
