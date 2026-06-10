package com.lycoris.loconautics.allsable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.lycoris.loconautics.core.LoconauticsConstants;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.graph.TrackGraphHelper;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;

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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
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
        ServerLevel level = player.serverLevel();
        SableTrain.Car car = buildCar(player, level, null);
        if (car == null) {
            return 0;
        }
        UUID id = UUID.randomUUID();
        SableTrain train = new SableTrain(id, level, List.of(car), physics);
        train.setTargetSpeed(speed);
        SableTrainRegistry.register(train);
        ensureObserver(level);
        lastTrainId = id;
        SableTrainPersistence.persist(train); // survive restarts
        msg(player, String.format("%s train spawned speed=%.2f — id %s — add cars with /loconautics sabletrain addcar",
                physics ? "PHYSICS" : "pinned", speed, id.toString().substring(0, 8)));
        return 1;
    }

    /** Appends the looked-at cart to the most recently spawned train as a new (articulated) car. */
    public static int addCar(ServerPlayer player) {
        if (lastTrainId == null || SableTrainRegistry.get(lastTrainId) == null) {
            msg(player, "no train to add to — spawn one first with /loconautics sabletrain");
            return 0;
        }
        SableTrain train = SableTrainRegistry.get(lastTrainId);
        // Resolve the new car's travel direction from the train's forward (front car), so it runs the SAME
        // way as the rest of the convoy instead of flipping based on where the player happened to look.
        Vec3 trainForward = train.cars().isEmpty() ? null : train.cars().get(0).carriage().forward();
        SableTrain.Car car = buildCar(player, train.level(), trainForward);
        if (car == null) {
            return 0;
        }
        train.cars().add(car);
        SableTrainPersistence.persist(train); // re-snapshot the now-longer consist
        msg(player, "car added — train now has " + train.cars().size() + " cars (each pivots on its own bogeys)");
        return 1;
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

        // 4) Separate the LOOSE bogeys from the body. Each Create bogey block becomes its OWN sub-level that
        //    rides the rail and pivots to the local tangent; the body is the cart MINUS the bogeys. (Detect the
        //    bogeys NOW, while everything is still in the world.)
        List<BlockPos> bogeyPositions = new ArrayList<>();
        for (BlockPos p : blocks) {
            if (level.getBlockState(p).getBlock() instanceof AbstractBogeyBlock) {
                bogeyPositions.add(p.immutable());
            }
        }
        // Order bogeys front-to-back along the travel direction, so the body's chord (first->last bogey) points
        // the same way as the body's own reference and the cars stay consistent.
        bogeyPositions.sort(java.util.Comparator.comparingDouble(p -> p.getX() * railDir.x + p.getZ() * railDir.z));
        Set<BlockPos> bodyBlocks = new HashSet<>(blocks);
        bodyBlocks.removeAll(bogeyPositions);
        if (bodyBlocks.isEmpty()) { // degenerate cart (only bogeys): keep it whole, no separation
            bodyBlocks = blocks;
            bogeyPositions.clear();
        }
        BoundingBox3i bodyBounds = BoundingBox3i.from(bodyBlocks);

        // Body sub-level (cart minus bogeys). MOVES the body blocks out of the world; bogey blocks stay for now.
        BlockPos bodyAnchor = bodyBlocks.contains(origin) ? origin : bodyBlocks.iterator().next();
        ServerSubLevel sub = SubLevelAssemblyHelper.assembleBlocks(level, bodyAnchor, bodyBlocks, bodyBounds);
        if (sub == null) {
            msg(player, "sub-level creation failed");
            return null;
        }

        // One sub-level per bogey, each seated on the rail directly under it (so it rides + pivots on its own).
        List<SableTrain.Bogey> bogeys = new ArrayList<>();
        for (BlockPos bp : bogeyPositions) {
            TrackHit bogeyTrack = findRailBelow(level, bp, railDir);
            if (bogeyTrack == null) {
                continue; // no rail under this bogey — skip it
            }
            RailCarriage bogeyRail = RailCarriage.at(bogeyTrack.location(), bogeyTrack.upNormal(), 1.0, 0.0);
            if (bogeyRail == null) {
                continue;
            }
            BoundingBox3i bbBounds = new BoundingBox3i(bp.getX(), bp.getY(), bp.getZ(), bp.getX(), bp.getY(), bp.getZ());
            ServerSubLevel bogeySub = SubLevelAssemblyHelper.assembleBlocks(level, bp, List.of(bp), bbBounds);
            if (bogeySub != null) {
                bogeys.add(new SableTrain.Bogey(bogeySub.getUniqueId(), bogeyRail));
            }
        }

        // 5) Re-locate the rail under the BODY's horizontal CENTRE so the body lines up over its bogeys.
        int cx = (bodyBounds.minX() + bodyBounds.maxX()) / 2;
        int cz = (bodyBounds.minZ() + bodyBounds.maxZ()) / 2;
        TrackHit centreTrack = findRailBelow(level, new BlockPos(cx, bodyBounds.minY(), cz), railDir);
        if (centreTrack != null) {
            track = centreTrack;
        }

        // 6) Body pose: a RailCarriage spanning the body's longer horizontal extent (it follows the rail; its
        //    orientation is the chord between its ends — so the body turns according to where its bogeys are).
        double spacing = Math.max(1.0, Math.max(bodyBounds.maxX() - bodyBounds.minX(), bodyBounds.maxZ() - bodyBounds.minZ()));
        RailCarriage carriage = RailCarriage.at(track.location(), track.upNormal(), spacing, 0.0);
        if (carriage == null) {
            msg(player, "couldn't seat the rail body (track not in a graph?)");
            return null;
        }
        // Capture the two bogey attachment points in the body sub-level's LOCAL frame (physics mode only).
        Vec3 leadW = carriage.leadingPos();
        Vec3 trailW = carriage.trailingPos();
        Vec3 localLeadVec = sub.logicalPose().transformPositionInverse(leadW);
        Vec3 localTrailVec = sub.logicalPose().transformPositionInverse(trailW);
        Vector3dc localLead = new Vector3d(localLeadVec.x, localLeadVec.y, localLeadVec.z);
        Vector3dc localTrail = new Vector3d(localTrailVec.x, localTrailVec.y, localTrailVec.z);

        LoconauticsConstants.LOGGER.info("[sabletrain] built car (body {} blocks, {} loose bogeys, spacing {}) on graph {}",
                bodyBlocks.size(), bogeys.size(), spacing, track.location().graph.id);
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
                for (SableTrain.Car car : train.cars()) {
                    if (car.subLevelId() != null
                            && container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub
                            && !sub.isRemoved()) {
                        container.removeSubLevel(sub, SubLevelRemovalReason.REMOVED);
                    }
                    for (SableTrain.Bogey bogey : car.bogeys()) {
                        if (container.getSubLevel(bogey.subLevelId()) instanceof ServerSubLevel bsub && !bsub.isRemoved()) {
                            container.removeSubLevel(bsub, SubLevelRemovalReason.REMOVED);
                        }
                    }
                }
            }
            SableTrainRegistry.remove(train.id());
            SableTrainPersistence.unpersist(train.level().getServer(), train.id()); // forget it on disk too
            n++;
        }
        lastTrainId = null;
        return n;
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

    /** Records {@code id} as the most-recently-touched train (so {@code addcar} targets a restored consist). */
    static void noteRestored(UUID id) {
        lastTrainId = id;
    }

    /** Attach (once per container) an observer that drops only the affected CAR when its sub-level is removed
     *  (not the whole train — breaking one car must not drop the rest of the convoy). Public so the restore
     *  path can re-attach it for trains rebuilt after a restart. */
    public static void ensureObserver(ServerLevel level) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null || OBSERVED.contains(container)) {
            return;
        }
        container.addObserver(new SubLevelObserver() {
            @Override
            public void onSubLevelRemoved(SubLevel subLevel, SubLevelRemovalReason reason) {
                // Only a genuine REMOVED (destroyed/disassembled) drops the car. UNLOADED means the sub-level is
                // just being unloaded (chunk unload / server stop) and will come back — keep the train AND its
                // saved record, or every shutdown would wipe persistence.
                if (reason != SubLevelRemovalReason.REMOVED) {
                    return;
                }
                UUID removedId = subLevel.getUniqueId();
                for (SableTrain train : SableTrainRegistry.all()) {
                    boolean removed = train.cars().removeIf(car -> removedId.equals(car.subLevelId()));
                    if (!removed) {
                        continue;
                    }
                    if (train.cars().isEmpty()) {
                        SableTrainRegistry.remove(train.id()); // only drop the train once it has no cars left
                        SableTrainPersistence.unpersist(train.level().getServer(), train.id());
                    } else {
                        SableTrainPersistence.persist(train); // re-snapshot the shortened consist
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
