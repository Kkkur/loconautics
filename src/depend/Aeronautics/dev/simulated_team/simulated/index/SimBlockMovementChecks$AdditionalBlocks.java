/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.simulated_team.simulated.index;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public static interface SimBlockMovementChecks.AdditionalBlocks {
    public Iterable<BlockPos> addAdditionalBlocks(BlockState var1, Level var2, BlockPos var3, Set<BlockPos> var4);
}
