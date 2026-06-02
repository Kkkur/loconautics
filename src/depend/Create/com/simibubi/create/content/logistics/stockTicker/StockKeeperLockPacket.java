/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class StockKeeperLockPacket
extends BlockEntityConfigurationPacket<StockTickerBlockEntity> {
    public static final StreamCodec<ByteBuf, StockKeeperLockPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, p -> p.pos, (StreamCodec)ByteBufCodecs.BOOL, p -> p.lock, StockKeeperLockPacket::new);
    private final boolean lock;

    public StockKeeperLockPacket(BlockPos pos, boolean lock) {
        super(pos);
        this.lock = lock;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.LOCK_STOCK_KEEPER;
    }

    @Override
    protected void applySettings(ServerPlayer player, StockTickerBlockEntity be) {
        if (!be.behaviour.mayAdministrate((Player)player)) {
            return;
        }
        LogisticsNetwork network = Create.LOGISTICS.logisticsNetworks.get(be.behaviour.freqId);
        if (network != null) {
            network.locked = this.lock;
            Create.LOGISTICS.markDirty();
        }
    }
}
