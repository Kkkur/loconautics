/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 */
package com.simibubi.create.compat.trainmap;

import com.simibubi.create.compat.trainmap.TrainMapSync;
import com.simibubi.create.compat.trainmap.TrainMapSyncPacket;
import com.simibubi.create.compat.trainmap.TrainMapSyncRequestPacket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class TrainMapSyncClient {
    public static Map<UUID, TrainMapSync.TrainMapSyncEntry> currentData = new HashMap<UUID, TrainMapSync.TrainMapSyncEntry>();
    public static double lastPacket;
    private static int ticks;

    public static void requestData() {
        if (++ticks % 5 == 0) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)TrainMapSyncRequestPacket.INSTANCE);
        }
    }

    public static void stopRequesting() {
        ticks = 0;
        currentData.clear();
    }

    public static void receive(TrainMapSyncPacket packet) {
        if (ticks == 0) {
            return;
        }
        lastPacket = AnimationTickHolder.getTicks();
        lastPacket += (double)AnimationTickHolder.getPartialTicks();
        HashSet<UUID> staleEntries = new HashSet<UUID>(currentData.keySet());
        for (Pair<UUID, TrainMapSync.TrainMapSyncEntry> pair : packet.entries) {
            UUID id = (UUID)pair.getFirst();
            TrainMapSync.TrainMapSyncEntry entry = (TrainMapSync.TrainMapSyncEntry)pair.getSecond();
            staleEntries.remove(id);
            currentData.computeIfAbsent(id, $ -> entry).updateFrom(entry, packet.light);
        }
        for (UUID uuid : staleEntries) {
            currentData.remove(uuid);
        }
    }
}
