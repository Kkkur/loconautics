/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package dev.ryanhcode.sable.sublevel.plot.heat;

import dev.ryanhcode.sable.SableConfig;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.plot.HeatDataChunkSection;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import dev.ryanhcode.sable.sublevel.plot.heat.HeatMapPropagationState;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SubLevelHeatMapManager {
    private static final Collection<SplitListener> LISTENERS = new ObjectArraySet();
    private static final BlockPos[] DIRECTION_OFFSETS = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 1, 0), new BlockPos(0, -1, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 1, 0), new BlockPos(-1, -1, 0), new BlockPos(1, -1, 0), new BlockPos(-1, 1, 0), new BlockPos(1, 0, 1), new BlockPos(-1, 0, -1), new BlockPos(1, 0, -1), new BlockPos(-1, 0, 1), new BlockPos(0, 1, 1), new BlockPos(0, -1, -1), new BlockPos(0, -1, 1), new BlockPos(0, 1, -1)};
    @NotNull
    private final ServerSubLevel subLevel;
    private final Long2IntOpenHashMap subLevelSplits = new Long2IntOpenHashMap();
    private final ObjectList<BlockPos> floodfill = new ObjectArrayList();
    private final ObjectList<BlockPos> removed = new ObjectArrayList();
    private final ObjectList<BlockPos> newStarts = new ObjectArrayList();
    private final IntArrayList splitIndexMap = new IntArrayList();
    private HeatMapPropagationState state = HeatMapPropagationState.FILLING;
    private boolean initialized = false;
    private boolean splitComplete = false;
    private int solidCount = 0;

    public SubLevelHeatMapManager(@NotNull ServerSubLevel subLevel) {
        this.subLevel = subLevel;
    }

    public void tick() {
        int steps = SableConfig.SUB_LEVEL_SPLITTING_HEATMAP_STEPS_PER_TICK.getAsInt();
        for (int i = 0; i < steps && !this.step(); ++i) {
        }
    }

    private boolean step() {
        short currentHeat;
        BlockPos p;
        if (this.state == HeatMapPropagationState.FILLING) {
            if (!this.floodfill.isEmpty()) {
                p = new BlockPos((Vec3i)this.floodfill.getFirst());
                this.floodfill.removeFirst();
                if (this.heatMapContains(p)) {
                    currentHeat = this.heatMapGet(p);
                    for (BlockPos dir : DIRECTION_OFFSETS) {
                        BlockPos p2 = p.offset((Vec3i)dir);
                        boolean solid = this.isSolidAt(p2);
                        boolean contains = this.heatMapContains(p2);
                        if (!solid || contains) continue;
                        this.heatMapSet(p2, (short)(currentHeat + 1));
                        this.subLevelSplits.remove(p2.asLong());
                        this.floodfill.add((Object)p2);
                    }
                }
            }
            if (this.floodfill.isEmpty()) {
                this.splitComplete = true;
                this.state = HeatMapPropagationState.CLEARING;
                if (!this.subLevelSplits.isEmpty()) {
                    this.split();
                }
            }
        }
        if (this.state == HeatMapPropagationState.CLEARING) {
            if (!this.floodfill.isEmpty()) {
                p = new BlockPos((Vec3i)this.floodfill.getFirst());
                this.floodfill.removeFirst();
                if (this.heatMapContains(p)) {
                    currentHeat = this.heatMapGet(p);
                    int currentIndex = this.splitIndexMap.getInt(this.subLevelSplits.get(p.asLong()));
                    for (BlockPos dir : DIRECTION_OFFSETS) {
                        int otherIndex;
                        BlockPos p2 = p.offset((Vec3i)dir);
                        if (!this.isSolidAt(p2)) continue;
                        if (this.subLevelSplits.containsKey(p2.asLong()) && currentIndex != (otherIndex = this.splitIndexMap.getInt(this.subLevelSplits.get(p2.asLong())))) {
                            this.splitIndexMap.set(this.subLevelSplits.get(p2.asLong()), currentIndex);
                        }
                        if (!this.heatMapContains(p2)) continue;
                        if (this.heatMapGet(p2) > currentHeat) {
                            this.floodfill.add((Object)p2);
                            this.subLevelSplits.put(p2.asLong(), currentIndex);
                            continue;
                        }
                        this.newStarts.add((Object)p2);
                    }
                    this.heatMapRemove(p);
                }
            } else if (!this.removed.isEmpty()) {
                for (BlockPos index : this.removed) {
                    BlockPos p2 = new BlockPos((Vec3i)index);
                    if (this.heatMapContains(p2)) {
                        short currentHeat2 = this.heatMapGet(p2);
                        for (BlockPos dir : DIRECTION_OFFSETS) {
                            BlockPos p22 = p2.offset((Vec3i)dir);
                            if (!this.isSolidAt(p22) || !this.heatMapContains(p22) || this.heatMapGet(p22) <= currentHeat2) continue;
                            boolean canRemove = true;
                            for (BlockPos dir2 : DIRECTION_OFFSETS) {
                                BlockPos p3;
                                if (new BlockPos(-dir.getX(), -dir.getY(), -dir.getZ()).equals((Object)dir2) || !this.isSolidAt(p3 = p22.offset((Vec3i)dir2)) || !this.heatMapContains(p3) || this.heatMapGet(p3) >= this.heatMapGet(p22)) continue;
                                canRemove = false;
                            }
                            if (!canRemove) continue;
                            this.floodfill.add((Object)p22);
                            int newIndex = this.splitIndexMap.size();
                            this.subLevelSplits.put(p22.asLong(), newIndex);
                            this.splitIndexMap.add(newIndex);
                        }
                    }
                    this.heatMapRemove(p2);
                }
                this.removed.clear();
            } else if (!this.newStarts.isEmpty()) {
                this.floodfill.addAll(this.newStarts);
                this.newStarts.clear();
                this.state = HeatMapPropagationState.FILLING;
            } else if (!this.subLevelSplits.isEmpty()) {
                this.splitComplete = true;
                this.split();
            } else {
                this.splitComplete = true;
                return true;
            }
        }
        return false;
    }

    private void split() {
        boolean splittingWholeSubLevel;
        Int2ObjectOpenHashMap newSubLevelBlocks = new Int2ObjectOpenHashMap();
        LongIterator longIterator = this.subLevelSplits.keySet().iterator();
        while (longIterator.hasNext()) {
            long l = (Long)longIterator.next();
            int splitIndex = this.splitIndexMap.get(this.subLevelSplits.get(l));
            ((List)newSubLevelBlocks.computeIfAbsent(splitIndex, x -> new ObjectArrayList())).add(BlockPos.of((long)l));
        }
        boolean bl = splittingWholeSubLevel = newSubLevelBlocks.size() == 1 && this.solidCount == ((List)newSubLevelBlocks.values().stream().findFirst().orElseThrow()).size();
        if (splittingWholeSubLevel) {
            List allBlocks = (List)newSubLevelBlocks.values().stream().findFirst().orElseThrow();
            this.rebuildHeatmapFrom(allBlocks);
            newSubLevelBlocks.clear();
        }
        int totalSplitBlocks = 0;
        for (List blocks : newSubLevelBlocks.values()) {
            totalSplitBlocks += blocks.size();
        }
        if (!splittingWholeSubLevel && totalSplitBlocks != 0 && totalSplitBlocks == this.solidCount) {
            Map.Entry minSize = newSubLevelBlocks.entrySet().stream().sorted(Comparator.comparingInt(a -> -((List)a.getValue()).size())).findFirst().orElseThrow();
            this.rebuildHeatmapFrom((List)minSize.getValue());
            newSubLevelBlocks.remove(((Integer)minSize.getKey()).intValue());
        }
        this.subLevelSplits.clear();
        this.splitIndexMap.clear();
        this.splitIndexMap.add(0);
        ServerLevel level = this.subLevel.getLevel();
        for (List blocks : newSubLevelBlocks.values()) {
            BoundingBox3i bounds = Objects.requireNonNull(BoundingBox3i.from((Iterable)blocks)).expand(1, 1, 1);
            for (SplitListener listener : LISTENERS) {
                listener.addBlocks((Level)level, (BoundingBox3ic)bounds, blocks);
            }
            ServerSubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks(level, (BlockPos)blocks.get(0), blocks, (BoundingBox3ic)bounds);
            if (subLevel.getMassTracker().getCenterOfMass() != null && !(subLevel.getMassTracker().getMass() <= 0.0)) continue;
            subLevel.getPlot().destroyAllBlocks();
            SubLevelContainer container = Objects.requireNonNull(SubLevelContainer.getContainer((Level)level));
            container.removeSubLevel(subLevel, SubLevelRemovalReason.REMOVED);
        }
    }

    private void rebuildHeatmapFrom(List<BlockPos> blocks) {
        this.state = HeatMapPropagationState.FILLING;
        this.initialized = false;
        this.splitComplete = false;
        this.solidCount = 0;
        this.newStarts.clear();
        this.floodfill.clear();
        this.removed.clear();
        blocks.forEach(this::heatMapRemove);
        blocks.forEach(this::onSolidAdded);
    }

    private boolean isSolidAt(BlockPos blockPos) {
        ServerLevel level = this.subLevel.getLevel();
        return !level.getBlockState(blockPos).isAir();
    }

    public void onSolidAdded(BlockPos blockPos) {
        short s;
        ++this.solidCount;
        if (!this.initialized) {
            this.initialized = true;
            this.heatMapSet(blockPos, (short)1);
            this.floodfill.add((Object)blockPos);
            this.splitIndexMap.add(0);
            return;
        }
        int n = Integer.MAX_VALUE;
        if (this.removed.remove((Object)blockPos)) {
            return;
        }
        for (BlockPos direction : DIRECTION_OFFSETS) {
            short heat;
            BlockPos neighbor = blockPos.offset((Vec3i)direction);
            if (!this.heatMapContains(neighbor) || (heat = this.heatMapGet(neighbor)) >= s) continue;
            s = heat;
        }
        if (s == Integer.MAX_VALUE) {
            if (!this.splitComplete) {
                this.subLevelSplits.put(blockPos.asLong(), 0);
            }
        } else {
            this.heatMapSet(blockPos, (short)(s + true));
            if (this.state == HeatMapPropagationState.FILLING) {
                this.floodfill.add((Object)blockPos);
            } else {
                this.newStarts.add((Object)blockPos);
            }
        }
    }

    public void onSolidRemoved(BlockPos blockPos) {
        --this.solidCount;
        this.removed.add((Object)blockPos);
    }

    private void heatMapRemove(BlockPos blockPos) {
        this.heatMapSet(blockPos, (short)0);
    }

    private boolean heatMapContains(BlockPos neighbor) {
        return this.heatMapGet(neighbor) != 0;
    }

    private short heatMapGet(BlockPos blockPos) {
        SectionPos section;
        ServerLevelPlot plot = this.subLevel.getPlot();
        PlotChunkHolder chunkHolder = plot.getChunkHolder(plot.toLocal((section = SectionPos.of((BlockPos)blockPos)).chunk()));
        if (chunkHolder == null) {
            return 0;
        }
        HeatDataChunkSection heatSection = chunkHolder.getHeatSection(section.y());
        if (heatSection == null) {
            return 0;
        }
        return heatSection.get(blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF);
    }

    private void heatMapSet(BlockPos blockPos, short value) {
        SectionPos section;
        ServerLevelPlot plot = this.subLevel.getPlot();
        PlotChunkHolder chunkHolder = plot.getChunkHolder(plot.toLocal((section = SectionPos.of((BlockPos)blockPos)).chunk()));
        if (chunkHolder == null) {
            return;
        }
        HeatDataChunkSection heatSection = chunkHolder.getHeatSection(section.y());
        if (heatSection == null) {
            heatSection = new HeatDataChunkSection();
            chunkHolder.setHeatSection(section.y(), heatSection);
        }
        heatSection.set(blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF, value);
    }

    public static void addSplitListener(SplitListener listener) {
        LISTENERS.add(listener);
    }

    @FunctionalInterface
    public static interface SplitListener {
        public void addBlocks(Level var1, BoundingBox3ic var2, Collection<BlockPos> var3);
    }
}
