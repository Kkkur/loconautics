/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.ChunkSource
 */
package dev.simulated_team.simulated.util;

import dev.ryanhcode.sable.Sable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;

public class SimLevelUtil {
    public static boolean isAreaActuallyLoaded(Level level, BlockPos center, int range) {
        if (Sable.HELPER.getContaining(level, (Vec3i)center) != null) {
            return true;
        }
        if (!level.isAreaLoaded(center, range)) {
            return false;
        }
        if (level.isClientSide) {
            int minY = center.getY() - range;
            int maxY = center.getY() + range;
            if (maxY < level.getMinBuildHeight() || minY >= level.getMaxBuildHeight()) {
                return false;
            }
            int minX = center.getX() - range;
            int minZ = center.getZ() - range;
            int maxX = center.getX() + range;
            int maxZ = center.getZ() + range;
            int minChunkX = SectionPos.blockToSectionCoord((int)minX);
            int maxChunkX = SectionPos.blockToSectionCoord((int)maxX);
            int minChunkZ = SectionPos.blockToSectionCoord((int)minZ);
            int maxChunkZ = SectionPos.blockToSectionCoord((int)maxZ);
            ChunkSource chunkSource = level.getChunkSource();
            for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX) {
                for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ) {
                    if (chunkSource.hasChunk(chunkX, chunkZ)) continue;
                    return false;
                }
            }
        }
        return true;
    }
}
