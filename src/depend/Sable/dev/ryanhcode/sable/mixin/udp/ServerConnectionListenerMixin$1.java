/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelInitializer
 *  net.minecraft.network.protocol.PacketFlow
 */
package dev.ryanhcode.sable.mixin.udp;

import dev.ryanhcode.sable.network.udp.SableUDPPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import net.minecraft.network.protocol.PacketFlow;

class ServerConnectionListenerMixin.1
extends ChannelInitializer<Channel> {
    ServerConnectionListenerMixin.1() {
    }

    protected void initChannel(Channel channel) {
        SableUDPPacket.configureSerialization(channel.pipeline(), PacketFlow.SERVERBOUND, false, null);
        ServerConnectionListenerMixin.this.sable$setupChannel(channel);
    }
}
