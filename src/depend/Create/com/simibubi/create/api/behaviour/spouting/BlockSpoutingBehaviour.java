/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.fluids.FluidStack
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.behaviour.spouting;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface BlockSpoutingBehaviour {
    public static final SimpleRegistry<Block, BlockSpoutingBehaviour> BY_BLOCK = SimpleRegistry.create();
    public static final SimpleRegistry<BlockEntityType<?>, BlockSpoutingBehaviour> BY_BLOCK_ENTITY = SimpleRegistry.create();

    @Nullable
    public static BlockSpoutingBehaviour get(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockSpoutingBehaviour byBlock = BY_BLOCK.get(state.getBlock());
        if (byBlock != null) {
            return byBlock;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) {
            return null;
        }
        return BY_BLOCK_ENTITY.get(be.getType());
    }

    public int fillBlock(Level var1, BlockPos var2, SpoutBlockEntity var3, FluidStack var4, boolean var5);
}
