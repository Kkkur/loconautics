/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.compat.trainmap;

import com.simibubi.create.AllPackets;
import com.simibubi.create.compat.trainmap.TrainMapSync;
import com.simibubi.create.compat.trainmap.TrainMapSyncClient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TrainMapSyncPacket
implements ClientboundPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, TrainMapSyncPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.BOOL, packet -> packet.light, (StreamCodec)CatnipStreamCodecBuilders.list((StreamCodec)Pair.streamCodec((StreamCodec)UUIDUtil.STREAM_CODEC, TrainMapSync.TrainMapSyncEntry.STREAM_CODEC)), packet -> packet.entries, TrainMapSyncPacket::new);
    public boolean light;
    public List<Pair<UUID, TrainMapSync.TrainMapSyncEntry>> entries = new ArrayList<Pair<UUID, TrainMapSync.TrainMapSyncEntry>>();

    public TrainMapSyncPacket(boolean light) {
        this.light = light;
    }

    public TrainMapSyncPacket(boolean light, List<Pair<UUID, TrainMapSync.TrainMapSyncEntry>> entries) {
        this.light = light;
        this.entries = entries;
    }

    public void add(UUID trainId, TrainMapSync.TrainMapSyncEntry data) {
        this.entries.add((Pair<UUID, TrainMapSync.TrainMapSyncEntry>)Pair.of((Object)trainId, (Object)data));
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        TrainMapSyncClient.receive(this);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.TRAIN_MAP_SYNC;
    }
}
