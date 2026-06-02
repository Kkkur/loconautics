/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class StockKeeperCategoryRefundPacket
extends BlockEntityConfigurationPacket<StockTickerBlockEntity> {
    public static final StreamCodec<RegistryFriendlyByteBuf, StockKeeperCategoryRefundPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, p -> p.pos, (StreamCodec)ItemStack.STREAM_CODEC, p -> p.filter, StockKeeperCategoryRefundPacket::new);
    private final ItemStack filter;

    public StockKeeperCategoryRefundPacket(BlockPos pos, ItemStack filter) {
        super(pos);
        this.filter = filter;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.REFUND_STOCK_KEEPER_CATEGORY;
    }

    @Override
    protected void applySettings(ServerPlayer player, StockTickerBlockEntity be) {
        if (!this.filter.isEmpty() && this.filter.getItem() instanceof FilterItem) {
            player.getInventory().placeItemBackInInventory(this.filter);
        }
    }
}
