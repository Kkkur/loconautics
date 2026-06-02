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
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.contraptions.actors.roller;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RollerBlockItem
extends BlockItem {
    public RollerBlockItem(Block pBlock, Item.Properties pProperties) {
        super(pBlock, pProperties);
    }

    public InteractionResult place(BlockPlaceContext ctx) {
        BlockPos clickedPos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        BlockState blockStateBelow = level.getBlockState(clickedPos.below());
        if (!Block.isFaceFull((VoxelShape)blockStateBelow.getCollisionShape((BlockGetter)level, clickedPos.below()), (Direction)Direction.UP)) {
            return super.place(ctx);
        }
        Direction clickedFace = ctx.getClickedFace();
        return super.place(BlockPlaceContext.at((BlockPlaceContext)ctx, (BlockPos)clickedPos.relative(Direction.UP), (Direction)clickedFace));
    }
}
