/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.common.util.TriState
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.simibubi.create.content.logistics.funnel;

import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class FunnelItem
extends BlockItem {
    public FunnelItem(Block p_i48527_1_, Item.Properties p_i48527_2_) {
        super(p_i48527_1_, p_i48527_2_);
    }

    @SubscribeEvent
    public static void funnelItemAlwaysPlacesWhenUsed(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().getItem() instanceof FunnelItem) {
            event.setUseBlock(TriState.FALSE);
        }
    }

    protected BlockState getPlacementState(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = super.getPlacementState(ctx);
        if (state == null) {
            return state;
        }
        if (!(state.getBlock() instanceof FunnelBlock)) {
            return state;
        }
        if (((Direction)state.getValue((Property)FunnelBlock.FACING)).getAxis().isVertical()) {
            return state;
        }
        Direction direction = (Direction)state.getValue((Property)FunnelBlock.FACING);
        FunnelBlock block = (FunnelBlock)this.getBlock();
        Block beltFunnelBlock = block.getEquivalentBeltFunnel((BlockGetter)world, pos, state).getBlock();
        BlockState equivalentBeltFunnel = (BlockState)beltFunnelBlock.getStateForPlacement(ctx).setValue((Property)BeltFunnelBlock.HORIZONTAL_FACING, (Comparable)direction);
        if (BeltFunnelBlock.isOnValidBelt(equivalentBeltFunnel, (LevelReader)world, pos)) {
            return equivalentBeltFunnel;
        }
        return state;
    }
}
