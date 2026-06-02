/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInboundHandler
 *  io.netty.channel.ChannelOutboundHandler
 *  io.netty.channel.ChannelPipeline
 *  io.netty.handler.flow.FlowControlHandler
 *  net.minecraft.network.BandwidthDebugMonitor
 *  net.minecraft.network.MonitorFrameDecoder
 *  net.minecraft.network.NoOpFrameDecoder
 *  net.minecraft.network.NoOpFrameEncoder
 *  net.minecraft.network.Varint21FrameDecoder
 *  net.minecraft.network.Varint21LengthFieldPrepender
 *  net.minecraft.network.protocol.PacketFlow
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.network.udp;

import dev.ryanhcode.sable.network.udp.SableUDPPacketDecoder;
import dev.ryanhcode.sable.network.udp.SableUDPPacketEncoder;
import dev.ryanhcode.sable.network.udp.SableUDPPacketType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.flow.FlowControlHandler;
import java.net.InetSocketAddress;
import net.minecraft.network.BandwidthDebugMonitor;
import net.minecraft.network.MonitorFrameDecoder;
import net.minecraft.network.NoOpFrameDecoder;
import net.minecraft.network.NoOpFrameEncoder;
import net.minecraft.network.Varint21FrameDecoder;
import net.minecraft.network.Varint21LengthFieldPrepender;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface SableUDPPacket {
    public static void configureSerialization(ChannelPipeline pipeline, PacketFlow flow, boolean memoryOnly, @Nullable BandwidthDebugMonitor debugMonitor) {
        pipeline.addLast("splitter", (ChannelHandler)SableUDPPacket.createFrameDecoder(debugMonitor, memoryOnly)).addLast(new ChannelHandler[]{new FlowControlHandler()}).addLast("decoder", (ChannelHandler)new SableUDPPacketDecoder()).addLast("prepender", (ChannelHandler)SableUDPPacket.createFrameEncoder(memoryOnly)).addLast("encoder", (ChannelHandler)new SableUDPPacketEncoder());
    }

    private static ChannelOutboundHandler createFrameEncoder(boolean memoryOnly) {
        return memoryOnly ? new NoOpFrameEncoder() : new Varint21LengthFieldPrepender();
    }

    private static ChannelInboundHandler createFrameDecoder(@Nullable BandwidthDebugMonitor debugMonitor, boolean memoryOnly) {
        if (!memoryOnly) {
            return new Varint21FrameDecoder(debugMonitor);
        }
        return debugMonitor != null ? new MonitorFrameDecoder(debugMonitor) : new NoOpFrameDecoder();
    }

    public static void configureInMemoryPipeline(ChannelPipeline channelPipeline, PacketFlow arg) {
        SableUDPPacket.configureSerialization(channelPipeline, arg, true, null);
    }

    public SableUDPPacketType getType();

    default public void handleClient(Level level) {
    }

    default public void handleServer(MinecraftServer server, InetSocketAddress sender) {
    }
}
