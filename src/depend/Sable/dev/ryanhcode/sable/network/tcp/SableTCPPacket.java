/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.PacketContext
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 */
package dev.ryanhcode.sable.network.tcp;

import foundry.veil.api.network.handler.PacketContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface SableTCPPacket
extends CustomPacketPayload {
    public void handle(PacketContext var1);
}
