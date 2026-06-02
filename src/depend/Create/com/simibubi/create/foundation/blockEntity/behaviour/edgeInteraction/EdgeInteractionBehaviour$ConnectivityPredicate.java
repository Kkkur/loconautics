/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

@FunctionalInterface
public static interface EdgeInteractionBehaviour.ConnectivityPredicate {
    public boolean test(Level var1, BlockPos var2, Direction var3, Direction var4);
}
