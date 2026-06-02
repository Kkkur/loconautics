/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.redstone_magnet;

import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetConsumer;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetPair;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetPairIdentifier;
import dev.simulated_team.simulated.util.SimMovementContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MagnetMap<T extends BlockEntity> {
    public final Map<LevelAccessor, Map<SectionPos, HashSet<BlockPos>>> magnetMap = new WeakHashMap<LevelAccessor, Map<SectionPos, HashSet<BlockPos>>>();
    public final Map<LevelAccessor, Map<MagnetPairIdentifier, MagnetPair<T>>> pairMap = new WeakHashMap<LevelAccessor, Map<MagnetPairIdentifier, MagnetPair<T>>>();

    public void addMagnet(LevelAccessor level, SectionPos sectionPos, BlockPos pos) {
        this.magnetMap.putIfAbsent(level, new HashMap());
        Map<SectionPos, HashSet<BlockPos>> levelMap = this.magnetMap.get(level);
        levelMap.putIfAbsent(sectionPos, new HashSet());
        HashSet<BlockPos> posSet = levelMap.get(sectionPos);
        posSet.add(pos);
    }

    public void removeMagnet(LevelAccessor level, SectionPos sectionPos, BlockPos pos) {
        Map<SectionPos, HashSet<BlockPos>> levelMap = this.magnetMap.get(level);
        if (levelMap == null) {
            return;
        }
        HashSet<BlockPos> posSet = levelMap.get(sectionPos);
        if (posSet == null) {
            return;
        }
        posSet.remove(pos);
        if (posSet.isEmpty()) {
            levelMap.remove(sectionPos);
            if (levelMap.isEmpty()) {
                this.magnetMap.remove(level);
            }
        }
    }

    public List<SimMovementContext> findNearby(SimMovementContext context) {
        Map<SectionPos, HashSet<BlockPos>> sectionMap = this.magnetMap.get(context.level());
        if (sectionMap == null) {
            return List.of();
        }
        int minX = Math.floorDiv((int)context.globalPosition().x - 8, 16);
        int minY = Math.floorDiv((int)context.globalPosition().y - 8, 16);
        int minZ = Math.floorDiv((int)context.globalPosition().z - 8, 16);
        ArrayList<SimMovementContext> contexts = new ArrayList<SimMovementContext>();
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                for (int k = 0; k < 2; ++k) {
                    SectionPos section = SectionPos.of((int)(minX + i), (int)(minY + j), (int)(minZ + k));
                    HashSet<BlockPos> posSet = sectionMap.get(section);
                    if (posSet == null) continue;
                    for (BlockPos blockPos : posSet) {
                        if (blockPos.equals((Object)context.localBlockPos())) continue;
                        SimMovementContext otherContext = SimMovementContext.getMovementContext(context.level(), Vec3.atCenterOf((Vec3i)blockPos));
                        contexts.add(otherContext);
                    }
                }
            }
        }
        return contexts;
    }

    @Nullable
    public MagnetPair<T> tryAddPair(Level level, BlockPos pos1, BlockPos pos2, MagnetConsumer<T> consumer) {
        this.pairMap.putIfAbsent((LevelAccessor)level, new HashMap());
        Map<MagnetPairIdentifier, MagnetPair<T>> levelMap = this.pairMap.get(level);
        MagnetPairIdentifier id = new MagnetPairIdentifier(pos1, pos2);
        MagnetPair<T> currentPair = levelMap.get(id);
        if (currentPair == null) {
            levelMap.put(id, consumer.apply(level, pos1, pos2));
        } else {
            currentPair.alive = true;
        }
        return currentPair;
    }

    @Nullable
    public MagnetPair<T> getPair(Level level, BlockPos pos1, BlockPos pos2) {
        Map<MagnetPairIdentifier, MagnetPair<T>> levelMap = this.pairMap.get(level);
        if (levelMap == null) {
            return null;
        }
        return levelMap.get(new MagnetPairIdentifier(pos1, pos2));
    }

    public void tick(Level level) {
        Map<MagnetPairIdentifier, MagnetPair<T>> map = this.pairMap.get(level);
        if (map != null) {
            map.entrySet().removeIf(x -> !((MagnetPair)x.getValue()).alive);
            for (MagnetPair<T> pair : map.values()) {
                pair.tick();
            }
        }
    }

    public void physicsTick(double substepTimeStep, Level level) {
        Map<MagnetPairIdentifier, MagnetPair<T>> pairs = this.pairMap.get(level);
        if (pairs != null) {
            for (MagnetPair<T> pair : pairs.values()) {
                pair.physicsTick(substepTimeStep);
            }
        }
    }
}
