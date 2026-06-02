/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
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
package com.simibubi.create.content.trains.signal;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllPackets;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.signal.EdgeGroupColor;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record SignalEdgeGroupPacket(List<UUID> ids, List<EdgeGroupColor> colors, boolean add) implements ClientboundPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, SignalEdgeGroupPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecBuilders.list((StreamCodec)UUIDUtil.STREAM_CODEC), p -> p.ids, (StreamCodec)CatnipStreamCodecBuilders.list(EdgeGroupColor.STREAM_CODEC), p -> p.colors, (StreamCodec)ByteBufCodecs.BOOL, p -> p.add, SignalEdgeGroupPacket::new);

    public SignalEdgeGroupPacket(UUID id, EdgeGroupColor color) {
        this((List<UUID>)ImmutableList.of((Object)id), (List<EdgeGroupColor>)ImmutableList.of((Object)((Object)color)), true);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        Map<UUID, SignalEdgeGroup> signalEdgeGroups = CreateClient.RAILWAYS.signalEdgeGroups;
        for (int i = 0; i < this.ids.size(); ++i) {
            UUID id = this.ids.get(i);
            if (!this.add) {
                signalEdgeGroups.remove(id);
                continue;
            }
            SignalEdgeGroup group = new SignalEdgeGroup(id);
            signalEdgeGroups.put(id, group);
            if (i >= this.colors.size()) continue;
            group.color = this.colors.get(i);
        }
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.SYNC_EDGE_GROUP;
    }
}
