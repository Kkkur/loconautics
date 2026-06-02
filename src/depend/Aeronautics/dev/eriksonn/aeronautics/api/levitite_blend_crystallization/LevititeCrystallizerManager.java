/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 */
package dev.eriksonn.aeronautics.api.levitite_blend_crystallization;

import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.CrystalPropagationContext;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.CrystallizationWorldSaveData;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeBlendTicker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class LevititeCrystallizerManager {
    private static final Map<LevelAccessor, List<LevititeBlendTicker>> tickers = new HashMap<LevelAccessor, List<LevititeBlendTicker>>();
    private static final List<LevititeBlendTicker> queuedTickers = new ArrayList<LevititeBlendTicker>();

    public static void tick(Level level) {
        if (tickers.containsKey(level)) {
            tickers.get(level).removeIf(LevititeBlendTicker::tick);
        }
        LevititeCrystallizerManager.addQueued(level);
    }

    private static void addQueued(Level level) {
        CrystallizationWorldSaveData data = CrystallizationWorldSaveData.get((ServerLevel)level);
        Set<BlockPos> tickedPositions = LevititeCrystallizerManager.getTickedPositions(level);
        List<LevititeBlendTicker> levelTickers = tickers.get(level);
        for (LevititeBlendTicker queuedTicker : queuedTickers) {
            if (tickedPositions.contains(queuedTicker.getPos())) continue;
            levelTickers.add(queuedTicker);
            queuedTicker.getContext().onCrystallizationInitialize(level, queuedTicker.getPos(), queuedTicker.isDormant);
            data.setDirty();
        }
        queuedTickers.clear();
    }

    public static void addTicker(Level level, BlockPos pos, int delay, boolean requiresCatalyst, boolean skipDormant, CrystalPropagationContext context) {
        queuedTickers.add(new LevititeBlendTicker(delay, pos, level, requiresCatalyst, skipDormant, context));
    }

    public static Set<BlockPos> getTickedPositions(Level level) {
        HashSet<BlockPos> tickedPositions = new HashSet<BlockPos>();
        tickers.putIfAbsent((LevelAccessor)level, new ArrayList());
        tickers.get(level).forEach(t -> tickedPositions.add(t.getPos()));
        return tickedPositions;
    }

    public static void saveData(ListTag list, Level level) {
        if (tickers.containsKey(level)) {
            for (LevititeBlendTicker ticker : tickers.get(level)) {
                list.add((Object)ticker.serialize());
            }
        }
    }

    public static void loadData(CompoundTag tag, Level level) {
        tickers.putIfAbsent((LevelAccessor)level, new ArrayList());
        ListTag data = tag.getList("Levitite Manager Data", 10);
        ArrayList<LevititeBlendTicker> newTickers = new ArrayList<LevititeBlendTicker>();
        for (int i = 0; i < data.size(); ++i) {
            newTickers.add(new LevititeBlendTicker(data.getCompound(i), level));
        }
        tickers.put((LevelAccessor)level, newTickers);
    }

    public static void clearLevel(LevelAccessor level) {
        tickers.remove(level);
    }
}
