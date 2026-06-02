/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.ContraptionColliderLockPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public record ContraptionColliderLockPacket.ContraptionColliderLockPacketRequest(int contraption, double offset) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ContraptionColliderLockPacket.ContraptionColliderLockPacketRequest> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, ContraptionColliderLockPacket.ContraptionColliderLockPacketRequest::contraption, (StreamCodec)ByteBufCodecs.DOUBLE, ContraptionColliderLockPacket.ContraptionColliderLockPacketRequest::offset, ContraptionColliderLockPacket.ContraptionColliderLockPacketRequest::new);

    public void handle(ServerPlayer player) {
        CatnipServices.NETWORK.sendToClientsTrackingEntity((Entity)player, (CustomPacketPayload)new ContraptionColliderLockPacket(this.contraption, this.offset, player.getId()));
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONTRAPTION_COLLIDER_LOCK_REQUEST;
    }
}
