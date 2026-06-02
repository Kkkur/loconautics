/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.impl.unpacking;

import com.simibubi.create.api.packager.unpacking.UnpackingHandler;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public enum BasinUnpackingHandler implements UnpackingHandler
{
    INSTANCE;


    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean unpack(Level level, BlockPos pos, BlockState state, Direction side, List<ItemStack> items, @Nullable PackageOrderWithCrafts orderContext, boolean simulate) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BasinBlockEntity)) {
            return false;
        }
        BasinBlockEntity basin = (BasinBlockEntity)be;
        basin.inputInventory.packagerMode = true;
        try {
            boolean bl = UnpackingHandler.DEFAULT.unpack(level, pos, state, side, items, orderContext, simulate);
            return bl;
        }
        finally {
            basin.inputInventory.packagerMode = false;
        }
    }
}
