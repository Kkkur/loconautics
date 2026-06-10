package com.lycoris.loconautics.allsable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.lycoris.loconautics.core.LoconauticsConstants;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Makes the all-Sable custom trains ({@link SableTrain}) survive server/world restarts.
 *
 * <p>The cars' blocks already persist (Sable saves the sub-levels), but the in-memory {@link SableTrainRegistry}
 * does not — so after a restart the cars sit there un-driven (orphans). This class:
 * <ul>
 *   <li><b>persists</b> each train to {@link SableTrainStore} whenever it is spawned/changed, periodically, and
 *       on shutdown (to capture its latest rail position);</li>
 *   <li><b>restores</b> them after start: each tick it retries the saved records until the Create track graphs
 *       and Sable sub-levels they reference are available, then rebuilds the {@link RailCarriage}s and registers
 *       the {@link SableTrain} so {@link SableTrainDriver} picks it up again.</li>
 * </ul>
 */
@EventBusSubscriber(modid = LoconauticsConstants.MOD_ID)
public final class SableTrainPersistence {

    /** How often (ticks) to attempt resolving still-pending records. A car's sub-level only becomes active once
     *  its plot-grid chunk loads, which can be long after start — so we keep retrying, not just for ~30s. */
    private static final int RESTORE_RETRY_INTERVAL = 10;

    /** How often (ticks) to log WHY a still-pending train can't be restored yet (so a remote test is
     *  self-diagnosing without us seeing the log live). */
    private static final int RESTORE_LOG_INTERVAL = 100; // ~5s

    /** After this many ticks still pending, stop retrying this session (keep the disk record for next launch).
     *  Generous: a sub-level may only activate when the player walks near it. */
    private static final int RESTORE_GIVEUP_TICKS = 6000; // ~5 min

    /** Re-snapshot live trains to disk this often, so a hard crash keeps a recent rail position. */
    private static final int SNAPSHOT_INTERVAL_TICKS = 200;

    /** Records still waiting to be restored (trainId -> serialised compound). Drained on tick. */
    private static final Map<UUID, CompoundTag> pending = new HashMap<>();
    /** Ticks elapsed since start while records are still pending (drives retry/log/giveup cadence). */
    private static int restoreTicks = 0;
    private static int snapshotCounter = 0;

    private SableTrainPersistence() {
    }

    // ---------------------------------------------------------------------------------------------
    // Mutation hooks — called by SableTrainSpawner when a train is created/changed/removed.
    // ---------------------------------------------------------------------------------------------

    /** Writes (or overwrites) the train's record to disk. */
    public static void persist(SableTrain train) {
        MinecraftServer server = train.level().getServer();
        if (server == null) {
            return;
        }
        try {
            SableTrainStore.get(server).put(train.id(), serialize(train));
        } catch (Throwable t) {
            LoconauticsConstants.LOGGER.error("[sabletrain] failed to persist train {}", train.id(), t);
        }
    }

    /** Deletes the train's record from disk (when it is cleared or loses its last car). */
    public static void unpersist(MinecraftServer server, UUID id) {
        if (server == null) {
            return;
        }
        SableTrainStore.get(server).remove(id);
        pending.remove(id);
    }

    // ---------------------------------------------------------------------------------------------
    // Lifecycle.
    // ---------------------------------------------------------------------------------------------

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        pending.clear();
        pending.putAll(SableTrainStore.get(server).records());
        restoreTicks = 0;
        snapshotCounter = 0;
        // Loud banner so a remote tester can confirm THIS build (with persistence) is the one running.
        LoconauticsConstants.LOGGER.info("[sabletrain] persistence active — {} saved train(s) to restore",
                pending.size());
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        if (!pending.isEmpty()) {
            restoreTicks++;
            if (restoreTicks % RESTORE_RETRY_INTERVAL == 0) {
                boolean verbose = restoreTicks % RESTORE_LOG_INTERVAL == 0; // periodically log why it's stuck
                drainPending(server, verbose);
            }
        }
        if (!SableTrainRegistry.isEmpty() && (snapshotCounter++ % SNAPSHOT_INTERVAL_TICKS) == 0) {
            snapshotAll(server);
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        MinecraftServer server = event.getServer();
        snapshotAll(server);          // capture final rail positions before the registry is dropped
        SableTrainRegistry.clear();   // in-memory only — the on-disk records stay for next launch
        pending.clear();
        restoreTicks = 0;
    }

