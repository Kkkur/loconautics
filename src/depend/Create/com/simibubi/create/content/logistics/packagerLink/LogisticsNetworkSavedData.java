/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraft.world.level.saveddata.SavedData$Factory
 */
package com.simibubi.create.content.logistics.packagerLink;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.GlobalLogisticsManager;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public class LogisticsNetworkSavedData
extends SavedData {
    private Map<UUID, LogisticsNetwork> logisticsNetworks = new HashMap<UUID, LogisticsNetwork>();

    public static SavedData.Factory<LogisticsNetworkSavedData> factory() {
        return new SavedData.Factory(LogisticsNetworkSavedData::new, LogisticsNetworkSavedData::load);
    }

    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
        GlobalLogisticsManager logistics = Create.LOGISTICS;
        nbt.put("LogisticsNetworks", (Tag)NBTHelper.writeCompoundList(logistics.logisticsNetworks.values(), network -> network.write(registries)));
        return nbt;
    }

    private static LogisticsNetworkSavedData load(CompoundTag nbt, HolderLookup.Provider registries) {
        LogisticsNetworkSavedData sd = new LogisticsNetworkSavedData();
        sd.logisticsNetworks = new HashMap<UUID, LogisticsNetwork>();
        NBTHelper.iterateCompoundList((ListTag)nbt.getList("LogisticsNetworks", 10), c -> {
            LogisticsNetwork network = LogisticsNetwork.read(c, registries);
            sd.logisticsNetworks.put(network.id, network);
        });
        return sd;
    }

    public Map<UUID, LogisticsNetwork> getLogisticsNetworks() {
        return this.logisticsNetworks;
    }

    private LogisticsNetworkSavedData() {
    }

    public static LogisticsNetworkSavedData load(MinecraftServer server) {
        return (LogisticsNetworkSavedData)server.overworld().getDataStorage().computeIfAbsent(LogisticsNetworkSavedData.factory(), "create_logistics");
    }
}
