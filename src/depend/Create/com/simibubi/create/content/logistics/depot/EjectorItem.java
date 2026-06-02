/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.logistics.depot;

import com.simibubi.create.content.logistics.depot.EjectorPlacementPacket;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EjectorItem
extends BlockItem {
    public EjectorItem(Block p_i48527_1_, Item.Properties p_i48527_2_) {
        super(p_i48527_1_, p_i48527_2_);
    }

    public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player != null && player.isShiftKeyDown()) {
            return InteractionResult.SUCCESS;
        }
        return super.useOn(ctx);
    }

    protected BlockState getPlacementState(BlockPlaceContext p_195945_1_) {
        BlockState stateForPlacement = super.getPlacementState(p_195945_1_);
        return stateForPlacement;
    }

    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, Player player, ItemStack p_195943_4_, BlockState p_195943_5_) {
        if (!world.isClientSide && player instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)player;
            CatnipServices.NETWORK.sendToClient(sp, (CustomPacketPayload)new EjectorPlacementPacket.ClientBoundRequest(pos));
        }
        return super.updateCustomBlockEntityTag(pos, world, player, p_195943_4_, p_195943_5_);
    }

    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player p_195938_4_) {
        return !p_195938_4_.isShiftKeyDown();
    }
}
