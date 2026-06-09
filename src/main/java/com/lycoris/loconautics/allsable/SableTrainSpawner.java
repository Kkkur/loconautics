package com.lycoris.loconautics.allsable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.lycoris.loconautics.core.LoconauticsConstants;

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

    public static int spawn(ServerPlayer player, double speed) {
        ServerLevel level = player.serverLevel();

        // 1) Raycast to the cart block the player is looking at.
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eye.add(look.scale(REACH));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() == HitResult.Type.MISS) {
            msg(player, "look at the cart you built (on a rail), within " + (int) REACH + " blocks");
            return 0;
        }
        BlockPos origin = hit.getBlockPos();
        if (level.getBlockState(origin).getBlock() instanceof ITrackBlock) {
            msg(player, "look at the CART, not the rail itself");
            return 0;
        }

        // 2) Find the rail UNDER the cart first (gives us railY for the gather filter + the bogey location).
        TrackHit track = findRailBelow(level, origin, look);
        if (track == null) {
            msg(player, "no Create rail found under/near the cart");
            return 0;
        }
        int railY = track.railY();

        // 3) Flood-fill the cart, but only blocks ABOVE the rail (never descend to the ground): reject tracks
        //    and anything at or below the rail's Y. This is why the cart no longer needs the ground cleared.
        GatherResult gather = SubLevelAssemblyHelper.gatherConnectedBlocks(origin, level, MAX_BLOCKS,
                (from, fromState, cand, candState, dir) ->
                        cand.getY() > railY && !(candState.getBlock() instanceof ITrackBlock));
        if (gather.assemblyState() != GatherResult.State.SUCCESS || gather.blocks() == null) {
            msg(player, "couldn't gather a cart above the rail (" + gather.assemblyState()
                    + ") — make sure cart blocks sit above the track");
            return 0;
        }
        Set<BlockPos> blocks = gather.blocks();
        BoundingBox3i bounds = gather.boundingBox();

        // 4) Lift the cart into a sub-level (MOVES the blocks out of the world).
        ServerSubLevel sub = SubLevelAssemblyHelper.assembleBlocks(level, origin, blocks, bounds);
        if (sub == null) {
            msg(player, "sub-level creation failed");
            return 0;
        }

        // 5) Bogey spacing from the cart's longer horizontal extent (so the two bogeys sit near its ends).
        double spacing = Math.max(1.0, Math.max(bounds.maxX() - bounds.minX(), bounds.maxZ() - bounds.minZ()));
        RailCarriage carriage = RailCarriage.at(track.location(), track.upNormal(), spacing, 0.0);
        if (carriage == null) {
            msg(player, "couldn't seat the rail body (track not in a graph?)");
            return 0;
        }

        UUID id = UUID.randomUUID();
        SableTrain train = new SableTrain(id, level, List.of(new SableTrain.Car(sub.getUniqueId(), carriage)));
        train.setTargetSpeed(speed);
        SableTrainRegistry.register(train);
        ensureObserver(level);

        msg(player, String.format("custom train spawned (%d blocks, spacing %.0f) speed=%.2f — id %s",
                blocks.size(), spacing, speed, id.toString().substring(0, 8)));
        LoconauticsConstants.LOGGER.info("[sabletrain] spawned {} ({} blocks, spacing {}) graph {} speed={}",
                id, blocks.size(), spacing, track.location().graph.id, speed);
        return 1;
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
                }
            }
            SableTrainRegistry.remove(train.id());
            n++;
        }
        return n;
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

    /** Attach (once per container) an observer that drops any train whose car sub-level gets removed. */
    private static void ensureObserver(ServerLevel level) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null || OBSERVED.contains(container)) {
            return;
        }
        container.addObserver(new SubLevelObserver() {
            @Override
            public void onSubLevelRemoved(SubLevel subLevel, SubLevelRemovalReason reason) {
                UUID removedId = subLevel.getUniqueId();
                for (SableTrain train : SableTrainRegistry.all()) {
                    for (SableTrain.Car car : train.cars()) {
                        if (removedId.equals(car.subLevelId())) {
                            SableTrainRegistry.remove(train.id());
                            break;
                        }
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
