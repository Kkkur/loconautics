/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains.station;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.trains.station.TrainEditPacket;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public static class TrainEditPacket.TrainEditReturnPacket
extends TrainEditPacket
implements ClientboundPacketPayload {
    public static final StreamCodec<ByteBuf, TrainEditPacket.TrainEditReturnPacket> STREAM_CODEC = TrainEditPacket.codec(TrainEditPacket.TrainEditReturnPacket::new);

    public TrainEditPacket.TrainEditReturnPacket(UUID id, String name, ResourceLocation iconType, int mapColor) {
        super(id, name, iconType, mapColor);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        this.handleSided(null);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.S_CONFIGURE_TRAIN;
    }
}
