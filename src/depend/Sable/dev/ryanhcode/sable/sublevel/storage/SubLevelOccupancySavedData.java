/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.datafix.DataFixTypes
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraft.world.level.saveddata.SavedData$Factory
 */
package dev.ryanhcode.sable.sublevel.storage;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import java.util.BitSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

public class SubLevelOccupancySavedData
extends SavedData {
    public static final String FILE_ID = "sable_sub_level_occupancy";
    private final ServerLevel level;

    private SubLevelOccupancySavedData(ServerLevel level) {
        this.level = level;
    }

    public static SubLevelOccupancySavedData getOrLoad(ServerLevel level) {
        return (SubLevelOccupancySavedData)level.getChunkSource().getDataStorage().computeIfAbsent(new SavedData.Factory(() -> new SubLevelOccupancySavedData(level), (tag, provider) -> SubLevelOccupancySavedData.load(level, tag), DataFixTypes.LEVEL), FILE_ID);
    }

    private static SubLevelOccupancySavedData load(ServerLevel level, CompoundTag tag) {
        SubLevelOccupancySavedData data = new SubLevelOccupancySavedData(level);
        long[] longArray = tag.getLongArray("sub_level_occupancy");
        if (longArray.length > 0) {
            BitSet occupancyData = BitSet.valueOf(longArray);
            ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
            assert (container != null) : "Sub-level container is null";
            BitSet occupancy = container.getOccupancy();
            occupancy.clear();
            occupancy.or(occupancyData);
        }
        return data;
    }

    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        assert (container != null) : "Sub-level container is null";
        BitSet occupancy = container.getOccupancy();
        long[] longArray = occupancy.toLongArray();
        compoundTag.putLongArray("sub_level_occupancy", longArray);
        return compoundTag;
    }
}
