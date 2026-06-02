/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.levelWrappers.WrappedLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.ticks.TickPriority
 */
package com.simibubi.create.content.kinetics.drill;

import java.util.HashMap;
import net.createmod.catnip.levelWrappers.WrappedLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.TickPriority;

public class CobbleGenLevel
extends WrappedLevel {
    public HashMap<BlockPos, BlockState> blocksAdded = new HashMap();

    public CobbleGenLevel(Level level) {
        super(level);
    }

    public void clear() {
        this.blocksAdded.clear();
    }

    public boolean setBlock(BlockPos pos, BlockState newState, int flags) {
        this.blocksAdded.put(pos.immutable(), newState);
        return true;
    }

    public boolean setBlockAndUpdate(BlockPos pos, BlockState state) {
        return this.setBlock(pos, state, 0);
    }

    public void scheduleTick(BlockPos pos, Block block, int delay) {
    }

    public void scheduleTick(BlockPos pos, Block block, int delay, TickPriority priority) {
    }

    public void scheduleTick(BlockPos pos, Fluid fluid, int delay) {
    }

    public void scheduleTick(BlockPos pos, Fluid fluid, int delay, TickPriority priority) {
    }

    public void levelEvent(int type, BlockPos pos, int data) {
    }

    public void levelEvent(Player player, int type, BlockPos pos, int data) {
    }

    public void blockEvent(BlockPos pos, Block block, int eventID, int eventParam) {
    }
}
