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

class ServerConnectionListenerMixin.2
extends ChannelInitializer<Channel> {
    ServerConnectionListenerMixin.2() {
    }

    protected void initChannel(Channel channel) {
        SableUDPPacket.configureInMemoryPipeline(channel.pipeline(), PacketFlow.SERVERBOUND);
        ServerConnectionListenerMixin.this.sable$setupChannel(channel);
    }
}
