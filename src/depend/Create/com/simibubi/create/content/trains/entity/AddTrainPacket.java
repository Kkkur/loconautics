/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.AllPackets;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.entity.Train;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record AddTrainPacket(Train train) implements ClientboundPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, AddTrainPacket> STREAM_CODEC = Train.STREAM_CODEC.map(AddTrainPacket::new, AddTrainPacket::train);

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        CreateClient.RAILWAYS.trains.put(this.train.id, this.train);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.ADD_TRAIN;
    }
}
