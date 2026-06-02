/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.logistics.chute;

import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class ChuteItem
extends BlockItem {
    public ChuteItem(Block p_i48527_1_, Item.Properties p_i48527_2_) {
        super(p_i48527_1_, p_i48527_2_);
    }

    public InteractionResult place(BlockPlaceContext context) {
        BlockPos correctPos;
        Direction face;
        block6: {
            ChuteBlock block;
            BlockState blockState;
            Level world;
            block8: {
                block7: {
                    face = context.getClickedFace();
                    BlockPos placedOnPos = context.getClickedPos().relative(face.getOpposite());
                    world = context.getLevel();
                    BlockState placedOnState = world.getBlockState(placedOnPos);
                    if (!AbstractChuteBlock.isChute(placedOnState) || context.isSecondaryUseActive()) {
                        return super.place(context);
                    }
                    if (face.getAxis().isVertical()) {
                        return super.place(context);
                    }
                    correctPos = context.getClickedPos().above();
                    blockState = world.getBlockState(correctPos);
                    if (blockState.canBeReplaced()) break block6;
                    Block block2 = blockState.getBlock();
                    if (!(block2 instanceof ChuteBlock)) break block7;
                    block = (ChuteBlock)block2;
                    if (!world.isClientSide) break block8;
                }
                return InteractionResult.FAIL;
            }
            if (block.getFacing(blockState) == Direction.DOWN) {
                world.setBlockAndUpdate(correctPos, ProperWaterloggedBlock.withWater((LevelAccessor)world, block.updateChuteState((BlockState)blockState.setValue((Property)ChuteBlock.FACING, (Comparable)face), world.getBlockState(correctPos.above()), (BlockGetter)world, correctPos), correctPos));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }
        context = BlockPlaceContext.at((BlockPlaceContext)context, (BlockPos)correctPos, (Direction)face);
        return super.place(context);
    }
}
