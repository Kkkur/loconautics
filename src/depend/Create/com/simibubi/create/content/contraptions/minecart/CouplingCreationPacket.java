/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 */
package com.simibubi.create.content.contraptions.minecart;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.minecart.CouplingHandler;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public record CouplingCreationPacket(int id1, int id2) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, CouplingCreationPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, CouplingCreationPacket::id1, (StreamCodec)ByteBufCodecs.VAR_INT, CouplingCreationPacket::id2, CouplingCreationPacket::new);

    public CouplingCreationPacket(AbstractMinecart cart1, AbstractMinecart cart2) {
        this(cart1.getId(), cart2.getId());
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.MINECART_COUPLING_CREATION;
    }

    public void handle(ServerPlayer player) {
        CouplingHandler.tryToCoupleCarts((Player)player, player.level(), this.id1, this.id2);
    }
}
