/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.redstone.contact;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.elevator.ElevatorColumn;
import com.simibubi.create.content.redstone.contact.RedstoneContactBlock;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class RedstoneContactItem
extends BlockItem {
    public RedstoneContactItem(Block pBlock, Item.Properties pProperties) {
        super(pBlock, pProperties);
    }

    protected BlockState getPlacementState(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = super.getPlacementState(ctx);
        if (state == null) {
            return state;
        }
        if (!(state.getBlock() instanceof RedstoneContactBlock)) {
            return state;
        }
        Direction facing = (Direction)state.getValue((Property)RedstoneContactBlock.FACING);
        if (facing.getAxis() == Direction.Axis.Y) {
            return state;
        }
        if (ElevatorColumn.get((LevelAccessor)world, new ElevatorColumn.ColumnCoords(pos.getX(), pos.getZ(), facing)) == null) {
            return state;
        }
        return BlockHelper.copyProperties(state, AllBlocks.ELEVATOR_CONTACT.getDefaultState());
    }
}
