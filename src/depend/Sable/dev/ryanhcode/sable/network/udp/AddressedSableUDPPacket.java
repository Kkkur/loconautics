/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.network.udp;

import dev.ryanhcode.sable.network.udp.SableUDPPacket;
import java.net.InetSocketAddress;

public record AddressedSableUDPPacket(SableUDPPacket packet, InetSocketAddress address) {
}
