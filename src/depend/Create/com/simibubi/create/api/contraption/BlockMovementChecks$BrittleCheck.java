/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.api.contraption;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public static interface BlockMovementChecks.BrittleCheck {
    public BlockMovementChecks.CheckResult isBrittle(BlockState var1);
}
