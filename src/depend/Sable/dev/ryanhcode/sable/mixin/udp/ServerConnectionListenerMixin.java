/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.bootstrap.Bootstrap
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.ChannelPipeline
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.epoll.Epoll
 *  io.netty.channel.epoll.EpollDatagramChannel
 *  io.netty.channel.local.LocalAddress
 *  io.netty.channel.local.LocalServerChannel
 *  io.netty.channel.socket.nio.NioDatagramChannel
 *  net.minecraft.network.protocol.PacketFlow
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.network.ServerConnectionListener
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.udp;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.SableConfig;
import dev.ryanhcode.sable.mixinterface.udp.ServerConnectionListenerExtension;
import dev.ryanhcode.sable.network.udp.SableUDPPacket;
import dev.ryanhcode.sable.network.udp.SableUDPServer;
import dev.ryanhcode.sable.network.udp.handler.SableUDPChannelHandlerServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.List;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConnectionListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ServerConnectionListener.class})
public class ServerConnectionListenerMixin
implements ServerConnectionListenerExtension {
    @Shadow
    @Final
    private List<ChannelFuture> channels;
    @Shadow
    @Final
    private MinecraftServer server;
    @Unique
    private SableUDPServer sable$server = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Inject(method={"startTcpServerListener"}, at={@At(value="HEAD")})
    private void sable$startTcpServerListener(InetAddress inetAddress, int port, CallbackInfo ci) {
        if (((Boolean)SableConfig.DISABLE_UDP_PIPELINE.get()).booleanValue()) {
            return;
        }
        List<ChannelFuture> list = this.channels;
        synchronized (list) {
            EventLoopGroup eventLoopGroup;
            Class<NioDatagramChannel> channelClass;
            if (Epoll.isAvailable() && this.server.isEpollEnabled()) {
                channelClass = EpollDatagramChannel.class;
                eventLoopGroup = (EventLoopGroup)ServerConnectionListener.SERVER_EPOLL_EVENT_GROUP.get();
            } else {
                channelClass = NioDatagramChannel.class;
                eventLoopGroup = (EventLoopGroup)ServerConnectionListener.SERVER_EVENT_GROUP.get();
            }
            Sable.LOGGER.info("Adding UDP server channel future");
            this.channels.add(((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().channel(channelClass)).option(ChannelOption.SO_BROADCAST, (Object)true)).handler((ChannelHandler)new ChannelInitializer<Channel>(){

                protected void initChannel(Channel channel) {
                    SableUDPPacket.configureSerialization(channel.pipeline(), PacketFlow.SERVERBOUND, false, null);
                    ServerConnectionListenerMixin.this.sable$setupChannel(channel);
                }
            })).group(eventLoopGroup)).localAddress(inetAddress, port)).bind().syncUninterruptibly());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Inject(method={"startMemoryChannel"}, at={@At(value="TAIL")})
    private void sable$startMemoryChannel(CallbackInfoReturnable<SocketAddress> cir) {
        if (((Boolean)SableConfig.DISABLE_UDP_PIPELINE.get()).booleanValue()) {
            return;
        }
        List<ChannelFuture> list = this.channels;
        synchronized (list) {
            Sable.LOGGER.info("Adding local UDP server channel future");
            this.channels.add(((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().channel(LocalServerChannel.class)).option(ChannelOption.SO_BROADCAST, (Object)true)).handler((ChannelHandler)new ChannelInitializer<Channel>(){

                protected void initChannel(Channel channel) {
                    SableUDPPacket.configureInMemoryPipeline(channel.pipeline(), PacketFlow.SERVERBOUND);
                    ServerConnectionListenerMixin.this.sable$setupChannel(channel);
                }
            })).group((EventLoopGroup)ServerConnectionListener.SERVER_EVENT_GROUP.get())).localAddress((SocketAddress)LocalAddress.ANY)).bind().syncUninterruptibly());
        }
    }

    @Unique
    private void sable$setupChannel(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new ChannelHandler[]{new SableUDPChannelHandlerServer(this.server, (ServerConnectionListener)this)});
    }

    @Inject(method={"stop"}, at={@At(value="TAIL")})
    private void sable$stop(CallbackInfo ci) {
        this.sable$server = null;
    }

    @Override
    public void sable$setupUDPServer(Channel channel) {
        this.sable$server = new SableUDPServer(this.server, channel);
    }

    @Override
    @Nullable
    public SableUDPServer sable$getServer() {
        return this.sable$server;
    }
}
