/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraft.world.level.saveddata.SavedData$Factory
 *  org.jetbrains.annotations.NotNull
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.Balloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.ServerBalloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map.BalloonMap;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map.SavedBalloon;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class BalloonLevelSavedData
extends SavedData {
    public static final String ID = "aeronautics_unloaded_balloons";
    public static Codec<List<SavedBalloon>> CODEC = Codec.list(SavedBalloon.CODEC);
    private Level level;

    private static BalloonLevelSavedData create(ServerLevel level, CompoundTag tag, HolderLookup.Provider registries) {
        BalloonLevelSavedData sd = new BalloonLevelSavedData();
        if (tag.contains(ID)) {
            DataResult result = CODEC.decode((DynamicOps)NbtOps.INSTANCE, (Object)tag.getList(ID, 10));
            BalloonMap map = (BalloonMap)BalloonMap.MAP.get((LevelAccessor)level);
            result.ifSuccess(x -> map.getUnloadedBalloons().addAll((Collection)x.getFirst()));
        }
        return sd;
    }

    public static BalloonLevelSavedData get(ServerLevel level) {
        BalloonLevelSavedData data = (BalloonLevelSavedData)level.getChunkSource().getDataStorage().computeIfAbsent(new SavedData.Factory(BalloonLevelSavedData::new, (nbt, lookup) -> BalloonLevelSavedData.create(level, nbt, lookup), null), ID);
        data.level = level;
        return data;
    }

    @NotNull
    public CompoundTag save(CompoundTag tag, // Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider provider) {
        BalloonMap map = (BalloonMap)BalloonMap.MAP.get((LevelAccessor)this.level);
        ObjectArrayList list = new ObjectArrayList(map.getUnloadedBalloons());
        for (Balloon balloon : map.getBalloons()) {
            list.add((Object)BalloonMap.saveBalloon((ServerBalloon)balloon));
        }
        DataResult result = CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)list);
        result.ifSuccess(data -> tag.put(ID, data));
        return tag;
    }
}
