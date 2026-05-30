package com.lycoris.loconautics.server.assembly;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.lycoris.loconautics.core.LoconauticsConstants;

import com.simibubi.create.content.trains.entity.CarriageContraption;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import org.joml.Quaterniond;

/**
 * Bridges a Create {@link CarriageContraption} into a Sable {@link ServerSubLevel}.
 *
 * <p>Camino A: called while the carriage's blocks are still in the world (inside the redirected
 * {@code removeBlocksFromWorld}). Reconstructs world positions and calls
 * {@link SubLevelAssemblyHelper#assembleBlocks}, which moves the blocks into a fresh sub-level.
 *
 * <p>After assembly we immediately re-teleport the sub-level with a 90° CCW rotation around Y
 * to align the Sable coordinate frame with Create's track orientation.
 */
public final class SubLevelBridge {

    /**
     * 90° counterclockwise around Y (right-handed, looking from above).
     * Negate to flip direction if the in-game result is CW instead.
     */
    private static final double INITIAL_YAW = Math.PI / 2.0;

    private SubLevelBridge() {}

    @Nullable
    public static ServerSubLevel createFromContraption(ServerLevel level, CarriageContraption contraption) {
        BlockPos anchor = contraption.anchor;
        if (anchor == null || contraption.getBlocks().isEmpty()) {
            return null;
        }

        List<BlockPos> worldBlocks = new ArrayList<>(contraption.getBlocks().size());
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (BlockPos local : contraption.getBlocks().keySet()) {
            BlockPos world = anchor.offset(local);
            worldBlocks.add(world);
            minX = Math.min(minX, world.getX());
            minY = Math.min(minY, world.getY());
            minZ = Math.min(minZ, world.getZ());
            maxX = Math.max(maxX, world.getX());
            maxY = Math.max(maxY, world.getY());
            maxZ = Math.max(maxZ, world.getZ());
        }

        if (worldBlocks.isEmpty()) return null;

        BoundingBox3i bounds = new BoundingBox3i(minX, minY, minZ, maxX, maxY, maxZ);
        ServerSubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks(level, anchor, worldBlocks, bounds);
        if (subLevel == null) return null;

        // Re-teleport with 90° CCW rotation.
        // assembleBlocks already called pipeline.teleport with identity orientation,
        // so we overwrite it here using the container obtained from the level the
        // sub-level actually lives in (guaranteed non-null at this point).
        ServerSubLevelContainer container = SubLevelContainer.getContainer(subLevel.getLevel());
        if (container != null) {
            Quaterniond rotation = new Quaterniond().rotationY(INITIAL_YAW);
            // Update the logical pose orientation so it stays consistent with the physics state.
            subLevel.logicalPose().orientation().set(rotation);
            container.physicsSystem().getPipeline().teleport(
                    subLevel,
                    subLevel.logicalPose().position(),
                    rotation
            );
        } else {
            LoconauticsConstants.LOGGER.warn(
                    "[Loconautics] SubLevelContainer is null for sub-level {}; rotation not applied",
                    subLevel.getUniqueId());
        }

        LoconauticsConstants.LOGGER.info(
                "[Loconautics] Created sub-level {} from carriage at {} ({} blocks)",
                subLevel.getUniqueId(), anchor, worldBlocks.size());

        return subLevel;
    }
}