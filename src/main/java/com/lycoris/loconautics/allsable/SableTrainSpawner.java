package com.lycoris.loconautics.allsable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.mixin.StationBlockEntityAccessor;
import com.lycoris.loconautics.network.packets.SableTrainSyncPacket;
import com.lycoris.loconautics.registry.LoconauticsRegistries;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.graph.TrackGraphHelper;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.foundation.utility.CreateLang;

import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueEntity;
import dev.simulated_team.simulated.util.SimAssemblyHelper;
import dev.simulated_team.simulated.util.SimMathUtils;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper.GatherResult;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Debug spawner for the all-Sable custom train (Option B). Build a cart on a Create rail, look at it, run
 * {@code /loconautics sabletrain [speed]}: we find the rail under the cart, flood-fill the cart blocks that
 * sit <b>above the rail</b> (so it doesn't matter if the cart touches the ground), lift them into a Sable
 * sub-level, seat a {@link RailCarriage}, and register a {@link SableTrain} driven by {@link SableTrainDriver}.
 *
 * <p>Validated in-game: the body rides the rail through curves and carries the player smoothly.
 */
public final class SableTrainSpawner {

    private static final double REACH = 12.0;
    private static final int MAX_BLOCKS = 4096;
    private static final int RAIL_SEARCH_DOWN = 16;
    private static final int RAIL_SEARCH_RADIUS = 2;

    /** Containers we've attached our removal observer to (so a destroyed/unloaded car cleans up its train). */
    private static final Set<ServerSubLevelContainer> OBSERVED =
            Collections.newSetFromMap(new IdentityHashMap<>());

    private SableTrainSpawner() {
    }

    /** A rail location under the cart + the track up-normal + the rail's world Y (top surface). */
    private record TrackHit(TrackGraphLocation location, Vec3 upNormal, int railY) {}

    /** The train most recently spawned, so {@code addcar} knows which convoy to extend. */
    private static UUID lastTrainId;

    /** Spawns a NEW single-car custom train from the cart the player is looking at.
     *  {@code physics=false} = kinematic teleport-pin (smooth, rigid); {@code physics=true} = free body held to
     *  the rail by bogey spring forces (can derail). */
    public static int spawn(ServerPlayer player, double speed, boolean physics) {
        if (!Config.ENABLE_SABLE_MODE.get()) {
            msg(player, "Sable physics mode is disabled in the config (enableSableMode)");
            return 0;
        }
        ServerLevel level = player.serverLevel();
        SableTrain.Car car = buildCar(player, level, null);
        if (car == null) {
            return 0;
        }
        UUID id = UUID.randomUUID();
        SableTrain train = new SableTrain(id, level, car, physics);
        train.setTargetSpeed(speed);
        SableTrainRegistry.register(train);
        ensureObserver(level);
        lastTrainId = id;
        SableTrainPersistence.persist(train); // survive restarts
        SableTrainSyncPacket.broadcast(train); // mark this body sub-level as a train sub-level on clients
        msg(player, String.format("%s cart spawned speed=%.2f — id %s — join carts together with steel cable",
                physics ? "PHYSICS" : "pinned", speed, id.toString().substring(0, 8)));
        return 1;
    }

    // ============================================================================================
    // Station-driven assembly (the proper assembly path, triggered from Create's AssemblyScreen).
    // ============================================================================================

    /** How many blocks above the rail line to scan for glued cart blocks. */
    private static final int STATION_SCAN_HEIGHT = 10;
    /** How far to either side of the rail line to scan (cart overhang). */
    private static final int STATION_SCAN_WIDTH = 2;

    /**
     * Assembles Sable trains from a Create train station instead of the player's view. Scans the station's
     * assembly range for glued clusters of blocks: each glued cluster that carries a Create bogey becomes its
     * own <b>independent</b> {@link SableTrain} car (clusters are never merged — gluing two clusters together is
     * what makes them one carriage). Only the frontmost carriage (nearest the station along the track) is
     * required to contain a {@code loconautics:bearing_axle}. Failures are surfaced through Create's standard
     * {@link AssemblyException} channel (rendered by {@code AssemblyScreen}), never via chat.
     */
    public static void assembleFromStation(ServerPlayer player, BlockPos stationPos) {
        ServerLevel level = player.serverLevel();
        if (!(level.getBlockEntity(stationPos) instanceof StationBlockEntity station)) {
            return;
        }
        StationBlockEntityAccessor acc = (StationBlockEntityAccessor) station;

        // Master switch: when off, the addon behaves as if not installed (no physics assembly).
        if (!Config.ENABLE_SABLE_MODE.get()) {
            fail(acc, "disabled", -1);
            return;
        }

        // Use Create's own scan to refresh the assembly area, direction, and bogey count.
        station.refreshAssemblyInfo();
        Direction dir = station.getAssemblyDirection();
        if (dir == null) {
            fail(acc, "no_track", -1);
            return;
        }
        BoundingBox area = StationBlockEntity.assemblyAreas.get(level).get(stationPos);
        if (area == null || acc.loconautics$getBogeyCount() == 0) {
            fail(acc, "no_bogeys", -1);
            return;
        }

        Vec3 railDir = Vec3.atLowerCornerOf(dir.getNormal());
        BlockPos stationTrack = station.edgePoint.getGlobalPosition();
        double railY = stationTrack.getY();

        // Scan a band sitting on top of the rail line for glue entities (Create super glue + Simulated honey
        // glue). The band is the assembly area inflated sideways for cart overhang and upward for cart height.
        AABB scan = new AABB(
                area.minX() - STATION_SCAN_WIDTH, railY + 1, area.minZ() - STATION_SCAN_WIDTH,
                area.maxX() + 1 + STATION_SCAN_WIDTH, railY + 1 + STATION_SCAN_HEIGHT, area.maxZ() + 1 + STATION_SCAN_WIDTH);

        List<Entity> glue = new ArrayList<>(level.getEntitiesOfClass(SuperGlueEntity.class, scan));
        try {
            collectHoneyGlue(level, scan, glue); // Simulated is an optional dependency — guard its class load
        } catch (Throwable ignored) {
        }

        // Union the solid blocks each glue entity rigidly links. Blocks not joined by any glue never enter the
        // union-find, so they are silently invisible to the assembler.
        Map<BlockPos, BlockPos> uf = new HashMap<>();
        for (Entity g : glue) {
            List<BlockPos> linked = solidBlocksIn(level, g.getBoundingBox(), scan);
            if (linked.size() < 2) {
                continue; // a glue touching only one solid block links nothing
            }
            BlockPos root = linked.get(0);
            ufAdd(uf, root);
            for (int i = 1; i < linked.size(); i++) {
                ufAdd(uf, linked.get(i));
                ufUnion(uf, root, linked.get(i));
            }
        }
        // Group the glued blocks by their union-find root → one set of blocks per glued cluster.
        Map<BlockPos, Set<BlockPos>> clustersByRoot = new HashMap<>();
        for (BlockPos p : uf.keySet()) {
            clustersByRoot.computeIfAbsent(ufFind(uf, p), k -> new HashSet<>()).add(p);
        }

        // A cluster is a carriage only if it carries a Create bogey; bogey-less clusters are ignored.
        List<Set<BlockPos>> carriages = new ArrayList<>();
        for (Set<BlockPos> cluster : clustersByRoot.values()) {
            if (clusterHasBogey(level, cluster)) {
                carriages.add(cluster);
            }
        }

        // Every bogey on the rail in range, ordered front-to-back (nearest the station first). The frontmost
        // bogey is the leading wheel-set — Create's own assembler keys its errors off this same ordering.
        List<BlockPos> bogeys = bogeysInBand(level, scan);
        if (bogeys.isEmpty()) {
            fail(acc, "no_bogeys", -1);
            return;
        }
        bogeys.sort(java.util.Comparator.comparingDouble(
                b -> (b.getX() - stationTrack.getX()) * railDir.x + (b.getZ() - stationTrack.getZ()) * railDir.z));
        BlockPos frontBogey = bogeys.get(0);

        // The frontmost carriage is the glued cluster that actually contains the frontmost bogey. If that bogey
        // has no glued structure on it, refuse exactly like Create ("No structure attached to bogey 1").
        Set<BlockPos> frontCarriage = carriageContaining(carriages, frontBogey);
        if (frontCarriage == null) {
            failNothingAttached(acc, 1);
            return;
        }

        // Bearing-axle requirement: ONLY the frontmost carriage must contain a loconautics:bearing_axle. Checked
        // before any sub-level is created so a refusal leaves the build untouched.
        if (!clusterHasBearingAxle(level, frontCarriage)) {
            fail(acc, "missing_bearing_axle", 1);
            return;
        }

        // Assemble front-to-back: frontmost carriage first, then the rest ordered back along the track.
        carriages.sort(java.util.Comparator.comparingDouble(c -> projectionAlong(c, stationTrack, railDir)));

        // A carriage may carry at most this many bogeys (Create's structural limit is 2; configurable). The
        // carriage index in the error matches the front-to-back ordering Create's own assembler uses.
        int maxBogeys = Config.MAX_BOGEYS_PER_CARRIAGE.get();
        for (int i = 0; i < carriages.size(); i++) {
            if (countBogeysIn(level, carriages.get(i)) > maxBogeys) {
                acc.loconautics$exception(new AssemblyException(
                        Component.translatable("loconautics.station.sabletrain.too_many_bogeys", maxBogeys)), i + 1);
                return;
            }
        }

        // All validation passed — assemble each cluster as its own independent Sable car. No links are created
        // between cars: coupling them (steel cable, knuckle, …) is the player's job at runtime.
        Set<BlockPos> assembled = new HashSet<>();
        int built = 0;
        SableTrain frontTrain = null; // the frontmost car (nearest the station marker), assembled first
        for (Set<BlockPos> cluster : carriages) {
            BoundingBox3i bounds = boundsOf(cluster);
            BlockPos anchor = frontmostBlock(cluster, stationTrack, railDir);
            SableTrain.Car car = seatCar(level, cluster, bounds, railDir, anchor);
            if (car == null) {
                LoconauticsConstants.LOGGER.warn(
                        "[sabletrain] station assembly: a carriage could not be seated (no rail/graph?) — skipped");
                continue;
            }
            UUID id = UUID.randomUUID();
            SableTrain train = new SableTrain(id, level, car, false);
            train.setTargetSpeed(0.0); // start idle — at rest on the rail until the player drives it
            SableTrainRegistry.register(train);
            ensureObserver(level);
            lastTrainId = id;
            SableTrainPersistence.persist(train);
            SableTrainSyncPacket.broadcast(train); // mark this body sub-level as a train sub-level on clients
            assembled.addAll(cluster);
            if (frontTrain == null) {
                frontTrain = train;
            }
            built++;
        }
        if (built == 0) {
            fail(acc, "no_bogeys", -1);
            return;
        }

        // A freshly assembled train sits on the station marker — mark its front car present so the station block
        // shows it arrived (flag + disassemble) immediately, without waiting for it to be driven away and parked.
        frontTrain.setAtStation(station.getStation());

        // Success: clear any previous error and drop the station out of assembly mode, exactly like Create does
        // after a normal assembly. The new cars sit idle (throttle 0, glued to the rail) until the player drives
        // them with a Bearing Axle.
        acc.loconautics$exception(null, -1);
        station.exitAssemblyMode();
        LoconauticsConstants.LOGGER.info("[sabletrain] station at {} assembled {} independent Sable car(s) (idle)",
                stationPos, built);
    }

    /** Surfaces an assembly failure through Create's standard error channel (no chat). */
    private static void fail(StationBlockEntityAccessor acc, String key, int carriage) {
        acc.loconautics$exception(
                new AssemblyException(Component.translatable("loconautics.station.sabletrain." + key)), carriage);
    }

    /**
     * Refuses assembly with Create's own "No structure attached to bogey N" message, so an unglued leading bogey
     * reads identically to Create's vanilla train assembler. Mirrors Create passing {@code -1} as the carriage
     * index (the bogey number is already baked into the message).
     */
    private static void failNothingAttached(StationBlockEntityAccessor acc, int bogeyNumber) {
        acc.loconautics$exception(
                new AssemblyException(CreateLang.translateDirect("train_assembly.nothing_attached", bogeyNumber)), -1);
    }

    /** The carriage cluster that contains {@code pos}, or {@code null} if no glued carriage covers it. */
    private static Set<BlockPos> carriageContaining(List<Set<BlockPos>> carriages, BlockPos pos) {
        for (Set<BlockPos> carriage : carriages) {
            if (carriage.contains(pos)) {
                return carriage;
            }
        }
        return null;
    }

    /** Every Create bogey block sitting inside the station's scan band (immutable positions). */
    private static List<BlockPos> bogeysInBand(ServerLevel level, AABB scan) {
        List<BlockPos> out = new ArrayList<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = Mth.floor(scan.minX); x < Mth.ceil(scan.maxX); x++) {
            for (int y = Mth.floor(scan.minY); y < Mth.ceil(scan.maxY); y++) {
                for (int z = Mth.floor(scan.minZ); z < Mth.ceil(scan.maxZ); z++) {
                    if (level.getBlockState(pos.set(x, y, z)).getBlock() instanceof AbstractBogeyBlock) {
                        out.add(pos.immutable());
                    }
                }
            }
        }
        return out;
    }

    /** Adds Simulated's honey-glue entities to {@code out}; isolated so its (optional) class only loads here. */
    private static void collectHoneyGlue(ServerLevel level, AABB scan, List<Entity> out) {
        out.addAll(level.getEntitiesOfClass(HoneyGlueEntity.class, scan));
    }

    /** Solid, non-track blocks whose centre lies inside both the glue box and the overall scan band. */
    private static List<BlockPos> solidBlocksIn(ServerLevel level, AABB box, AABB scan) {
        List<BlockPos> out = new ArrayList<>();
        int minX = Mth.floor(box.minX);
        int minY = Mth.floor(box.minY);
        int minZ = Mth.floor(box.minZ);
        int maxX = Mth.ceil(box.maxX) - 1;
        int maxY = Mth.ceil(box.maxY) - 1;
        int maxZ = Mth.ceil(box.maxZ) - 1;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Vec3 centre = new Vec3(x + 0.5, y + 0.5, z + 0.5);
                    if (!box.contains(centre) || !scan.contains(centre)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(pos.set(x, y, z));
                    if (state.isAir() || state.getBlock() instanceof ITrackBlock) {
                        continue;
                    }
                    out.add(pos.immutable());
                }
            }
        }
        return out;
    }

    /** True if any block in the cluster is a Create train bogey. */
    private static boolean clusterHasBogey(ServerLevel level, Set<BlockPos> cluster) {
        for (BlockPos p : cluster) {
            if (level.getBlockState(p).getBlock() instanceof AbstractBogeyBlock) {
                return true;
            }
        }
        return false;
    }

    /** Number of Create train bogey blocks in the cluster (a carriage is capped at maxBogeysPerCarriage). */
    private static int countBogeysIn(ServerLevel level, Set<BlockPos> cluster) {
        int count = 0;
        for (BlockPos p : cluster) {
            if (level.getBlockState(p).getBlock() instanceof AbstractBogeyBlock) {
                count++;
            }
        }
        return count;
    }

    /** True if any block in the cluster is a {@code loconautics:bearing_axle}. */
    private static boolean clusterHasBearingAxle(ServerLevel level, Set<BlockPos> cluster) {
        Block axle = LoconauticsRegistries.BEARING_AXLE.get();
        for (BlockPos p : cluster) {
            if (level.getBlockState(p).getBlock() == axle) {
                return true;
            }
        }
        return false;
    }

    /** Mean signed distance of the cluster from the station along the track direction (smaller = nearer/front). */
    private static double projectionAlong(Set<BlockPos> cluster, BlockPos station, Vec3 railDir) {
        double sum = 0.0;
        for (BlockPos p : cluster) {
            sum += (p.getX() - station.getX()) * railDir.x + (p.getZ() - station.getZ()) * railDir.z;
        }
        return sum / cluster.size();
    }

    /** The cluster block nearest the station along the track direction (used as the sub-level anchor). */
    private static BlockPos frontmostBlock(Set<BlockPos> cluster, BlockPos station, Vec3 railDir) {
        return cluster.stream()
                .min(java.util.Comparator.comparingDouble(
                        p -> (p.getX() - station.getX()) * railDir.x + (p.getZ() - station.getZ()) * railDir.z))
                .orElseGet(() -> cluster.iterator().next());
    }

    /** Axis-aligned integer bounds enclosing every block in the cluster. */
    private static BoundingBox3i boundsOf(Set<BlockPos> cluster) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (BlockPos p : cluster) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            minZ = Math.min(minZ, p.getZ());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
            maxZ = Math.max(maxZ, p.getZ());
        }
        return new BoundingBox3i(minX, minY, minZ, maxX, maxY, maxZ);
    }

    // ----- tiny BlockPos union-find (glued-cluster detection) -----

    private static void ufAdd(Map<BlockPos, BlockPos> uf, BlockPos p) {
        uf.putIfAbsent(p, p);
    }

    private static BlockPos ufFind(Map<BlockPos, BlockPos> uf, BlockPos p) {
        BlockPos root = p;
        while (!uf.get(root).equals(root)) {
            root = uf.get(root);
        }
        BlockPos cur = p; // path-compress
        while (!cur.equals(root)) {
            BlockPos next = uf.get(cur);
            uf.put(cur, root);
            cur = next;
        }
        return root;
    }

    private static void ufUnion(Map<BlockPos, BlockPos> uf, BlockPos a, BlockPos b) {
        BlockPos ra = ufFind(uf, a);
        BlockPos rb = ufFind(uf, b);
        if (!ra.equals(rb)) {
            uf.put(ra, rb);
        }
    }

    /**
     * Lifts the cart the player is looking at into a sub-level and seats a {@link RailCarriage} under it,
     * returning a {@link SableTrain.Car}. Sends a chat message and returns {@code null} on any failure.
     */
    private static SableTrain.Car buildCar(ServerPlayer player, ServerLevel level, Vec3 dirHint) {
        // 1) Raycast to the cart block the player is looking at (always the player's actual view).
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 view = player.getViewVector(1.0F);
        Vec3 end = eye.add(view.scale(REACH));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() == HitResult.Type.MISS) {
            msg(player, "look at the cart you built (on a rail), within " + (int) REACH + " blocks");
            return null;
        }
        BlockPos origin = hit.getBlockPos();
        if (level.getBlockState(origin).getBlock() instanceof ITrackBlock) {
            msg(player, "look at the CART, not the rail itself");
            return null;
        }

        // Direction used to pick the rail's travel axis: the train's forward when extending a consist
        // (so the new car runs the SAME way), else the player's view for a fresh train.
        Vec3 railDir = dirHint != null ? dirHint : view;

        // 2) Find the rail UNDER the cart first (gives us railY for the gather filter + the bogey location).
        TrackHit track = findRailBelow(level, origin, railDir);
        if (track == null) {
            msg(player, "no Create rail found under/near the cart");
            return null;
        }
        int railY = track.railY();

        // 3) Flood-fill the cart, but only blocks ABOVE the rail (reject tracks and anything at/below rail Y).
        GatherResult gather = SubLevelAssemblyHelper.gatherConnectedBlocks(origin, level, MAX_BLOCKS,
                (from, fromState, cand, candState, dir) ->
                        cand.getY() > railY && !(candState.getBlock() instanceof ITrackBlock));
        if (gather.assemblyState() != GatherResult.State.SUCCESS || gather.blocks() == null) {
            msg(player, "couldn't gather a cart above the rail (" + gather.assemblyState()
                    + ") — make sure cart blocks sit above the track");
            return null;
        }
        Set<BlockPos> blocks = gather.blocks();
        BoundingBox3i bounds = gather.boundingBox();

        // 3b) Require a Create bogey block in the cart — no bogey, no train.
        if (!hasBogey(level, bounds, railY)) {
            msg(player, "the cart needs a Create train bogey (the wheels) to be assembled into a train");
            return null;
        }

        // 4) Lift the cart into a sub-level and seat its rail body + per-bogey reference rails.
        SableTrain.Car car = seatCar(level, blocks, bounds, railDir, origin);
        if (car == null) {
            msg(player, "couldn't seat the rail body (no rail/graph under the cart?)");
            return null;
        }
        return car;
    }

    /**
     * Lifts a gathered set of cart blocks into a Sable sub-level and seats a {@link RailCarriage} body plus one
     * reference {@link RailCarriage} per Create bogey (used only to read the local rail tangent for the yaw
     * visual — never advanced/ticked). Returns {@code null} on any failure (no rail/graph/sub-level); does NOT
     * message the player, so both the raycast command path and the station path can surface errors their own way.
     *
     * @param preferredAnchor a block to anchor the sub-level on if present in {@code blocks} (else an arbitrary one)
     */
    private static SableTrain.Car seatCar(ServerLevel level, Set<BlockPos> blocks, BoundingBox3i bounds,
                                          Vec3 railDir, BlockPos preferredAnchor) {
        // Find the bogey blocks (Create train bogeys) WITHOUT removing them — they stay part of the body
        // sub-level and never move on their own. We only record where they are (WORLD space, converted to the
        // body's LOCAL frame after assembly) and seat a small reference RailCarriage under each.
        List<BlockPos> bogeyPositions = new ArrayList<>();
        for (BlockPos p : blocks) {
            if (level.getBlockState(p).getBlock() instanceof AbstractBogeyBlock) {
                bogeyPositions.add(p.immutable());
            }
        }
        // Order bogeys front-to-back along the travel direction, for consistency with previous behaviour.
        bogeyPositions.sort(java.util.Comparator.comparingDouble(p -> p.getX() * railDir.x + p.getZ() * railDir.z));

        // Body sub-level (the entire cluster, including its bogey blocks).
        BlockPos bodyAnchor = (preferredAnchor != null && blocks.contains(preferredAnchor))
                ? preferredAnchor : blocks.iterator().next();
        ServerSubLevel sub = SubLevelAssemblyHelper.assembleBlocks(level, bodyAnchor, blocks, bounds);
        if (sub == null) {
            return null;
        }

        // One reference RailCarriage per bogey, seated on the rail directly under its (former world) position.
        // localPos is the bogey's position relative to the body sub-level's anchor block, so the driver can
        // find the bogey's BlockEntity inside the sub-level each tick.
        List<SableTrain.Bogey> bogeys = new ArrayList<>();
        for (BlockPos bp : bogeyPositions) {
            TrackHit bogeyTrack = findRailBelow(level, bp, railDir);
            if (bogeyTrack == null) {
                LoconauticsConstants.LOGGER.warn(
                        "[sabletrain] seat: NO rail found under bogey at {} — this bogey will not follow the track", bp);
                continue; // no rail under this bogey — skip it (no yaw visual, but it still rides the body)
            }
            RailCarriage bogeyRail = RailCarriage.at(bogeyTrack.location(), bogeyTrack.upNormal(), 1.0, 0.0);
            if (bogeyRail == null) {
                continue;
            }
            // findRailBelow seats the follower at BLOCK resolution (the track block's graph point), which can
            // be up to a block away along the arc from where the bogey actually sits. Create's assembler seats
            // its TravellingPoints at the exact positions — mirror that: advance the follower by the along-track
            // distance to the bogey block's centre, so its rail point and tangent are sampled at the right spot.
            Vector3d seatC = bogeyRail.center();
            Vec3 seatFwd = bogeyRail.forward();
            double ds0 = 0.0;
            if (seatC != null) {
                ds0 = (bp.getX() + 0.5 - seatC.x) * seatFwd.x + (bp.getZ() + 0.5 - seatC.z) * seatFwd.z;
                if (Math.abs(ds0) > 1.0e-3) {
                    bogeyRail.setSpeed(ds0);
                    bogeyRail.tick();
                    bogeyRail.setSpeed(0.0);
                }
            }
            Vector3d seated = bogeyRail.center();
            LoconauticsConstants.LOGGER.info(
                    "[sabletrain] seat: bogey at {} aligned ds0={} -> follower {}", bp,
                    String.format("%.2f", ds0),
                    seated == null ? "null" : String.format("(%.1f, %.1f, %.1f)", seated.x, seated.y, seated.z));
            bogeys.add(new SableTrain.Bogey(bp.subtract(bodyAnchor), bogeyRail));
        }

        // Re-locate the rail under the body's horizontal CENTRE so the body lines up over its bogeys, falling
        // back to the anchor / a bogey position if the centre column has no rail.
        int cx = (bounds.minX() + bounds.maxX()) / 2;
        int cz = (bounds.minZ() + bounds.maxZ()) / 2;
        TrackHit track = findRailBelow(level, new BlockPos(cx, bounds.minY(), cz), railDir);
        if (track == null) {
            track = findRailBelow(level, bodyAnchor, railDir);
        }
        if (track == null) {
            for (BlockPos bp : bogeyPositions) {
                track = findRailBelow(level, bp, railDir);
                if (track != null) {
                    break;
                }
            }
        }
        if (track == null) {
            return null;
        }

        // Body pose: a RailCarriage spanning the body's longer horizontal extent (it follows the rail; its
        // orientation is the chord between its ends — so the body turns according to where its bogeys are).
        double spacing = Math.max(1.0, Math.max(bounds.maxX() - bounds.minX(), bounds.maxZ() - bounds.minZ()));
        RailCarriage carriage = RailCarriage.at(track.location(), track.upNormal(), spacing, 0.0);
        if (carriage == null) {
            return null;
        }
        // Capture the two bogey attachment points in the body sub-level's LOCAL frame (physics mode only).
        Vec3 leadW = carriage.leadingPos();
        Vec3 trailW = carriage.trailingPos();
        Vec3 localLeadVec = sub.logicalPose().transformPositionInverse(leadW);
        Vec3 localTrailVec = sub.logicalPose().transformPositionInverse(trailW);
        Vector3dc localLead = new Vector3d(localLeadVec.x, localLeadVec.y, localLeadVec.z);
        Vector3dc localTrail = new Vector3d(localTrailVec.x, localTrailVec.y, localTrailVec.z);

        LoconauticsConstants.LOGGER.info("[sabletrain] seated car (body {} blocks, {} loose bogeys, spacing {}) on graph {}",
                blocks.size(), bogeys.size(), spacing, track.location().graph.id);
        return new SableTrain.Car(sub.getUniqueId(), carriage, bogeys, localLead, localTrail);
    }

    /** Sets the target speed of every active custom train (live throttle for testing). Returns the count. */
    public static int setSpeedAll(double speed) {
        int n = 0;
        for (SableTrain train : SableTrainRegistry.all()) {
            train.setTargetSpeed(speed);
            n++;
        }
        return n;
    }

    /** Stops all custom trains AND removes their sub-levels cleanly (no orphan bodies / no packet spam). */
    public static int clear() {
        int n = 0;
        for (SableTrain train : SableTrainRegistry.all()) {
            ServerSubLevelContainer container = SubLevelContainer.getContainer(train.level());
            if (container != null) {
                SableTrain.Car car = train.car();
                if (car.subLevelId() != null
                        && container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub
                        && !sub.isRemoved()) {
                    // Bogeys live inside the body sub-level, so removing the body removes them too.
                    container.removeSubLevel(sub, SubLevelRemovalReason.REMOVED);
                }
            }
            SableTrainRegistry.remove(train.id());
            SableTrainPersistence.unpersist(train.level().getServer(), train.id()); // forget it on disk too
            SableTrainSyncPacket.broadcastRemoval(train.car().subLevelId()); // drop the client marker
            n++;
        }
        lastTrainId = null;
        return n;
    }

    // ============================================================================================
    // Station parking + disassembly (mirrors Create's station disassemble, for Sable trains).
    // ============================================================================================

    /**
     * The Sable train currently parked (stopped) at {@code station} in {@code level}, or {@code null} if none.
     * A train counts as parked when its station-stop state machine is {@link SableTrain.StationState#STOPPED} at
     * exactly this station (durable: the train holds {@code STOPPED} until the operator pulls away). Used both to
     * light up the station's disassemble button and to resolve which train that button disassembles.
     */
    public static SableTrain findParkedTrain(ServerLevel level, GlobalStation station) {
        if (station == null) {
            return null;
        }
        for (SableTrain train : SableTrainRegistry.all()) {
            if (train.level() != level || train.isDerailed()) {
                continue;
            }
            // Use the durable atStation marker (set at assembly and when the car parks, cleared when it rolls away)
            // rather than the transient, powered-only stationState — so an idle/un-crewed train still reads present.
            // Match by the station's stable graph id, not object identity: the GlobalStation our driver/assembler
            // captured can be a different instance from the one the station BE's edge point returns, though they are
            // the same station on the same graph (Create keys stations by this id everywhere).
            GlobalStation at = train.atStation();
            if (at != null && (at == station || (at.id != null && at.id.equals(station.id)))) {
                return train;
            }
        }
        return null;
    }

    /** Display name of a Sable train (its front car's sub-level name), or {@code ""} when unnamed/missing. */
    public static String trainName(ServerLevel level, SableTrain train) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        UUID subId = train == null ? null : train.car().subLevelId();
        if (container == null || subId == null
                || !(container.getSubLevel(subId) instanceof ServerSubLevel sub) || sub.isRemoved()) {
            return "";
        }
        String n = sub.getName();
        return n == null ? "" : n;
    }

    /**
     * Carriage-icon spans for the consist parked at {@code front}, front car first. Each value is the car's body
     * span (its {@link RailCarriage} bogeySpacing rounded to whole blocks), fed to Create's {@code TrainIconType}
     * so the station screen draws one carriage icon per coupled car; the front car's value is unused (it is drawn
     * as the engine). Coupled cars behind the front are ordered nearest-first, matching {@link #nameConsist}.
     */
    public static List<Integer> consistCarriageIcons(ServerLevel level, SableTrain front) {
        List<Integer> out = new ArrayList<>();
        if (front == null) {
            return out;
        }
        out.add(spanOf(front));

        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        UUID frontId = front.car().subLevelId();
        if (container == null || frontId == null
                || !(container.getSubLevel(frontId) instanceof ServerSubLevel frontSub) || frontSub.isRemoved()) {
            return out;
        }
        Vector3dc fp = frontSub.logicalPose().position();

        List<SableTrain> behind = new ArrayList<>();
        for (UUID id : TrainConsist.connectedSubLevels(level, frontId)) {
            if (id.equals(frontId)) {
                continue;
            }
            SableTrain t = SableTrainRegistry.bySubLevel(id);
            if (t != null && container.getSubLevel(id) instanceof ServerSubLevel s && !s.isRemoved()) {
                behind.add(t);
            }
        }
        behind.sort(java.util.Comparator.comparingDouble(t -> {
            UUID id = t.car().subLevelId();
            if (id != null && container.getSubLevel(id) instanceof ServerSubLevel s && !s.isRemoved()) {
                Vector3dc p = s.logicalPose().position();
                double dx = p.x() - fp.x();
                double dz = p.z() - fp.z();
                return dx * dx + dz * dz;
            }
            return Double.MAX_VALUE;
        }));
        for (SableTrain t : behind) {
            out.add(spanOf(t));
        }
        return out;
    }

    /** A car's carriage-icon span: its rail body length in whole blocks (>= 1). */
    private static int spanOf(SableTrain train) {
        return (int) Math.max(1, Math.round(train.car().carriage().bogeySpacing()));
    }

    /** Server entry for the station train-name box: names the whole consist parked at {@code stationPos}. */
    public static void setTrainNameFromStation(ServerPlayer player, BlockPos stationPos, String name) {
        ServerLevel level = player.serverLevel();
        if (!(level.getBlockEntity(stationPos) instanceof StationBlockEntity station)) {
            return;
        }
        SableTrain front = findParkedTrain(level, station.getStation());
        if (front != null) {
            nameConsist(level, front, name == null ? "" : name);
        }
    }

    /**
     * Names the whole coupled consist: the frontmost car ({@code front}) gets {@code base}, and every car coupled
     * behind it gets {@code "<base> Carriage N"} (N from 1, ordered nearest-to-front first). The name is written onto
     * each Sable sub-level via {@link ServerSubLevel#setName}, which persists it (Sable saves {@code display_name})
     * and syncs it to clients, so the Simulated nameplate reflects it too.
     */
    public static void nameConsist(ServerLevel level, SableTrain front, String base) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return;
        }
        UUID frontId = front.car().subLevelId();
        if (frontId == null
                || !(container.getSubLevel(frontId) instanceof ServerSubLevel frontSub) || frontSub.isRemoved()) {
            return;
        }
        Vector3dc fp = frontSub.logicalPose().position();

        // Coupled cars behind the front, ordered by distance from the front car (nearest first = Carriage 1).
        List<ServerSubLevel> behind = new ArrayList<>();
        for (UUID id : TrainConsist.connectedSubLevels(level, frontId)) {
            if (id.equals(frontId)) {
                continue;
            }
            if (container.getSubLevel(id) instanceof ServerSubLevel s && !s.isRemoved()) {
                behind.add(s);
            }
        }
        behind.sort(java.util.Comparator.comparingDouble(s -> {
            Vector3dc p = s.logicalPose().position();
            double dx = p.x() - fp.x();
            double dz = p.z() - fp.z();
            return dx * dx + dz * dz;
        }));

        frontSub.setName(base);
        int n = 1;
        for (ServerSubLevel s : behind) {
            s.setName(base + " Carriage " + n);
            n++;
        }
        LoconauticsConstants.LOGGER.info("[sabletrain] named consist '{}' ({} car(s)) from station", base,
                behind.size() + 1);
    }

    /** How far along the rail (blocks) from the station marker a Sable car counts as "in the station's detection
     *  range" for the disassemble button — the same order as the station-stop approach window. */
    private static final double STATION_DISASSEMBLE_RANGE = 32.0;

    /** Extra tolerance (blocks) past the station marker on the far side: a frontmost carriage that overshoots the
     *  marker a little still counts as in range, despite sitting on the station's non-platform side. */
    private static final double STATION_DISASSEMBLE_OVERSHOOT = 2.0;

    /**
     * Server entry for the station disassemble button: disassembles <b>every</b> Sable train sitting on the same
     * track within the station's detection range (not just the single car that drove in and parked), so a whole
     * parked consist or several cars stopped at the platform all come apart in one click. "In range" reuses the
     * station-stop detector's own rail scout ({@link SableTrainDriver#isStationInRange}), which inherently filters
     * to the station's own track graph and the platform-side approach.
     */
    public static void disassembleFromStation(ServerPlayer player, BlockPos stationPos) {
        ServerLevel level = player.serverLevel();
        if (!(level.getBlockEntity(stationPos) instanceof StationBlockEntity station)) {
            return;
        }
        GlobalStation global = station.getStation();
        if (global == null) {
            return;
        }
        int disassembled = 0;
        // Snapshot the registry first: disassembleSableTrain mutates it as it removes each car.
        for (SableTrain train : new ArrayList<>(SableTrainRegistry.all())) {
            if (train.level() != level || train.isDerailed()) {
                continue;
            }
            // The frontmost car parks ~1.5 blocks PAST the marker (the driver's STATION_FRONT_OFFSET back-shift),
            // i.e. on the station's far side where the platform-side scout is unreliable — but it carries the durable
            // atStation marker (the same flag that lit this button), so match that directly. Cars coupled/queued
            // behind it sit on the platform side and are caught by the along-rail scout.
            if ((isParkedAt(train, global)
                    || SableTrainDriver.isStationInRange(train, global, STATION_DISASSEMBLE_RANGE,
                            STATION_DISASSEMBLE_OVERSHOOT))
                    && disassembleSableTrain(train)) {
                disassembled++;
            }
        }
        LoconauticsConstants.LOGGER.info("[sabletrain] station at {} disassembled {} Sable car(s) in detection range",
                stationPos, disassembled);
    }

    /** True if {@code train} holds the durable "present at this station" marker (matched by stable graph id, like
     *  {@link #findParkedTrain}); the frontmost parked car that lit the disassemble button. */
    private static boolean isParkedAt(SableTrain train, GlobalStation station) {
        GlobalStation at = train.atStation();
        return at != null && (at == station || (at.id != null && at.id.equals(station.id)));
    }

    /**
     * Disassembles a Sable train back into the world, reusing Simulated's physics-assembler disassembly path
     * ({@link SimAssemblyHelper#disassembleSubLevel}) so the blocks (and any glue / Create contraptions inside the
     * sub-level) are written back to the world exactly as the Physics Assembler does it. A railed, {@code STOPPED}
     * train is already grid-aligned by the driver's rail constraint, so we skip the assembler's free-body alignment
     * dance and only replicate the bits that call for: anchor a sub-level block, find where the live pose maps it
     * into the world, and derive the 90° grid rotation from the (already rail-aligned) yaw.
     */
    public static boolean disassembleSableTrain(SableTrain train) {
        ServerLevel level = train.level();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        UUID subId = train.car().subLevelId();
        if (container == null || subId == null
                || !(container.getSubLevel(subId) instanceof ServerSubLevel sub) || sub.isRemoved()) {
            return false;
        }

        BlockPos anchor = sub.getPlot().getCenterBlock();
        BlockPos goal = BlockPos.containing(sub.logicalPose().transformPosition(Vec3.atCenterOf(anchor)));
        double yaw = SimMathUtils.getClosestYaw(sub.logicalPose().orientation());
        int turns = -Mth.floor(yaw / (Math.PI / 2.0) + 0.5);
        Rotation rotation = SimAssemblyHelper.rotationFrom90DegRots(turns);

        SimAssemblyHelper.disassembleSubLevel(level, sub, anchor, goal, rotation, true);

        // Stop driving it and forget it everywhere, then drop the now-empty sub-level husk.
        SableTrainRegistry.remove(train.id());
        SableTrainPersistence.unpersist(level.getServer(), train.id());
        SableTrainSyncPacket.broadcastRemoval(subId);
        if (!sub.isRemoved()) {
            container.removeSubLevel(sub, SubLevelRemovalReason.REMOVED);
        }
        LoconauticsConstants.LOGGER.info("[sabletrain] disassembled train {} (sub-level {}) back into the world at {}",
                train.id(), subId, goal);
        return true;
    }

    /** How close the player's look ray must pass to a car's centre to target it for {@link #derailLookedAt}. */
    private static final double DERAIL_RAY_RADIUS = 2.5;

    /**
     * Derails the Sable car the player is looking at: finds the car whose body sub-level centre lies nearest the
     * player's view ray (within {@link #REACH}) and flags it {@linkplain SableTrain#setDerailed derailed}, so the
     * driver releases its rail constraint and it becomes a free physics body. Backs the {@code /loconautics derail}
     * debug command.
     */
    public static int derailLookedAt(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            msg(player, "no Sable sub-levels in this dimension");
            return 0;
        }
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);

        SableTrain best = null;
        double bestPerp = DERAIL_RAY_RADIUS;
        for (SableTrain train : SableTrainRegistry.all()) {
            if (train.level() != level || train.car().subLevelId() == null) {
                continue;
            }
            if (!(container.getSubLevel(train.car().subLevelId()) instanceof ServerSubLevel sub) || sub.isRemoved()) {
                continue;
            }
            Vector3dc p = sub.logicalPose().position();
            Vec3 centre = new Vec3(p.x(), p.y(), p.z());
            double along = centre.subtract(eye).dot(look); // distance of the car's centre projected onto the ray
            if (along < 0.0 || along > REACH) {
                continue;
            }
            double perp = eye.add(look.scale(along)).distanceTo(centre); // how far the ray passes from the centre
            if (perp < bestPerp) {
                best = train;
                bestPerp = perp;
            }
        }

        if (best == null) {
            msg(player, "look at a Sable cart within " + (int) REACH + " blocks to derail it");
            return 0;
        }
        if (best.isDerailed()) {
            msg(player, "that cart is already off the rails");
            return 0;
        }
        best.setDerailed(true);
        SableTrainPersistence.persist(best); // remember the derailed state across restarts
        SableTrainSyncPacket.broadcast(best); // update the client marker's derail state (gates derailedOnly relocation)
        msg(player, "derailed cart " + best.id().toString().substring(0, 8) + ", released from the rail");
        return 1;
    }

    /**
     * Server authority for wrench relocation: re-seats the train sub-level {@code subLevelId} onto the Create
     * track at {@code pos}. Re-validates everything Create's {@code TrainRelocationPacket.handle} does (feature
     * enabled, the train still exists, the {@code derailedOnly} gate, player reach, the rail resolves) before
     * rebuilding the {@link RailCarriage} at the new location and snapping the body onto it. The existing
     * {@link SableTrainDriver} then re-pins/re-glues the body to the new rail from the next physics tick, so no
     * post-teleport collision handling (Create's {@code nonDamageTicks}) is needed — there is no contraption entity.
     */
    public static void relocate(ServerPlayer player, UUID subLevelId, BlockPos pos, Vec3 lookAngle,
                                boolean bezierDirection) {
        if (!Config.WRENCH_RELOCATION_ENABLED.get()) {
            return;
        }
        ServerLevel level = player.serverLevel();
        SableTrain train = SableTrainRegistry.bySubLevel(subLevelId);
        if (train == null || train.level() != level) {
            return;
        }
        if (Config.WRENCH_RELOCATION_DERAILED_ONLY.get() && !train.isDerailed()) {
            return; // config restricts relocation to derailed trains
        }
        if (!player.canInteractWithBlock(pos, 24.0)) {
            return; // anti-cheat: the player must actually be able to reach the target rail
        }

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ITrackBlock track)) {
            return;
        }
        Pair<Vec3, Direction.AxisDirection> axis = track.getNearestTrackAxis(level, pos, state, lookAngle);
        TrackGraphLocation loc = TrackGraphHelper.getGraphLocationAt(level, pos, axis.getSecond(), axis.getFirst());
        if (loc == null || loc.graph == null) {
            return;
        }
        Vec3 upNormal = track.getUpNormal(level, pos, state);

        double spacing = train.car().carriage().bogeySpacing();
        RailCarriage carriage = RailCarriage.at(loc, upNormal, spacing, 0.0);
        if (carriage == null) {
            return;
        }
        // CRITICAL: keep the ORIGINAL assembly reference frame. A fresh carriage captures its reference from
        // the new rail's tangent, so orientation() would stop mapping the axis the car's blocks were actually
        // built along — leaving the body permanently misaligned with the track (it "stops following the rail")
        // whenever the target rail runs along a different axis than the original assembly.
        carriage.adoptReferenceFrame(train.car().carriage());

        // Snap the body sub-level onto the new rail FIRST, so each bogey block's world position on the new
        // rail is known and its follower can be seated at its own exact arc position (mirrors assembly).
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        ServerSubLevel sub = container != null
                && container.getSubLevel(subLevelId) instanceof ServerSubLevel s && !s.isRemoved() ? s : null;
        if (sub != null) {
            Vector3d center = carriage.center();
            if (center != null) {
                sub.logicalPose().position().set(center);
                sub.logicalPose().orientation().set(carriage.orientation());
                sub.updateLastPose();
            }
        }

        // Re-seat each bogey's follower EXACTLY like first assembly does: find the track UNDER the bogey's
        // own (snapped) position and align to its block centre — never seed every follower from the clicked
        // block, whose arc estimate degrades with distance/curves and leaves the rear bogey turning late.
        List<SableTrain.Bogey> bogeys = new ArrayList<>();
        Vec3 railDir = carriage.forward(); // travel axis at the destination (better hint than the look ray)
        int bogeyIdx = 0;
        for (SableTrain.Bogey bogey : train.car().bogeys()) {
            RailCarriage bogeyRail = null;
            Vector3d w = null;
            boolean ownRail = false;
            if (sub != null) {
                BlockPos bogeyWorld = sub.getPlot().getCenterBlock().offset(bogey.localPos());
                w = sub.logicalPose().transformPosition(
                        new Vector3d(bogeyWorld.getX() + 0.5, bogeyWorld.getY() + 0.5, bogeyWorld.getZ() + 0.5),
                        new Vector3d());
                TrackHit hit = findRailBelow(level, BlockPos.containing(w.x, w.y, w.z), railDir);
                if (hit != null) {
                    bogeyRail = RailCarriage.at(hit.location(), hit.upNormal(), 1.0, 0.0);
                    ownRail = bogeyRail != null;
                }
            }
            if (bogeyRail == null) {
                bogeyRail = RailCarriage.at(loc, upNormal, 1.0, 0.0); // fallback: seed at the clicked block
                if (bogeyRail == null) {
                    continue;
                }
            }
            // Align the follower's arc position to the bogey block's snapped centre (both paths).
            if (w != null) {
                Vector3d c = bogeyRail.center();
                Vec3 fwd = bogeyRail.forward();
                if (c != null) {
                    double ds0 = (w.x - c.x) * fwd.x + (w.z - c.z) * fwd.z;
                    if (Math.abs(ds0) > 1.0e-3) {
                        bogeyRail.setSpeed(ds0);
                        bogeyRail.tick();
                        bogeyRail.setSpeed(0.0);
                    }
                }
            }
            Vector3d seated = bogeyRail.center();
            LoconauticsConstants.LOGGER.info(
                    "[sabletrain] relocate: bogey[{}] ownRail={} target={} -> follower={}",
                    bogeyIdx, ownRail,
                    w == null ? "null" : String.format("(%.1f, %.1f, %.1f)", w.x, w.y, w.z),
                    seated == null ? "null" : String.format("(%.1f, %.1f, %.1f)", seated.x, seated.y, seated.z));
            bogeys.add(new SableTrain.Bogey(bogey.localPos(), bogeyRail));
            bogeyIdx++;
        }

        train.relocate(carriage, bogeys);
        train.setDerailed(false); // back on the rails (mirrors Create's train.derailed = false on relocate)

        SableTrainPersistence.persist(train);
        SableTrainSyncPacket.broadcast(train); // marker is no longer derailed
        LoconauticsConstants.LOGGER.info("[sabletrain] relocated train {} (sub-level {}) to {}",
                train.id(), subLevelId, pos);
    }

    /**
     * Re-seats the train's followers on the LIVE track graph at the car's current position — used when the
     * graph changed underneath it (a rail was broken or the network was rebuilt/split) and the followers'
     * edges no longer exist. Keeps the body pose and speed untouched: the car simply continues riding the
     * (possibly shortened) track from where it is, so a break further ahead is met at the new track end by
     * the normal end-of-track rule. Returns {@code false} when there is no rail under the car/bogeys any
     * more — the caller should derail it.
     */
    public static boolean reseatOnRail(SableTrain train, ServerSubLevel sub) {
        ServerLevel level = train.level();
        Vec3 fwd = train.car().carriage().forward();
        Vector3dc p = sub.logicalPose().position();

        TrackHit bodyHit = findRailBelow(level, BlockPos.containing(p.x(), p.y(), p.z()), fwd);
        if (bodyHit == null) {
            return false;
        }
        double spacing = train.car().carriage().bogeySpacing();
        RailCarriage carriage = RailCarriage.at(bodyHit.location(), bodyHit.upNormal(), spacing, 0.0);
        if (carriage == null) {
            return false;
        }
        carriage.adoptReferenceFrame(train.car().carriage());
        // Align the carriage's arc position to the body's actual centre (block-coarse seating otherwise).
        Vector3d c = carriage.center();
        Vec3 cf = carriage.forward();
        if (c != null) {
            double ds0 = (p.x() - c.x) * cf.x + (p.z() - c.z) * cf.z;
            if (Math.abs(ds0) > 1.0e-3) {
                carriage.setSpeed(ds0);
                carriage.tick();
                carriage.setSpeed(0.0);
            }
        }

        // Each bogey follower re-seats at its own spot; a bogey with no rail below means the track is gone.
        List<SableTrain.Bogey> bogeys = new ArrayList<>();
        for (SableTrain.Bogey bogey : train.car().bogeys()) {
            BlockPos bogeyWorld = sub.getPlot().getCenterBlock().offset(bogey.localPos());
            Vector3d w = sub.logicalPose().transformPosition(
                    new Vector3d(bogeyWorld.getX() + 0.5, bogeyWorld.getY() + 0.5, bogeyWorld.getZ() + 0.5),
                    new Vector3d());
            TrackHit hit = findRailBelow(level, BlockPos.containing(w.x, w.y, w.z), fwd);
            if (hit == null) {
                return false;
            }
            RailCarriage rail = RailCarriage.at(hit.location(), hit.upNormal(), 1.0, 0.0);
            if (rail == null) {
                return false;
            }
            Vector3d rc = rail.center();
            Vec3 rf = rail.forward();
            if (rc != null) {
                double ds0 = (w.x - rc.x) * rf.x + (w.z - rc.z) * rf.z;
                if (Math.abs(ds0) > 1.0e-3) {
                    rail.setSpeed(ds0);
                    rail.tick();
                    rail.setSpeed(0.0);
                }
            }
            bogeys.add(new SableTrain.Bogey(bogey.localPos(), rail));
        }

        double keepSpeed = train.speed();
        train.relocate(carriage, bogeys);
        train.setSpeed(keepSpeed); // mid-run re-seat: the car keeps rolling (relocate() zeroes it)
        LoconauticsConstants.LOGGER.info(
                "[sabletrain] re-seated train {} on the live track graph (rail broken/rebuilt nearby)", train.id());
        return true;
    }

    /** True if a Create train bogey block sits in the cart's footprint (scanned down to the rail level). */
    private static boolean hasBogey(ServerLevel level, BoundingBox3i bounds, int railY) {
        int minY = Math.min(railY, bounds.minY());
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int y = minY; y <= bounds.maxY(); y++) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    if (level.getBlockState(pos.set(x, y, z)).getBlock() instanceof AbstractBogeyBlock) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Scans a small box below/around {@code origin} for the nearest Create track, returning a graph location
     * and the rail's Y. Lets the player look at any cart block, not necessarily the one directly over the rail.
     */
    private static TrackHit findRailBelow(ServerLevel level, BlockPos origin, Vec3 look) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dy = 0; dy <= RAIL_SEARCH_DOWN; dy++) {
            for (int dx = -RAIL_SEARCH_RADIUS; dx <= RAIL_SEARCH_RADIUS; dx++) {
                for (int dz = -RAIL_SEARCH_RADIUS; dz <= RAIL_SEARCH_RADIUS; dz++) {
                    pos.set(origin.getX() + dx, origin.getY() - dy, origin.getZ() + dz);
                    BlockState state = level.getBlockState(pos);
                    if (!(state.getBlock() instanceof ITrackBlock track)) {
                        continue;
                    }
                    Pair<Vec3, Direction.AxisDirection> axis = track.getNearestTrackAxis(level, pos, state, look);
                    TrackGraphLocation loc = TrackGraphHelper.getGraphLocationAt(
                            level, pos.immutable(), axis.getSecond(), axis.getFirst());
                    if (loc != null && loc.graph != null) {
                        return new TrackHit(loc, track.getUpNormal(level, pos, state), pos.getY());
                    }
                }
            }
        }
        return null;
    }

    /** Records {@code id} as the most-recently-touched cart (used as the default throttle target, etc.). */
    static void noteRestored(UUID id) {
        lastTrainId = id;
    }

    /** Attach (once per container) an observer that drops a cart when its body sub-level is destroyed. Public so
     *  the restore path can re-attach it for carts rebuilt after a restart. */
    public static void ensureObserver(ServerLevel level) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null || OBSERVED.contains(container)) {
            return;
        }
        container.addObserver(new SubLevelObserver() {
            @Override
            public void onSubLevelRemoved(SubLevel subLevel, SubLevelRemovalReason reason) {
                // Only a genuine REMOVED (destroyed/disassembled) drops the cart. UNLOADED means the sub-level is
                // just being unloaded (chunk unload / server stop) and will come back — keep the cart AND its
                // saved record, or every shutdown would wipe persistence.
                if (reason != SubLevelRemovalReason.REMOVED) {
                    return;
                }
                UUID removedId = subLevel.getUniqueId();
                for (SableTrain train : SableTrainRegistry.all()) {
                    if (removedId.equals(train.car().subLevelId())) {
                        SableTrainRegistry.remove(train.id());
                        SableTrainPersistence.unpersist(train.level().getServer(), train.id());
                        SableTrainSyncPacket.broadcastRemoval(removedId); // drop the client marker
                    }
                }
            }
        });
        OBSERVED.add(container);
    }

    private static void msg(ServerPlayer player, String text) {
        player.sendSystemMessage(Component.literal("[sabletrain] " + text));
    }
}