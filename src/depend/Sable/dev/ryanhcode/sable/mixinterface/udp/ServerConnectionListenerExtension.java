/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.mixinterface.udp;

import dev.ryanhcode.sable.network.udp.SableUDPServer;
import io.netty.channel.Channel;
import org.jetbrains.annotations.Nullable;

public interface ServerConnectionListenerExtension {
    public void sable$setupUDPServer(Channel var1);

    @Nullable
    public SableUDPServer sable$getServer();
}
