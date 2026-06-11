package com.lycoris.loconautics.allsable;

import java.util.ArrayList;
import java.util.List;

import com.lycoris.loconautics.core.LoconauticsConstants;

import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;

import org.joml.Quaterniond;
import org.joml.Vector3d;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Disassembles a parked sable train back into world blocks — the reverse of
 * {@link SableTrainSpawner#assembleFromStation}, playing the role of Create's {@code Train.disassemble}:
 * the body sub-level's blocks are written back into the world grid where the train is standing (orientation
 * snapped to the nearest 90°), one super-glue entity is left spanning the placed structure so it can be
 * re-assembled, and the sub-level is removed (which unregisters the train via the removal observer).
 *
 * <p>Triggered from the station screen's disassemble button (via {@code DisassembleSableTrainPacket}) when the
 * {@code StationBlockEntityMixin} has put the station into parked mode for a sable train.
 */
public final class SableTrainDisassembler {

    /** Max angle (degrees) the parked body may deviate from the snapped grid orientation (yaw) / level (roll). */
    private static final double ALIGN_TOLERANCE_DEG = 10.0;

    private SableTrainDisassembler() {
    }

    /**
     * Disassembles the sable train parked at this station. Mirrors {@code StationBlockEntity.tryDisassembleTrain}:
     * resolves the present train, refuses (with a HUD message to {@code sender}) when the body is not grid-aligned
     * or the target space is obstructed, otherwise moves the blocks back and removes the train.
     */
    public static boolean disassemble(ServerPlayer sender, StationBlockEntity stationBE) {
        GlobalStation station = stationBE.getStation();
        if (station == null) {
            return false;
        }
        SableTrain train = SableStationParking.presentTrain(station);
        if (train == null) {
            return false;
        }
        ServerLevel level = train.level();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return false;
        }
        SableTrain.Car car = train.car();
        if (car.subLevelId() == null
                || !(container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub) || sub.isRemoved()) {
            return false;
        }

        // --- Orientation snap: the body must be sitting close enough to a 90°-grid orientation and level. ---
        Quaterniond q = sub.logicalPose().orientation();
        Vector3d up = q.transform(new Vector3d(0.0, 1.0, 0.0));
        Vector3d fwd = q.transform(new Vector3d(1.0, 0.0, 0.0));
        double tolerance = Math.cos(Math.toRadians(ALIGN_TOLERANCE_DEG));

        Rotation rotation = nearestGridRotation(fwd);
        double horizontal = Math.hypot(fwd.x, fwd.z);
        double yawDot = horizontal < 1.0e-6 ? 0.0
                : (fwd.x * rotatedX(rotation) + fwd.z * rotatedZ(rotation)) / horizontal;
        if (up.y < tolerance || yawDot < tolerance) {
            message(sender, "loconautics.station.sabletrain.not_aligned");
            return false;
        }

        // --- Lattice transform: world(p) = worldAnchor + rotate(p - plotAnchor). Rigid by construction, so the
        // placed structure can never self-overlap regardless of small physics-pose error. ---
        ServerLevel plotLevel = sub.getLevel();
        var bounds = sub.getPlot().getBoundingBox();
        BlockPos plotAnchor = sub.getPlot().getCenterBlock();
        Vector3d anchorWorld = sub.logicalPose().transformPosition(
                new Vector3d(plotAnchor.getX() + 0.5, plotAnchor.getY() + 0.5, plotAnchor.getZ() + 0.5),
                new Vector3d());
        BlockPos worldAnchor = BlockPos.containing(anchorWorld.x, anchorWorld.y, anchorWorld.z);

