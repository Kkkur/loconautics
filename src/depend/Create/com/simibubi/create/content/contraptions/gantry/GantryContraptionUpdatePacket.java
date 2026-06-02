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
package com.simibubi.create.content.contraptions.gantry;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.gantry.GantryContraptionEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record GantryContraptionUpdatePacket(int entityID, double coord, double motion, double sequenceLimit) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, GantryContraptionUpdatePacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, GantryContraptionUpdatePacket::entityID, (StreamCodec)ByteBufCodecs.DOUBLE, GantryContraptionUpdatePacket::coord, (StreamCodec)ByteBufCodecs.DOUBLE, GantryContraptionUpdatePacket::motion, (StreamCodec)ByteBufCodecs.DOUBLE, GantryContraptionUpdatePacket::sequenceLimit, GantryContraptionUpdatePacket::new);

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        GantryContraptionEntity.handlePacket(this);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.GANTRY_UPDATE;
    }
}
