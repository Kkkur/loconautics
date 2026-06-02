/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class StockKeeperCategoryHidingPacket
extends BlockEntityConfigurationPacket<StockTickerBlockEntity> {
    public static final StreamCodec<ByteBuf, StockKeeperCategoryHidingPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, i -> i.pos, (StreamCodec)CatnipStreamCodecBuilders.list((StreamCodec)ByteBufCodecs.INT), i -> i.indices, StockKeeperCategoryHidingPacket::new);
    private final List<Integer> indices;

    public StockKeeperCategoryHidingPacket(BlockPos pos, List<Integer> indices) {
        super(pos);
        this.indices = indices;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.STOCK_KEEPER_HIDE_CATEGORY;
    }

    @Override
    protected void applySettings(ServerPlayer player, StockTickerBlockEntity be) {
        if (this.indices.isEmpty()) {
            be.hiddenCategoriesByPlayer.remove(player.getUUID());
        } else {
            be.hiddenCategoriesByPlayer.put(player.getUUID(), this.indices);
            be.notifyUpdate();
        }
    }
}
