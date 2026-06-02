/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraft.world.level.saveddata.SavedData$Factory
 */
package dev.eriksonn.aeronautics.api.levitite_blend_crystallization;

import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeCrystallizerManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public class CrystallizationWorldSaveData
extends SavedData {
    public static final String ID = "aeronautics_levitite_data";
    Level level;

    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag list = new ListTag();
        LevititeCrystallizerManager.saveData(list, this.level);
        tag.put("Levitite Manager Data", (Tag)list);
        return tag;
    }

    public static CrystallizationWorldSaveData load(ServerLevel level, CompoundTag tag, HolderLookup.Provider registries) {
        CrystallizationWorldSaveData data = new CrystallizationWorldSaveData();
        data.level = level;
        LevititeCrystallizerManager.loadData(tag, (Level)level);
        return data;
    }

    public static CrystallizationWorldSaveData get(ServerLevel level) {
        CrystallizationWorldSaveData data = (CrystallizationWorldSaveData)level.getChunkSource().getDataStorage().computeIfAbsent(new SavedData.Factory(CrystallizationWorldSaveData::new, (nbt, lookup) -> CrystallizationWorldSaveData.load(level, nbt, lookup), null), ID);
        data.level = level;
        return data;
    }
}
