/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.logistics.tunnel;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityDataPacket;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class TunnelFlapPacket
extends BlockEntityDataPacket<BeltTunnelBlockEntity> {
    public static final StreamCodec<ByteBuf, TunnelFlapPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.pos, (StreamCodec)CatnipStreamCodecBuilders.list((StreamCodec)CatnipStreamCodecBuilders.pair((StreamCodec)Direction.STREAM_CODEC, (StreamCodec)ByteBufCodecs.BOOL)), packet -> packet.flaps, TunnelFlapPacket::new);
    private final List<Pair<Direction, Boolean>> flaps;

    public TunnelFlapPacket(BeltTunnelBlockEntity blockEntity, List<Pair<Direction, Boolean>> flaps) {
        this(blockEntity.getBlockPos(), new ArrayList<Pair<Direction, Boolean>>(flaps));
    }

    private TunnelFlapPacket(BlockPos pos, List<Pair<Direction, Boolean>> flaps) {
        super(pos);
        this.flaps = flaps;
    }

    @Override
    protected void handlePacket(BeltTunnelBlockEntity blockEntity) {
        for (Pair<Direction, Boolean> flap : this.flaps) {
            blockEntity.flap((Direction)flap.getFirst(), (Boolean)flap.getSecond());
        }
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.TUNNEL_FLAP;
    }
}
