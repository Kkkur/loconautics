package com.lycoris.loconautics.server.assembly;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.lycoris.loconautics.core.LoconauticsConstants;

import com.simibubi.create.content.trains.entity.CarriageContraption;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Bridges a Create {@link CarriageContraption} into a Sable {@link ServerSubLevel}.
 *
 * <p>Camino A: this is called while the carriage's blocks are still in the world (during the
 * redirected {@code removeBlocksFromWorld}). It reconstructs the world positions of the carriage
 * blocks and hands them to {@link SubLevelAssemblyHelper#assembleBlocks}, which moves them out of
 * the world and into a fresh sub-level.
 *
 * <p>Block position mapping (verified against Create 6.0.10 via javap):
 * {@code Contraption.getBlocks()} keys are LOCAL positions; {@code Contraption.toLocalPos} shows
 * local = world - anchor, so {@code worldPos = anchor.offset(localPos)}.
 */
public final class SubLevelBridge {

    private SubLevelBridge() {
    }

    /**
     * Creates a Sable sub-level from a carriage contraption.
     *
     * @return the created {@link ServerSubLevel}, or {@code null} if the contraption had no blocks.
     */
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

        if (worldBlocks.isEmpty()) {
            return null;
        }

        BoundingBox3i bounds = new BoundingBox3i(minX, minY, minZ, maxX, maxY, maxZ);
        ServerSubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks(level, anchor, worldBlocks, bounds);

        LoconauticsConstants.LOGGER.info(
                "Created sub-level {} from carriage at {} ({} blocks)",
                subLevel != null ? subLevel.getUniqueId() : "null", anchor, worldBlocks.size());

        return subLevel;
    }
}
