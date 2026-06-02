/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.api.sublevel.SubLevelObserver
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.plot.LevelPlot
 *  dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  net.createmod.catnip.data.WorldAttached
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map;

import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.Balloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.ServerBalloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.graph.BalloonBuilder;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map.BalloonLevelSavedData;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map.SavedBalloon;
import dev.eriksonn.aeronautics.index.AeroTags;
import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BalloonMap {
    public static WorldAttached<BalloonMap> MAP = new WorldAttached(BalloonMap::new);
    private final Level level;
    private final ObjectSet<Balloon> balloons = new ObjectOpenHashSet();
    private final ObjectSet<SavedBalloon> unloadedBalloons = new ObjectOpenHashSet();
    private boolean initialized;

    public BalloonMap(LevelAccessor level) {
        this.level = (Level)level;
    }

    public static void tick(Level level) {
        ((BalloonMap)MAP.get((LevelAccessor)level)).tick();
    }

    public static void physicsTick(ServerLevel level, double timeStep) {
        BalloonMap handler = (BalloonMap)MAP.get((LevelAccessor)level);
        for (Balloon balloon : handler.balloons) {
            if (!(balloon instanceof ServerBalloon)) continue;
            ServerBalloon serverBalloon = (ServerBalloon)balloon;
            serverBalloon.applyForces(timeStep);
        }
    }

    public static SavedBalloon saveBalloon(ServerBalloon balloon) {
        return new SavedBalloon(new BoundingBox3i(balloon.getBounds()), balloon.getControllerPos(), balloon.getLiftingGasHolders());
    }

    public void addBalloon(Balloon balloon) {
        this.balloons.add((Object)balloon);
    }

    public void markDirty() {
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            BalloonLevelSavedData.get(serverLevel).setDirty();
        }
    }

    public void updateNearbyBalloons(BlockPos blockPos, BlockState oldState, BlockState newState) {
        boolean newSolid;
        boolean newAirtight;
        boolean oldAirtight = oldState.is(AeroTags.BlockTags.AIRTIGHT);
        if (oldAirtight != (newAirtight = newState.is(AeroTags.BlockTags.AIRTIGHT))) {
            for (Balloon balloon : this.getBalloonsNear(blockPos)) {
                if (newAirtight) {
                    balloon.onAirtightBlockAdded(blockPos);
                    continue;
                }
                balloon.onAirtightBlockRemoved(blockPos);
            }
            return;
        }
        boolean oldSolid = BalloonBuilder.isSolid(oldState);
        if (oldSolid != (newSolid = BalloonBuilder.isSolid(newState))) {
            for (Balloon balloon : this.getBalloonsNear(blockPos)) {
                if (newSolid) {
                    balloon.onSolidBlockAdded(blockPos);
                    continue;
                }
                balloon.onSolidBlockRemoved(blockPos);
            }
        }
    }

    public void tick() {
        if (!this.initialized) {
            Level level = this.level;
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                BalloonLevelSavedData.get(serverLevel);
            }
            this.initialized = true;
        }
        this.balloons.forEach(Balloon::tick);
        this.removeBalloons();
        this.mergeBalloons();
        this.markDirty();
    }

    private void mergeBalloons() {
        ObjectIterator iter = this.balloons.iterator();
        block0: while (iter.hasNext()) {
            Balloon balloon = (Balloon)iter.next();
            for (Balloon otherBalloon : this.balloons) {
                if (balloon == otherBalloon || !balloon.getBounds().intersects(otherBalloon.getBounds()) || balloon.getGraph().getLayerAt(otherBalloon.getControllerPos()) == null) continue;
                balloon.onRemoved();
                otherBalloon.merge(balloon);
                iter.remove();
                continue block0;
            }
        }
    }

    private void removeBalloons() {
        ObjectIterator iter = this.balloons.iterator();
        while (iter.hasNext()) {
            Balloon balloon = (Balloon)iter.next();
            if (balloon.isValid()) continue;
            iter.remove();
            balloon.onRemoved();
        }
    }

    public void removeBalloon(Balloon balloon) {
        balloon.onRemoved();
    }

    @Nullable
    public Balloon getBalloon(BlockPos blockPos) {
        for (Balloon balloon : this.balloons) {
            BoundingBox3ic bounds = balloon.getBounds();
            if (!bounds.contains(blockPos.getX(), blockPos.getY(), blockPos.getZ()) || !balloon.getGraph().hasBlockAt(blockPos)) continue;
            return balloon;
        }
        return null;
    }

    private Iterable<Balloon> getBalloonsNear(BlockPos blockPos) {
        ObjectArrayList balloons = null;
        int padding = 24;
        for (Balloon balloon : this.balloons) {
            BoundingBox3ic bounds = balloon.getBounds();
            if (blockPos.getX() <= bounds.minX() - 24 || blockPos.getY() <= bounds.minY() - 24 || blockPos.getZ() <= bounds.minZ() - 24 || blockPos.getX() >= bounds.maxX() + 24 || blockPos.getY() >= bounds.maxY() + 24 || blockPos.getZ() >= bounds.maxZ() + 24) continue;
            if (balloons == null) {
                balloons = new ObjectArrayList();
            }
            balloons.add((Object)balloon);
        }
        return balloons != null ? balloons : List.of();
    }

    public Iterable<Balloon> getBalloons() {
        return this.balloons;
    }

    public boolean isEmpty() {
        return this.balloons.isEmpty();
    }

    public void unloadBalloon(ServerBalloon balloon) {
        SavedBalloon unloadedBalloon = BalloonMap.saveBalloon(balloon);
        this.balloons.remove((Object)balloon);
        this.unloadedBalloons.add((Object)unloadedBalloon);
        this.markDirty();
    }

    public Collection<SavedBalloon> getUnloadedBalloons() {
        return this.unloadedBalloons;
    }

    public static class BalloonSubLevelObserver
    implements SubLevelObserver {
        private final Level level;

        public BalloonSubLevelObserver(Level level) {
            this.level = level;
        }

        public void onSubLevelRemoved(SubLevel subLevel, SubLevelRemovalReason reason) {
            if (reason == SubLevelRemovalReason.REMOVED) {
                LevelPlot plot = subLevel.getPlot();
                BalloonMap map = (BalloonMap)MAP.get((LevelAccessor)this.level);
                Iterator<Balloon> iter = map.getBalloons().iterator();
                while (iter.hasNext()) {
                    BlockPos controllerPos;
                    Balloon balloon = iter.next();
                    if (balloon.isAssembling() || !plot.contains((double)(controllerPos = balloon.getControllerPos()).getX(), (double)controllerPos.getZ())) continue;
                    balloon.onRemoved();
                    iter.remove();
                }
            }
        }
    }
}
