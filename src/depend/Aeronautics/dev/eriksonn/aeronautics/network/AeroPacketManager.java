/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.VeilPacketManager
 */
package dev.eriksonn.aeronautics.network;

import dev.eriksonn.aeronautics.network.packets.LevititeCatalystCrystallizationPacket;
import foundry.veil.api.network.VeilPacketManager;

public class AeroPacketManager {
    public static VeilPacketManager INSTANCE = VeilPacketManager.create((String)"aeronautics", (String)"0.1");

    public static void init() {
        INSTANCE.registerServerbound(LevititeCatalystCrystallizationPacket.TYPE, LevititeCatalystCrystallizationPacket.STREAM_CODEC, LevititeCatalystCrystallizationPacket::handle);
    }
}
