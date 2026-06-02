/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.shapes.CollisionContext
 */
package com.simibubi.create.content.logistics.tunnel;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class BeltTunnelItem
extends BlockItem {
    public BeltTunnelItem(Block p_i48527_1_, Item.Properties p_i48527_2_) {
        super(p_i48527_1_, p_i48527_2_);
    }

    protected boolean canPlace(BlockPlaceContext ctx, BlockState state) {
        Player playerentity = ctx.getPlayer();
        CollisionContext iselectioncontext = playerentity == null ? CollisionContext.empty() : CollisionContext.of((Entity)playerentity);
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        return (!this.mustSurvive() || ((BeltTunnelBlock)AllBlocks.ANDESITE_TUNNEL.get()).isValidPositionForPlacement(state, (LevelReader)world, pos)) && world.isUnobstructed(state, pos, iselectioncontext);
    }

    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, Player p_195943_3_, ItemStack p_195943_4_, BlockState state) {
        BeltBlockEntity belt;
        boolean flag = super.updateCustomBlockEntityTag(pos, world, p_195943_3_, p_195943_4_, state);
        if (!world.isClientSide && (belt = BeltHelper.getSegmentBE((LevelAccessor)world, pos.below())) != null && belt.casing == BeltBlockEntity.CasingType.NONE) {
            belt.setCasingType(AllBlocks.ANDESITE_TUNNEL.has(state) ? BeltBlockEntity.CasingType.ANDESITE : BeltBlockEntity.CasingType.BRASS);
        }
        return flag;
    }
}
