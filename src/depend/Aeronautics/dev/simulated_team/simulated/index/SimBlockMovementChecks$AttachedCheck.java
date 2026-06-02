/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.contraption.BlockMovementChecks$CheckResult
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.simulated_team.simulated.index;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public static interface SimBlockMovementChecks.AttachedCheck {
    public BlockMovementChecks.CheckResult isBlockAttachedTowards(BlockState var1, Level var2, BlockPos var3, BlockPos var4);
}
