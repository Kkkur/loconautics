/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.PacketContext
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.util.concurrent.GenericFutureListener
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.Connection
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 */
package dev.ryanhcode.sable.network.packets.tcp;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.mixinterface.udp.ConnectionExtension;
import dev.ryanhcode.sable.network.packets.udp.SableUDPAuthenticationPacket;
import dev.ryanhcode.sable.network.tcp.SableTCPPacket;
import dev.ryanhcode.sable.network.udp.AddressedSableUDPPacket;
import foundry.veil.api.network.handler.PacketContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundSableUDPActivationPacket(UUID uuid) implements SableTCPPacket
{
    public static final CustomPacketPayload.Type<ClientboundSableUDPActivationPacket> TYPE = new CustomPacketPayload.Type(Sable.sablePath("udp_activation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSableUDPActivationPacket> CODEC = StreamCodec.of((buf, value) -> value.write((FriendlyByteBuf)buf), ClientboundSableUDPActivationPacket::read);

    private void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.uuid);
    }

    private static ClientboundSableUDPActivationPacket read(FriendlyByteBuf buf) {
        return new ClientboundSableUDPActivationPacket(buf.readUUID());
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(PacketContext context) {
        Connection connection = Minecraft.getInstance().getConnection().getConnection();
        ConnectionExtension connectionExtension = (ConnectionExtension)connection;
        Channel channel = connectionExtension.sable$getUDPChannel();
        InetSocketAddress baseAddress = (InetSocketAddress)connection.getRemoteAddress();
        InetSocketAddress remoteAddress = new InetSocketAddress(baseAddress.getAddress(), baseAddress.getPort());
        Sable.LOGGER.info("Received authentication request, sending response over UDP to {}", (Object)remoteAddress);
        channel.eventLoop().execute(() -> {
            SableUDPAuthenticationPacket packet = new SableUDPAuthenticationPacket(this.uuid.toString());
            AddressedSableUDPPacket envelope = new AddressedSableUDPPacket(packet, remoteAddress);
            ChannelFuture writeFuture = channel.writeAndFlush((Object)envelope);
            writeFuture.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        });
    }
}
