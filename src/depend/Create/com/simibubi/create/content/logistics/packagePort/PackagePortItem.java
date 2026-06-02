/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.logistics.packagePort;

import com.simibubi.create.content.logistics.packagePort.PackagePortPlacementPacket;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PackagePortItem
extends BlockItem {
    public PackagePortItem(Block pBlock, Item.Properties pProperties) {
        super(pBlock, pProperties);
    }

    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, Player player, ItemStack p_195943_4_, BlockState p_195943_5_) {
        if (!world.isClientSide && player instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)player;
            CatnipServices.NETWORK.sendToClient(sp, (CustomPacketPayload)new PackagePortPlacementPacket.ClientBoundRequest(pos));
        }
        return super.updateCustomBlockEntityTag(pos, world, player, p_195943_4_, p_195943_5_);
    }
}
