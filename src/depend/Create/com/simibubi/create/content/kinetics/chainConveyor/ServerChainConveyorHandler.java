/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.player.Player
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.simibubi.create.content.kinetics.chainConveyor.ClientboundChainConveyorRidingPacket;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class ServerChainConveyorHandler {
    public static Object2IntMap<UUID> hangingPlayers = new Object2IntOpenHashMap();
    public static int ticks;

    public static void handleTTLPacket(Player player) {
        int count = hangingPlayers.size();
        hangingPlayers.put((Object)player.getUUID(), 20);
        if (hangingPlayers.size() != count) {
            ServerChainConveyorHandler.sync();
        }
    }

    public static void handleStopRidingPacket(Player player) {
        if (hangingPlayers.removeInt((Object)player.getUUID()) != 0) {
            ServerChainConveyorHandler.sync();
        }
    }

    public static void tick() {
        ++ticks;
        int before = hangingPlayers.size();
        ObjectIterator iterator = hangingPlayers.object2IntEntrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            int newTTL = (Integer)entry.getValue() - 1;
            if (newTTL <= 0) {
                iterator.remove();
                continue;
            }
            entry.setValue(newTTL);
        }
        int after = hangingPlayers.size();
        if (ticks % 10 != 0 && before == after) {
            return;
        }
        ServerChainConveyorHandler.sync();
    }

    public static void sync() {
        CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)new ClientboundChainConveyorRidingPacket((Collection<UUID>)hangingPlayers.keySet()));
    }
}
