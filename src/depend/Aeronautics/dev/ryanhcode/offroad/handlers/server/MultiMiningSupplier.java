/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.ryanhcode.offroad.handlers.server;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface MultiMiningSupplier {
    public float getBreakingSpeed(Level var1, BlockPos var2, BlockState var3);

    public boolean isActive();

    @Nullable
    public BlockPos getLocation();

    public void itemCallback(ItemStack var1);
}
