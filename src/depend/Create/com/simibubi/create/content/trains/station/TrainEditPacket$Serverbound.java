/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 */
package com.simibubi.create.content.trains.station;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.trains.station.TrainEditPacket;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public static class TrainEditPacket.Serverbound
extends TrainEditPacket
implements ServerboundPacketPayload {
    public static final StreamCodec<ByteBuf, TrainEditPacket.Serverbound> STREAM_CODEC = TrainEditPacket.codec(TrainEditPacket.Serverbound::new);

    public TrainEditPacket.Serverbound(UUID id, String name, ResourceLocation iconType, int mapColor) {
        super(id, name, iconType, mapColor);
    }

    public void handle(ServerPlayer sender) {
        this.handleSided((Player)sender);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.C_CONFIGURE_TRAIN;
    }
}
