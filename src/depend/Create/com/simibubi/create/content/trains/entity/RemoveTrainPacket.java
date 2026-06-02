/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.AllPackets;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.entity.Train;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record RemoveTrainPacket(UUID id) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, RemoveTrainPacket> STREAM_CODEC = UUIDUtil.STREAM_CODEC.map(RemoveTrainPacket::new, RemoveTrainPacket::id);

    public RemoveTrainPacket(Train train) {
        this(train.id);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        CreateClient.RAILWAYS.trains.remove(this.id);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.REMOVE_TRAIN;
    }
}
