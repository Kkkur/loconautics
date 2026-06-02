/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  vectorwing.farmersdelight.common.registry.ModBlocks
 */
package com.simibubi.create.compat.farmersdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public class FarmersDelightCompat {
    public static boolean shouldHarvestMushroom(Level world, BlockPos pos, BlockState state) {
        return !world.getBlockState(pos.below()).is((Block)ModBlocks.RICH_SOIL.get());
    }
}
