/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  net.minecraft.network.Connection
 *  net.minecraft.network.protocol.PacketFlow
 */
package dev.ryanhcode.sable.mixin.udp;

import dev.ryanhcode.sable.mixin.udp.ConnectionMixin;
import dev.ryanhcode.sable.network.udp.SableUDPPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;

static class ConnectionMixin.1
extends ChannelInitializer<Channel> {
    final /* synthetic */ Connection val$connection;

    ConnectionMixin.1(Connection connection) {
        this.val$connection = connection;
    }

    protected void initChannel(Channel channel) {
        channel.config().setOption(ChannelOption.SO_KEEPALIVE, (Object)true);
        SableUDPPacket.configureSerialization(channel.pipeline(), PacketFlow.CLIENTBOUND, false, null);
        ConnectionMixin.sable$setupChannel(channel, this.val$connection);
    }
}
