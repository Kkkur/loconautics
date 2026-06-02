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
package dev.simulated_team.simulated.content.blocks.handle;

import dev.simulated_team.simulated.network.packets.handle.ClientboundPlayersHoldingHandlePacket;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class ServerHandleHoldingHandler {
    public static Object2IntMap<UUID> holdingPlayers = new Object2IntOpenHashMap();
    public static int ticks;

    public static void startHolding(Player player) {
        int count = holdingPlayers.size();
        holdingPlayers.put((Object)player.getUUID(), 20);
        if (holdingPlayers.size() != count) {
            ServerHandleHoldingHandler.sync();
        }
    }

    public static void stopHolding(Player player) {
        if (holdingPlayers.removeInt((Object)player.getUUID()) != 0) {
            ServerHandleHoldingHandler.sync();
        }
    }

    public static void tick() {
        ++ticks;
        int before = holdingPlayers.size();
        ObjectIterator iterator = holdingPlayers.object2IntEntrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            int newTTL = (Integer)entry.getValue() - 1;
            if (newTTL <= 0) {
                iterator.remove();
                continue;
            }
            entry.setValue(newTTL);
        }
        int after = holdingPlayers.size();
        if (ticks % 10 != 0 && before == after) {
            return;
        }
        ServerHandleHoldingHandler.sync();
    }

    public static void sync() {
        CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)new ClientboundPlayersHoldingHandlePacket((Collection<UUID>)holdingPlayers.keySet()));
    }
}
