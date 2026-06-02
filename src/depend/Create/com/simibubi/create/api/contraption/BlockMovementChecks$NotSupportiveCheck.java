/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.api.contraption;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public static interface BlockMovementChecks.NotSupportiveCheck {
    public BlockMovementChecks.CheckResult isNotSupportive(BlockState var1, Direction var2);
}
