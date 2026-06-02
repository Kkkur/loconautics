/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 */
package dev.ryanhcode.sable.mixinterface.udp;

import io.netty.channel.Channel;

public interface ConnectionExtension {
    public void sable$setUDPChannel(Channel var1);

    public Channel sable$getUDPChannel();
}
