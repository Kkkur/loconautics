/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.ryanhcode.sable.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockSubLevelAssemblyListener {
    default public void beforeMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
    }

    public void afterMove(ServerLevel var1, ServerLevel var2, BlockState var3, BlockPos var4, BlockPos var5);
}
