/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@FunctionalInterface
public static interface EdgeInteractionBehaviour.ConnectionCallback {
    public void apply(Level var1, BlockPos var2, BlockPos var3);
}
