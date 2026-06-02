/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 */
package dev.ryanhcode.sable.network.udp;

import dev.ryanhcode.sable.network.packets.ClientboundSableSnapshotDualPacket;
import dev.ryanhcode.sable.network.packets.ClientboundSableSnapshotInfoDualPacket;
import dev.ryanhcode.sable.network.packets.udp.SableUDPAuthenticationPacket;
import dev.ryanhcode.sable.network.packets.udp.SableUDPClientboundKeepAlivePacket;
import dev.ryanhcode.sable.network.packets.udp.SableUDPEchoPacket;
import dev.ryanhcode.sable.network.packets.udp.SableUDPServerboundAlivePacket;
import dev.ryanhcode.sable.network.udp.SableUDPPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum SableUDPPacketType {
    PING(SableUDPEchoPacket.CODEC),
    SNAPSHOT(ClientboundSableSnapshotDualPacket.CODEC),
    SNAPSHOT_INFO(ClientboundSableSnapshotInfoDualPacket.CODEC),
    AUTH(SableUDPAuthenticationPacket.CODEC),
    KEEP_ALIVE_CLIENTBOUND(SableUDPClientboundKeepAlivePacket.CODEC),
    ALIVE_SERVERBOUND(SableUDPServerboundAlivePacket.CODEC);

    public static final SableUDPPacketType[] VALUES;
    private final StreamCodec<RegistryFriendlyByteBuf, ? extends SableUDPPacket> codec;

    private SableUDPPacketType(StreamCodec<RegistryFriendlyByteBuf, ? extends SableUDPPacket> codec) {
        this.codec = codec;
    }

    public SableUDPPacket create(RegistryFriendlyByteBuf buf) {
        return (SableUDPPacket)this.codec.decode((Object)buf);
    }

    public void write(RegistryFriendlyByteBuf buf, SableUDPPacket packet) {
        this.codec.encode((Object)buf, (Object)packet);
    }

    static {
        VALUES = SableUDPPacketType.values();
    }
}
