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
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.processing;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.processing.AssemblyOperatorUseContext;
import com.simibubi.create.content.processing.basin.BasinBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AssemblyOperatorBlockItem
extends BlockItem {
    public AssemblyOperatorBlockItem(Block block, Item.Properties builder) {
        super(block, builder);
    }

    public InteractionResult place(BlockPlaceContext context) {
        BlockState placedOnState;
        BlockPos placedOnPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        Level level = context.getLevel();
        if (this.operatesOn((LevelReader)level, placedOnPos, placedOnState = level.getBlockState(placedOnPos)) && context.getClickedFace() == Direction.UP) {
            if (level.getBlockState(placedOnPos.above(2)).canBeReplaced()) {
                context = this.adjustContext(context, placedOnPos);
            } else {
                return InteractionResult.FAIL;
            }
        }
        return super.place(context);
    }

    protected BlockPlaceContext adjustContext(BlockPlaceContext context, BlockPos placedOnPos) {
        BlockPos up = placedOnPos.above(2);
        return new AssemblyOperatorUseContext(context.getLevel(), context.getPlayer(), context.getHand(), context.getItemInHand(), new BlockHitResult(new Vec3((double)up.getX() + 0.5 + (double)Direction.UP.getStepX() * 0.5, (double)up.getY() + 0.5 + (double)Direction.UP.getStepY() * 0.5, (double)up.getZ() + 0.5 + (double)Direction.UP.getStepZ() * 0.5), Direction.UP, up, false));
    }

    protected boolean operatesOn(LevelReader world, BlockPos pos, BlockState placedOnState) {
        if (AllBlocks.BELT.has(placedOnState)) {
            return placedOnState.getValue(BeltBlock.SLOPE) == BeltSlope.HORIZONTAL;
        }
        return BasinBlock.isBasin(world, pos) || AllBlocks.DEPOT.has(placedOnState) || AllBlocks.WEIGHTED_EJECTOR.has(placedOnState);
    }
}
