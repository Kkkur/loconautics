/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.schematics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public static interface SchematicPrinter.PlacementPredicate {
    public boolean shouldPlace(BlockPos var1, BlockState var2, BlockEntity var3, BlockState var4, BlockState var5, boolean var6);
}
