/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.levelWrappers.PlacementSimulationServerLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.equipment;

import net.createmod.catnip.levelWrappers.PlacementSimulationServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

private static class TreeFertilizerItem.TreesDreamWorld
extends PlacementSimulationServerLevel {
    private final BlockState soil;

    protected TreeFertilizerItem.TreesDreamWorld(ServerLevel wrapped, BlockPos saplingPos) {
        super(wrapped);
        BlockState stateUnderSapling = wrapped.getBlockState(saplingPos.below());
        if (stateUnderSapling.is(BlockTags.DIRT)) {
            stateUnderSapling = Blocks.DIRT.defaultBlockState();
        }
        this.soil = stateUnderSapling;
    }

    public BlockState getBlockState(BlockPos pos) {
        if (pos.getY() <= 9) {
            return this.soil;
        }
        return super.getBlockState(pos);
    }

    public boolean setBlock(BlockPos pos, BlockState newState, int flags) {
        if (newState.getBlock() == Blocks.PODZOL) {
            return true;
        }
        return super.setBlock(pos, newState, flags);
    }
}
