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

    /** Ticks after start to keep retrying graph/sub-level resolution before giving up on a record. */
    private static final long RESTORE_WINDOW_TICKS = 600; // ~30s

    /** Re-snapshot live trains to disk this often, so a hard crash keeps a recent rail position. */
    private static final int SNAPSHOT_INTERVAL_TICKS = 200;

    /** Records still waiting to be restored (trainId -> serialised compound). Drained on tick. */
    private static final Map<UUID, CompoundTag> pending = new HashMap<>();
    private static long restoreDeadline = -1;
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
        restoreDeadline = server.overworld().getGameTime() + RESTORE_WINDOW_TICKS;
        snapshotCounter = 0;
        if (!pending.isEmpty()) {
            LoconauticsConstants.LOGGER.info("[sabletrain] persistence: {} train(s) to restore after restart",
                    pending.size());
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        if (!pending.isEmpty()) {
            drainPending(server);
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
        restoreDeadline = -1;
    }

    // ---------------------------------------------------------------------------------------------
    // Restore.
    // ---------------------------------------------------------------------------------------------

    private static void drainPending(MinecraftServer server) {
        boolean pastDeadline = restoreDeadline >= 0 && server.overworld().getGameTime() > restoreDeadline;
        Iterator<Map.Entry<UUID, CompoundTag>> it = pending.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, CompoundTag> entry = it.next();
            SableTrain train;
            try {
                train = deserialize(entry.getValue(), server);
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
                LoconauticsConstants.LOGGER.info("[sabletrain] restored train {} ({} car(s)) in {}",
                        train.id(), train.cars().size(), train.level().dimension().location());
            } else if (pastDeadline) {
                // Graph/sub-level still not available after the window. Stop retrying this session, but KEEP the
                // disk record — the dimension may simply be unloaded and become resolvable on a future launch.
                LoconauticsConstants.LOGGER.warn(
                        "[sabletrain] could not restore train {} (track graph or sub-level unavailable) — will retry next launch",
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
        ListTag carList = new ListTag();
        for (SableTrain.Car car : train.cars()) {
            if (car.subLevelId() == null) {
                continue;
            }
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
            carList.add(c);
        }
        tag.put("Cars", carList);
        dims.write(tag); // AFTER the cars, so the palette has gathered every dimension the points reference
        return tag;
    }

    /**
     * Rebuilds a {@link SableTrain} from a serialised record, or returns {@code null} if it isn't yet
     * resolvable (dimension/container/track-graph/sub-level not loaded) — the caller retries those next tick.
     */
    static SableTrain deserialize(CompoundTag tag, MinecraftServer server) {
        ResourceKey<Level> dimKey = ResourceKey.create(
                Registries.DIMENSION, ResourceLocation.parse(tag.getString("Dimension")));
        ServerLevel level = server.getLevel(dimKey);
        if (level == null) {
            return null; // dimension not loaded yet
        }
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return null;
        }
        DimensionPalette dims = DimensionPalette.read(tag);

        List<SableTrain.Car> cars = new ArrayList<>();
        ListTag carList = tag.getList("Cars", Tag.TAG_COMPOUND);
        for (int i = 0; i < carList.size(); i++) {
            CompoundTag c = carList.getCompound(i);
            UUID subId = c.getUUID("SubLevel");
            UUID graphId = c.getUUID("Graph");

            TrackGraph graph = Create.RAILWAYS.trackNetworks.get(graphId);
            if (graph == null) {
                return null; // track graphs not loaded yet — retry the whole train later
            }
            if (!(container.getSubLevel(subId) instanceof ServerSubLevel)) {
                return null; // the car's sub-level hasn't loaded yet — retry later
            }
            RailCarriage carriage = RailCarriage.restore(graph, c.getCompound("Carriage"), dims);
            if (carriage == null) {
                // The rail this car sat on no longer exists in the graph — drop just this car.
                LoconauticsConstants.LOGGER.warn(
                        "[sabletrain] car {} could not be re-seated (track changed?) — skipping it", subId);
                continue;
            }
            Vector3dc localLead = readVec3d(c, "LocalLead");
            Vector3dc localTrail = readVec3d(c, "LocalTrail");
            cars.add(new SableTrain.Car(subId, carriage, localLead, localTrail));
        }
        if (cars.isEmpty()) {
            return null; // nothing placeable yet (or all cars' track is gone) — retry until the deadline
        }

        SableTrain train = new SableTrain(tag.getUUID("Id"), level, cars, tag.getBoolean("Physics"));
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
