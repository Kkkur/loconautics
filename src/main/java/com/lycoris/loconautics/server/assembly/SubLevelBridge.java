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

public final class SubLevelBridge {

    private SubLevelBridge() {}

    @Nullable
    public static ServerSubLevel createFromContraption(ServerLevel level, CarriageContraption contraption) {
        BlockPos anchor = contraption.anchor;
        if (anchor == null || contraption.getBlocks().isEmpty()) return null;

        List<BlockPos> worldBlocks = new ArrayList<>(contraption.getBlocks().size());
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (BlockPos local : contraption.getBlocks().keySet()) {
            BlockPos world = anchor.offset(local);
            worldBlocks.add(world);
            minX = Math.min(minX, world.getX()); minY = Math.min(minY, world.getY()); minZ = Math.min(minZ, world.getZ());
            maxX = Math.max(maxX, world.getX()); maxY = Math.max(maxY, world.getY()); maxZ = Math.max(maxZ, world.getZ());
        }

        if (worldBlocks.isEmpty()) return null;

        BoundingBox3i bounds = new BoundingBox3i(minX, minY, minZ, maxX, maxY, maxZ);
        ServerSubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks(level, anchor, worldBlocks, bounds);

        if (subLevel != null) {
            var pose = subLevel.logicalPose();
            LoconauticsConstants.LOGGER.info(
                    "[assemble] sub-level {} | worldAnchor={} plotAnchor={} pose.pos={} rotationPoint={} ({} blocks, bounds {})",
                    subLevel.getUniqueId(), anchor, subLevel.getPlot().getCenterBlock(),
                    pose.position(), pose.rotationPoint(), worldBlocks.size(), bounds);
        } else {
            LoconauticsConstants.LOGGER.warn("[assemble] sub-level creation returned null for carriage at {}", anchor);
        }

        return subLevel;
    }
}