/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.api.contraption;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public static interface BlockMovementChecks.MovementNecessaryCheck {
    public BlockMovementChecks.CheckResult isMovementNecessary(BlockState var1, Level var2, BlockPos var3);
}
