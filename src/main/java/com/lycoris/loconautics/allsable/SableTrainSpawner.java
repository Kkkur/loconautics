package com.lycoris.loconautics.allsable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.lycoris.loconautics.core.LoconauticsConstants;

import com.simibubi.create.content.trains.graph.TrackGraphHelper;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper.GatherResult;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;

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
 * Debug spawner for the all-Sable custom train (Option B, layer 1 / Phase 2b keystone).
 *
 * <p>The player builds a small cart structure sitting on a Create rail and looks at it. We flood-fill the
 * connected blocks (excluding the rail), lift them into a Sable sub-level ({@link SubLevelAssemblyHelper}),
 * find the rail under the cart, seat a {@link RailCarriage} there, and register a {@link SableTrain} driven by
 * {@link SableTrainDriver}. The result is a physical body that rides the rail and carries the player — with no
 * {@code CarriageContraptionEntity} and no Create {@code Train}.
 *
 * <p>This is intentionally rough (fixed bogey spacing, no disassembly on clear) — it exists to validate the
 * moving-sub-level keystone before assembly integration. Iterate from here.
 */
public final class SableTrainSpawner {

    private static final double REACH = 12.0;
    private static final int MAX_BLOCKS = 2048;
    private static final double BOGEY_SPACING = 2.0;
    private static final int TRACK_SCAN_BELOW = 3;

    private SableTrainSpawner() {
    }

    /** A rail location under the cart, plus the track's up-normal for the bogeys. */
    private record TrackHit(TrackGraphLocation location, Vec3 upNormal) {}

    /**
     * Spawns a custom train from the cart the player is looking at, moving at {@code speed} blocks/tick.
     * Returns 1 on success, 0 on a (messaged) failure.
     */
    public static int spawn(ServerPlayer player, double speed) {
        ServerLevel level = player.serverLevel();

        // 1) Raycast to the cart block the player is looking at.
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eye.add(look.scale(REACH));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() == HitResult.Type.MISS) {
            msg(player, "look at the cart you built (sitting on a rail), within " + (int) REACH + " blocks");
            return 0;
        }
        BlockPos origin = hit.getBlockPos();
        if (level.getBlockState(origin).getBlock() instanceof ITrackBlock) {
            msg(player, "look at the CART, not the rail itself");
            return 0;
        }

        // 2) Flood-fill the connected structure, refusing to cross into track blocks (don't rip up the rail).
        GatherResult gather = SubLevelAssemblyHelper.gatherConnectedBlocks(origin, level, MAX_BLOCKS,
                (from, fromState, cand, candState, dir) -> !(candState.getBlock() instanceof ITrackBlock));
        if (gather.assemblyState() != GatherResult.State.SUCCESS || gather.blocks() == null) {
            msg(player, "couldn't gather a cart from there (" + gather.assemblyState() + ")");
            return 0;
        }
        Set<BlockPos> blocks = gather.blocks();
        BoundingBox3i bounds = gather.boundingBox();

        // 3) Find the rail under the cart for the bogeys (before assembling, while the cart is still in world).
        TrackHit track = findTrackNear(level, bounds, look);
        if (track == null) {
            msg(player, "no Create rail found under the cart (place it on a track)");
            return 0;
        }

        // 4) Lift the cart blocks into a Sable sub-level (this MOVES them out of the world).
        ServerSubLevel sub = SubLevelAssemblyHelper.assembleBlocks(level, origin, blocks, bounds);
        if (sub == null) {
            msg(player, "sub-level creation failed");
            return 0;
        }

        // 5) Seat a rail body and register the custom train.
        RailCarriage carriage = RailCarriage.at(track.location(), track.upNormal(), BOGEY_SPACING, 0.0);
        if (carriage == null) {
            msg(player, "couldn't seat the rail body (track not in a graph?)");
            return 0;
        }
        UUID id = UUID.randomUUID();
        SableTrain train = new SableTrain(id, level, List.of(new SableTrain.Car(sub.getUniqueId(), carriage)));
        train.setTargetSpeed(speed);
        SableTrainRegistry.register(train);

        msg(player, String.format("custom train spawned (%d blocks) speed=%.2f — id %s", blocks.size(), speed,
                id.toString().substring(0, 8)));
        LoconauticsConstants.LOGGER.info("[sabletrain] spawned {} ({} blocks) on graph {} speed={}",
                id, blocks.size(), track.location().graph.id, speed);
        return 1;
    }

    /** Stops driving all custom trains. NOTE: layer-1 leaves the sub-levels behind (no disassembly yet). */
    public static int clear() {
        int n = SableTrainRegistry.all().size();
        SableTrainRegistry.clear();
        return n;
    }

    /**
     * Scans the cart's bounding box (and a few blocks below it) for the nearest Create track block, then
     * resolves it to a graph location using the player's look direction to choose the travel axis.
     */
    private static TrackHit findTrackNear(ServerLevel level, BoundingBox3i bounds, Vec3 look) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = bounds.maxY(); y >= bounds.minY() - TRACK_SCAN_BELOW; y--) {
            for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    pos.set(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (!(state.getBlock() instanceof ITrackBlock track)) {
                        continue;
                    }
                    Pair<Vec3, Direction.AxisDirection> axis = track.getNearestTrackAxis(level, pos, state, look);
                    TrackGraphLocation loc = TrackGraphHelper.getGraphLocationAt(
                            level, pos.immutable(), axis.getSecond(), axis.getFirst());
                    if (loc != null && loc.graph != null) {
                        return new TrackHit(loc, track.getUpNormal(level, pos, state));
                    }
                }
            }
        }
        return null;
    }

    private static void msg(ServerPlayer player, String text) {
        player.sendSystemMessage(Component.literal("[sabletrain] " + text));
    }
}
