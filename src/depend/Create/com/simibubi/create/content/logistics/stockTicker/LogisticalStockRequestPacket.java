/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.stockTicker.StockCheckingBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class LogisticalStockRequestPacket
extends BlockEntityConfigurationPacket<StockCheckingBlockEntity> {
    public static final StreamCodec<ByteBuf, LogisticalStockRequestPacket> STREAM_CODEC = BlockPos.STREAM_CODEC.map(LogisticalStockRequestPacket::new, packet -> packet.pos);

    public LogisticalStockRequestPacket(BlockPos pos) {
        super(pos);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.LOGISTICS_STOCK_REQUEST;
    }

    @Override
    protected void applySettings(ServerPlayer player, StockCheckingBlockEntity be) {
        be.getRecentSummary().divideAndSendTo(player, this.pos);
    }

    @Override
    protected int maxRange() {
        return 4096;
    }
}
