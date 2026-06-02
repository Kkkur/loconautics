/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.VeilPacketManager
 */
package dev.ryanhcode.offroad.network;

import dev.ryanhcode.offroad.network.borehead_bearing.ClientboundMultiMiningSync;
import foundry.veil.api.network.VeilPacketManager;

public class OffroadPacketManager {
    public static VeilPacketManager INSTANCE = VeilPacketManager.create((String)"offroad", (String)"0.1");

    public static void init() {
        INSTANCE.registerClientbound(ClientboundMultiMiningSync.TYPE, ClientboundMultiMiningSync.CODEC, ClientboundMultiMiningSync::handle);
    }
}