        // Collect the body's blocks and validate every target world position before touching anything.
        List<BlockPos> plotBlocks = new ArrayList<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    if (!plotLevel.getBlockState(pos.set(x, y, z)).isAir()) {
                        plotBlocks.add(pos.immutable());
                    }
                }
            }
        }
        if (plotBlocks.isEmpty()) {
            return false;
        }
        for (BlockPos p : plotBlocks) {
            BlockPos target = targetOf(p, plotAnchor, worldAnchor, rotation);
            BlockState existing = level.getBlockState(target);
            if (!existing.isAir() && !existing.canBeReplaced()) {
                message(sender, "loconautics.station.sabletrain.obstructed");
                return false;
            }
        }

        // --- Place the blocks (state rotated to match, block-entity data carried over). ---
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (BlockPos p : plotBlocks) {
            BlockPos target = targetOf(p, plotAnchor, worldAnchor, rotation);
            BlockState state = plotLevel.getBlockState(p).rotate(rotation);
            level.setBlock(target, state, Block.UPDATE_ALL);
            BlockEntity plotBe = plotLevel.getBlockEntity(p);
            if (plotBe != null && level.getBlockEntity(target) instanceof BlockEntity worldBe) {
                CompoundTag data = plotBe.saveWithFullMetadata(plotLevel.registryAccess());
                worldBe.loadWithComponents(data, level.registryAccess());
                worldBe.setChanged();
            }
            minX = Math.min(minX, target.getX());
            minY = Math.min(minY, target.getY());
            minZ = Math.min(minZ, target.getZ());
            maxX = Math.max(maxX, target.getX());
            maxY = Math.max(maxY, target.getY());
            maxZ = Math.max(maxZ, target.getZ());
        }

        // One glue entity spanning the placed structure, so the disassembled carriage is immediately
        // re-assemblable — the station assembler unions glued blocks into carriages (Create's contraption
        // disassembly leaves its blocks glued the same way).
        AABB glueBounds = new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
        level.addFreshEntity(new SuperGlueEntity(level, glueBounds));

        // --- Retire the train: release the station slot, then remove the body sub-level. The container's
        // removal observer (SableTrainSpawner.ensureObserver) unregisters the train, forgets it on disk and
        // drops the client marker; the station mixin sees no parked train next tick and lowers the flag. ---
        SableStationParking.cancelReservation(train, station);
        container.removeSubLevel(sub, SubLevelRemovalReason.REMOVED);

        LoconauticsConstants.LOGGER.info("[sabletrain] disassembled train {} at station '{}' ({} blocks)",
                train.id(), station.name, plotBlocks.size());
        return true;
    }

    /** World block position of plot block {@code p} under the snapped lattice transform. */
    private static BlockPos targetOf(BlockPos p, BlockPos plotAnchor, BlockPos worldAnchor, Rotation rotation) {
        int dx = p.getX() - plotAnchor.getX();
        int dy = p.getY() - plotAnchor.getY();
        int dz = p.getZ() - plotAnchor.getZ();
        return switch (rotation) {
            case NONE -> worldAnchor.offset(dx, dy, dz);
            case CLOCKWISE_90 -> worldAnchor.offset(-dz, dy, dx);
            case CLOCKWISE_180 -> worldAnchor.offset(-dx, dy, -dz);
            case COUNTERCLOCKWISE_90 -> worldAnchor.offset(dz, dy, -dx);
        };
    }

    /** The grid rotation whose image of local +X points closest to the body's actual forward. */
    private static Rotation nearestGridRotation(Vector3d forward) {
        Rotation best = Rotation.NONE;
        double bestDot = -Double.MAX_VALUE;
        for (Rotation rotation : Rotation.values()) {
            double dot = forward.x * rotatedX(rotation) + forward.z * rotatedZ(rotation);
            if (dot > bestDot) {
                bestDot = dot;
                best = rotation;
            }
        }
        return best;
    }

    /** X component of local +X (1,0,0) rotated by {@code rotation} (matches {@link #targetOf}'s offset math). */
    private static double rotatedX(Rotation rotation) {
        return switch (rotation) {
            case NONE -> 1.0;
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> 0.0;
            case CLOCKWISE_180 -> -1.0;
        };
    }

    /** Z component of local +X (1,0,0) rotated by {@code rotation}. */
    private static double rotatedZ(Rotation rotation) {
        return switch (rotation) {
            case NONE, CLOCKWISE_180 -> 0.0;
            case CLOCKWISE_90 -> 1.0;
            case COUNTERCLOCKWISE_90 -> -1.0;
        };
    }

    private static void message(ServerPlayer player, String key) {
        if (player != null) {
            player.displayClientMessage(Component.translatable(key), true);
        }
    }
}
