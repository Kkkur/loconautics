/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.levelWrappers.PlacementSimulationServerLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.BoneMealItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.BonemealableBlock
 *  net.minecraft.world.level.block.MangrovePropaguleBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.equipment;

import net.createmod.catnip.levelWrappers.PlacementSimulationServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class TreeFertilizerItem
extends Item {
    public TreeFertilizerItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        Block block = state.getBlock();
        if (block instanceof BonemealableBlock) {
            BonemealableBlock bonemealableBlock = (BonemealableBlock)block;
            if (state.is(BlockTags.SAPLINGS)) {
                if (state.getOptionalValue((Property)MangrovePropaguleBlock.HANGING).orElse(false).booleanValue()) {
                    return InteractionResult.PASS;
                }
                if (context.getLevel().isClientSide) {
                    BoneMealItem.addGrowthParticles((LevelAccessor)context.getLevel(), (BlockPos)context.getClickedPos(), (int)100);
                    return InteractionResult.SUCCESS;
                }
                BlockPos saplingPos = context.getClickedPos();
                TreesDreamWorld world = new TreesDreamWorld((ServerLevel)context.getLevel(), saplingPos);
                for (BlockPos pos : BlockPos.betweenClosed((int)-1, (int)0, (int)-1, (int)1, (int)0, (int)1)) {
                    if (context.getLevel().getBlockState(saplingPos.offset((Vec3i)pos)).getBlock() != block) continue;
                    world.setBlockAndUpdate(pos.above(10), this.withStage(state, 1));
                }
                bonemealableBlock.performBonemeal((ServerLevel)world, world.getRandom(), BlockPos.ZERO.above(10), this.withStage(state, 1));
                for (BlockPos pos : world.blocksAdded.keySet()) {
                    BlockPos actualPos = pos.offset((Vec3i)saplingPos).below(10);
                    BlockState newState = (BlockState)world.blocksAdded.get(pos);
                    if (context.getLevel().getBlockState(actualPos).getDestroySpeed((BlockGetter)context.getLevel(), actualPos) == -1.0f || !newState.isRedstoneConductor((BlockGetter)world, pos) && !context.getLevel().getBlockState(actualPos).getCollisionShape((BlockGetter)context.getLevel(), actualPos).isEmpty()) continue;
                    context.getLevel().destroyBlock(actualPos, true);
                    context.getLevel().setBlockAndUpdate(actualPos, newState);
                }
                if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    private BlockState withStage(BlockState original, int stage) {
        if (!original.hasProperty((Property)BlockStateProperties.STAGE)) {
            return original;
        }
        return (BlockState)original.setValue((Property)BlockStateProperties.STAGE, (Comparable)Integer.valueOf(1));
    }

    private static class TreesDreamWorld
    extends PlacementSimulationServerLevel {
        private final BlockState soil;

        protected TreesDreamWorld(ServerLevel wrapped, BlockPos saplingPos) {
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
}
