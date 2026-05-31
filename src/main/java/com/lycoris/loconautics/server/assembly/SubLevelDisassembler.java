package com.lycoris.loconautics.server.assembly;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.lycoris.loconautics.core.LoconauticsConstants;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper.AssemblyTransform;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Disassembles a Sable sub-level back into the world, the way Create Aeronautics does it: it moves
 * the sub-level's CURRENT blocks (with block entities and their inventories) to a world target,
 * then removes the now-empty sub-level.
 *
 * <p>Because we move the live sub-level — not Create's assembly-time snapshot — player edits
 * (broken blocks, chest contents) survive disassembly.
 */
public final class SubLevelDisassembler {

    private SubLevelDisassembler() {
    }

    /**
     * Finds the sub-level by id, moves its current blocks to {@code goal} in the world, and removes
     * it. The plot anchor (the carriage's bogey in plot space) lands at {@code goal}.
     *
     * @return true if a sub-level was found and disassembled.
     */
    public static boolean disassembleCarriage(MinecraftServer server, UUID subLevelId, BlockPos goal, Rotation rotation) {
        if (subLevelId == null) {
            return false;
        }
        for (ServerLevel level : server.getAllLevels()) {
            ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
            if (container == null) {
                continue;
            }
            SubLevel sub = container.getSubLevel(subLevelId);
            if (!(sub instanceof ServerSubLevel serverSub)) {
                continue;
            }
            moveSubLevelToWorld(level, serverSub, goal, rotation);
            container.removeSubLevel(serverSub, SubLevelRemovalReason.REMOVED);
            return true;
        }
        return false;
    }

    /** Mirrors Create Aeronautics' SimAssemblyHelper.disassembleSubLevel (block move only). */
    private static void moveSubLevelToWorld(ServerLevel level, ServerSubLevel sub, BlockPos goal, Rotation rotation) {
        ServerLevelPlot plot = sub.getPlot();
        BlockPos subLevelAnchor = plot.getCenterBlock();
        BoundingBox3i plotBounds = new BoundingBox3i(plot.getBoundingBox());

        int angle = rotation == Rotation.NONE ? 0 : 4 - rotation.ordinal();
        AssemblyTransform transform = new AssemblyTransform(subLevelAnchor, goal, angle, rotation, level);

        List<BlockPos> blocks = new ArrayList<>();
        for (PlotChunkHolder chunk : plot.getLoadedChunks()) {
            BoundingBox3ic bounds = chunk.getBoundingBox();
            if (bounds == null || bounds == BoundingBox3i.EMPTY) {
                continue;
            }
            int baseX = chunk.getPos().getMinBlockX();
            int baseZ = chunk.getPos().getMinBlockZ();
            for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
                for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
                    for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                        BlockPos pos = new BlockPos(x + baseX, y, z + baseZ);
                        BlockState state = level.getBlockState(pos);
                        if (state.isAir()) {
                            continue;
                        }
                        blocks.add(pos);
                    }
                }
            }
        }

        if (!blocks.isEmpty()) {
            plot.kickAllEntities();
            SubLevelAssemblyHelper.moveBlocks(level, transform, blocks);
        }
        SubLevelAssemblyHelper.moveTrackingPoints(level, plotBounds, null, transform);

        LoconauticsConstants.LOGGER.info("Disassembled sub-level {} to {} ({} blocks)", sub.getUniqueId(), goal, blocks.size());
    }
}
