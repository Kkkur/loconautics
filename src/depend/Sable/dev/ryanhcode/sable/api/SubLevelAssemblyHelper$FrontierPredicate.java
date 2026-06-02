/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public static interface SubLevelAssemblyHelper.FrontierPredicate {
    public boolean isValidConnection(BlockPos var1, BlockState var2, BlockPos var3, BlockState var4, @Nullable Direction var5);
}
