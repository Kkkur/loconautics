/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.HitResult
 */
package com.simibubi.create.content.contraptions.pulley;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.pulley.PulleyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.HitResult;

private static class PulleyBlock.RopeBlockBase
extends Block
implements SimpleWaterloggedBlock {
    public PulleyBlock.RopeBlockBase(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)super.defaultBlockState().setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return AllBlocks.ROPE_PULLEY.asStack();
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(isMoving || state.hasProperty((Property)BlockStateProperties.WATERLOGGED) && newState.hasProperty((Property)BlockStateProperties.WATERLOGGED) && state.getValue((Property)BlockStateProperties.WATERLOGGED) != newState.getValue((Property)BlockStateProperties.WATERLOGGED))) {
            PulleyBlock.onRopeBroken(worldIn, pos.above());
            if (!worldIn.isClientSide) {
                BlockState above = worldIn.getBlockState(pos.above());
                BlockState below = worldIn.getBlockState(pos.below());
                if (above.getBlock() instanceof PulleyBlock.RopeBlockBase) {
                    worldIn.destroyBlock(pos.above(), true);
                }
                if (below.getBlock() instanceof PulleyBlock.RopeBlockBase) {
                    worldIn.destroyBlock(pos.below(), true);
                }
            }
        }
        if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
            worldIn.removeBlockEntity(pos);
        }
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED) != false ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{BlockStateProperties.WATERLOGGED});
        super.createBlockStateDefinition(builder);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
        if (((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)).booleanValue()) {
            world.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader)world));
        }
        return state;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState FluidState2 = context.getLevel().getFluidState(context.getClickedPos());
        return (BlockState)super.getStateForPlacement(context).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(FluidState2.getType() == Fluids.WATER));
    }
}
