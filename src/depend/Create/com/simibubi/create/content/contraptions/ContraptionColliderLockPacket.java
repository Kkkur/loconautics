/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record ContraptionColliderLockPacket(int contraption, double offset, int sender) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ContraptionColliderLockPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, ContraptionColliderLockPacket::contraption, (StreamCodec)ByteBufCodecs.DOUBLE, ContraptionColliderLockPacket::offset, (StreamCodec)ByteBufCodecs.VAR_INT, ContraptionColliderLockPacket::sender, ContraptionColliderLockPacket::new);

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        ContraptionCollider.lockPacketReceived(this.contraption, this.sender, this.offset);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONTRAPTION_COLLIDER_LOCK;
    }

    public record ContraptionColliderLockPacketRequest(int contraption, double offset) implements ServerboundPacketPayload
    {
        public static final StreamCodec<ByteBuf, ContraptionColliderLockPacketRequest> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, ContraptionColliderLockPacketRequest::contraption, (StreamCodec)ByteBufCodecs.DOUBLE, ContraptionColliderLockPacketRequest::offset, ContraptionColliderLockPacketRequest::new);

        public void handle(ServerPlayer player) {
            CatnipServices.NETWORK.sendToClientsTrackingEntity((Entity)player, (CustomPacketPayload)new ContraptionColliderLockPacket(this.contraption, this.offset, player.getId()));
        }

        public BasePacketPayload.PacketTypeProvider getTypeProvider() {
            return AllPackets.CONTRAPTION_COLLIDER_LOCK_REQUEST;
        }
    }
}