    // ---------------------------------------------------------------------------------------------
    // Restore.
    // ---------------------------------------------------------------------------------------------

    private static void drainPending(MinecraftServer server, boolean verbose) {
        boolean giveUp = restoreTicks >= RESTORE_GIVEUP_TICKS;
        Iterator<Map.Entry<UUID, CompoundTag>> it = pending.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, CompoundTag> entry = it.next();
            SableTrain train;
            try {
                train = deserialize(entry.getValue(), server, verbose, entry.getKey());
            } catch (Throwable t) {
                LoconauticsConstants.LOGGER.error("[sabletrain] error restoring train {} — dropping", entry.getKey(), t);
                it.remove();
                continue;
            }
            if (train != null) {
                SableTrainRegistry.register(train);
                SableTrainSpawner.ensureObserver(train.level());
                SableTrainSpawner.noteRestored(train.id());
                it.remove();
                LoconauticsConstants.LOGGER.info("[sabletrain] restored cart {} in {}",
                        train.id(), train.level().dimension().location());
            } else if (giveUp) {
                // Still unresolved after a long window. Stop retrying this session, but KEEP the disk record so a
                // future launch (e.g. once the player visits that area / dimension) can restore it.
                LoconauticsConstants.LOGGER.warn(
                        "[sabletrain] giving up on train {} this session (track graph or sub-level never became available) — kept on disk for next launch",
                        entry.getKey());
                it.remove();
            }
        }
    }

    private static void snapshotAll(MinecraftServer server) {
        SableTrainStore store = SableTrainStore.get(server);
        for (SableTrain train : SableTrainRegistry.all()) {
            try {
                store.put(train.id(), serialize(train));
            } catch (Throwable t) {
                LoconauticsConstants.LOGGER.error("[sabletrain] snapshot failed for train {}", train.id(), t);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // (De)serialisation.
    // ---------------------------------------------------------------------------------------------

    /** Serialises a live train (id, dimension, motion, and every car's sub-level + carriage) to NBT. */
    static CompoundTag serialize(SableTrain train) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Id", train.id());
        tag.putString("Dimension", train.level().dimension().location().toString());
        tag.putBoolean("Physics", train.isPhysics());
        tag.putDouble("TargetSpeed", train.targetSpeed());
        tag.putDouble("Speed", train.speed());
        tag.putDouble("Accel", train.accel());

        DimensionPalette dims = new DimensionPalette();
        SableTrain.Car car = train.car();
        if (car.subLevelId() != null) {
            CompoundTag c = new CompoundTag();
            c.putUUID("SubLevel", car.subLevelId());
            c.putUUID("Graph", car.carriage().graphId());
            c.put("Carriage", car.carriage().writeNbt(dims)); // populates dims via the bogey points
            if (car.localLead() != null) {
                writeVec3d(c, "LocalLead", car.localLead());
            }
            if (car.localTrail() != null) {
                writeVec3d(c, "LocalTrail", car.localTrail());
            }
            // Loose bogeys: each its own sub-level + rail carriage.
            ListTag bogeyList = new ListTag();
            for (SableTrain.Bogey bogey : car.bogeys()) {
                CompoundTag bt = new CompoundTag();
                bt.putUUID("SubLevel", bogey.subLevelId());
                bt.putUUID("Graph", bogey.rail().graphId());
                bt.put("Carriage", bogey.rail().writeNbt(dims));
                bogeyList.add(bt);
            }
            c.put("Bogeys", bogeyList);
            tag.put("Car", c);
        }
        dims.write(tag); // AFTER the car, so the palette has gathered every dimension the points reference
        return tag;
    }

    /**
     * Rebuilds a {@link SableTrain} from a serialised record, or returns {@code null} if it isn't yet
     * resolvable (dimension/container/track-graph/sub-level not loaded) — the caller retries those next tick.
     */
    static SableTrain deserialize(CompoundTag tag, MinecraftServer server, boolean verbose, UUID trainId) {
        ResourceKey<Level> dimKey = ResourceKey.create(
                Registries.DIMENSION, ResourceLocation.parse(tag.getString("Dimension")));
        ServerLevel level = server.getLevel(dimKey);
        if (level == null) {
            if (verbose) {
                LoconauticsConstants.LOGGER.warn("[sabletrain] waiting to restore {}: dimension {} not loaded",
                        trainId, tag.getString("Dimension"));
            }
            return null; // dimension not loaded yet
        }
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            if (verbose) {
                LoconauticsConstants.LOGGER.warn("[sabletrain] waiting to restore {}: no sub-level container yet", trainId);
            }
            return null;
        }
        DimensionPalette dims = DimensionPalette.read(tag);

        // A cart is a single car. Back-compat: pre-rope saves stored a "Cars" list — read its first entry.
        CompoundTag c;
        if (tag.contains("Car")) {
            c = tag.getCompound("Car");
        } else {
            ListTag carList = tag.getList("Cars", Tag.TAG_COMPOUND);
            if (carList.isEmpty()) {
                return null;
            }
            c = carList.getCompound(0);
        }
        UUID subId = c.getUUID("SubLevel");
        UUID graphId = c.getUUID("Graph");

        TrackGraph graph = Create.RAILWAYS.trackNetworks.get(graphId);
        if (graph == null) {
            if (verbose) {
                LoconauticsConstants.LOGGER.warn(
                        "[sabletrain] waiting to restore {}: track graph {} not loaded ({} graphs present)",
                        trainId, graphId, Create.RAILWAYS.trackNetworks.size());
            }
            return null; // track graphs not loaded yet — retry later
        }
        if (!(container.getSubLevel(subId) instanceof ServerSubLevel)) {
            if (verbose) {
                LoconauticsConstants.LOGGER.warn(
                        "[sabletrain] waiting to restore {}: sub-level {} not active yet (chunk not loaded?)",
                        trainId, subId);
            }
            return null; // the cart's sub-level hasn't loaded yet — retry later
        }
        RailCarriage carriage = RailCarriage.restore(graph, c.getCompound("Carriage"), dims);
        if (carriage == null) {
            // The rail this cart sat on no longer exists in the graph — it can't be re-seated, drop it.
            LoconauticsConstants.LOGGER.warn(
                    "[sabletrain] cart {} could not be re-seated (track changed?) — dropping it", subId);
            return null;
        }
        // Restore the loose bogeys (each its own sub-level + rail). Skip a bogey whose sub-level/track is gone;
        // wait (retry) if its graph/sub-level just isn't loaded yet.
        List<SableTrain.Bogey> bogeys = new ArrayList<>();
        ListTag bogeyList = c.getList("Bogeys", Tag.TAG_COMPOUND);
        for (int j = 0; j < bogeyList.size(); j++) {
            CompoundTag bt = bogeyList.getCompound(j);
            UUID bSub = bt.getUUID("SubLevel");
            TrackGraph bGraph = Create.RAILWAYS.trackNetworks.get(bt.getUUID("Graph"));
            if (bGraph == null || !(container.getSubLevel(bSub) instanceof ServerSubLevel)) {
                return null; // bogey graph/sub-level not ready yet — retry
            }
            RailCarriage bRail = RailCarriage.restore(bGraph, bt.getCompound("Carriage"), dims);
            if (bRail != null) {
                bogeys.add(new SableTrain.Bogey(bSub, bRail));
            }
        }
        Vector3dc localLead = readVec3d(c, "LocalLead");
        Vector3dc localTrail = readVec3d(c, "LocalTrail");
        SableTrain.Car car = new SableTrain.Car(subId, carriage, bogeys, localLead, localTrail);

        SableTrain train = new SableTrain(tag.getUUID("Id"), level, car, tag.getBoolean("Physics"));
        train.setTargetSpeed(tag.getDouble("TargetSpeed"));
        train.setSpeed(tag.getDouble("Speed"));
        if (tag.contains("Accel")) {
            train.setAccel(tag.getDouble("Accel"));
        }
        return train;
    }

    // ----- small vector helpers -----

    private static void writeVec3d(CompoundTag tag, String key, Vector3dc v) {
        CompoundTag t = new CompoundTag();
        t.putDouble("x", v.x());
        t.putDouble("y", v.y());
        t.putDouble("z", v.z());
        tag.put(key, t);
    }

    private static Vector3dc readVec3d(CompoundTag tag, String key) {
        if (!tag.contains(key)) {
            return null;
        }
        CompoundTag t = tag.getCompound(key);
        return new Vector3d(t.getDouble("x"), t.getDouble("y"), t.getDouble("z"));
    }
}
