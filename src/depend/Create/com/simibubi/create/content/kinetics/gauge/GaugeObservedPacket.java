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
package com.simibubi.create.content.kinetics.gauge;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class GaugeObservedPacket
extends BlockEntityConfigurationPacket<StressGaugeBlockEntity> {
    public static final StreamCodec<ByteBuf, GaugeObservedPacket> STREAM_CODEC = BlockPos.STREAM_CODEC.map(GaugeObservedPacket::new, packet -> packet.pos);

    public GaugeObservedPacket(BlockPos pos) {
        super(pos);
    }

    @Override
    protected void applySettings(ServerPlayer player, StressGaugeBlockEntity be) {
        be.onObserved();
    }

    @Override
    protected boolean causeUpdate() {
        return false;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.OBSERVER_STRESSOMETER;
    }
}
