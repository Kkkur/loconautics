/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.gearbox;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.IRotate;
import java.util.Map;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class VerticalGearboxItem
extends BlockItem {
    public VerticalGearboxItem(Item.Properties builder) {
        super((Block)AllBlocks.GEARBOX.get(), builder);
    }

    public String getDescriptionId() {
        return "item.create.vertical_gearbox";
    }

    public void registerBlocks(Map<Block, Item> p_195946_1_, Item p_195946_2_) {
    }

    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, Player player, ItemStack stack, BlockState state) {
        Direction.Axis prefferedAxis = null;
        for (Direction side : Iterate.horizontalDirections) {
            BlockState blockState = world.getBlockState(pos.relative(side));
            if (!(blockState.getBlock() instanceof IRotate) || !((IRotate)blockState.getBlock()).hasShaftTowards((LevelReader)world, pos.relative(side), blockState, side.getOpposite())) continue;
            if (prefferedAxis != null && prefferedAxis != side.getAxis()) {
                prefferedAxis = null;
                break;
            }
            prefferedAxis = side.getAxis();
        }
        Direction.Axis axis = prefferedAxis == null ? player.getDirection().getClockWise().getAxis() : (prefferedAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
        world.setBlockAndUpdate(pos, (BlockState)state.setValue((Property)BlockStateProperties.AXIS, (Comparable)axis));
        return super.updateCustomBlockEntityTag(pos, world, player, stack, state);
    }
}
