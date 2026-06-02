/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record ContraptionStallPacket(int entityId, double x, double y, double z, float angle) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ContraptionStallPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, ContraptionStallPacket::entityId, (StreamCodec)ByteBufCodecs.DOUBLE, ContraptionStallPacket::x, (StreamCodec)ByteBufCodecs.DOUBLE, ContraptionStallPacket::y, (StreamCodec)ByteBufCodecs.DOUBLE, ContraptionStallPacket::z, (StreamCodec)ByteBufCodecs.FLOAT, ContraptionStallPacket::angle, ContraptionStallPacket::new);

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        AbstractContraptionEntity.handleStallPacket(this);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONTRAPTION_STALL;
    }
}
