/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.block_breakers;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

public class SubLevelBlockBreakingUtility {
    public static BlockPos findBreakingPos(BiPredicate<BlockPos, BlockState> canBreak, @Nullable SubLevel subLevel, Level level, Vec3 drillFacingVec, Vec3 center, BlockPos breakingPos) {
        double scaleDown = 0.125;
        BoundingBox3d localMiningBox = new BoundingBox3d(new AABB(center.x - 0.5, center.y - 0.5, center.z - 0.5, center.x + 0.5, center.y + 0.5, center.z + 0.5).inflate(-0.125).move(drillFacingVec.scale(0.625)));
        BoundingBox3d globalMiningBox = new BoundingBox3d((BoundingBox3dc)localMiningBox);
        if (subLevel != null) {
            globalMiningBox.transform((Pose3dc)subLevel.logicalPose(), globalMiningBox);
        }
        BoundingBox3i globalBlockMiningBox = new BoundingBox3i(globalMiningBox);
        BoundingBox3d otherLocalMiningBox = new BoundingBox3d();
        ObjectArrayList possiblyBreakableBlocks = new ObjectArrayList();
        SubLevelBlockBreakingUtility.collectBlocksInBounds(canBreak, level, BlockPos.containing((Position)center), globalBlockMiningBox, (ObjectList<BlockPos>)possiblyBreakableBlocks);
        for (SubLevel otherSubLevel : Sable.HELPER.getAllIntersecting(level, (BoundingBox3dc)new BoundingBox3d((BoundingBox3dc)globalMiningBox))) {
            if (subLevel == otherSubLevel) continue;
            globalMiningBox.transformInverse((Pose3dc)otherSubLevel.logicalPose(), otherLocalMiningBox);
            globalBlockMiningBox.set(otherLocalMiningBox);
            SubLevelBlockBreakingUtility.collectBlocksInBounds(canBreak, level, BlockPos.containing((Position)center), globalBlockMiningBox, (ObjectList<BlockPos>)possiblyBreakableBlocks);
        }
        BlockPos closestPosition = breakingPos;
        double closestDistanceSqr = Double.MAX_VALUE;
        for (BlockPos possiblyBreakableBlock : possiblyBreakableBlocks) {
            Vec3 blockCenter;
            double distanceSqr;
            if (Sable.HELPER.getContaining(level, (Vec3i)possiblyBreakableBlock) == subLevel || !((distanceSqr = Sable.HELPER.distanceSquaredWithSubLevels(level, (Position)center, (Position)(blockCenter = Vec3.atCenterOf((Vec3i)possiblyBreakableBlock)))) < closestDistanceSqr)) continue;
            closestDistanceSqr = distanceSqr;
            closestPosition = possiblyBreakableBlock;
        }
        return closestPosition;
    }

    @Unique
    private static void collectBlocksInBounds(BiPredicate<BlockPos, BlockState> canBreak, Level level, BlockPos drillPos, BoundingBox3i globalBlockMiningBox, ObjectList<BlockPos> possiblyBreakableBlocks) {
        BlockPos.MutableBlockPos globalBlockPos = new BlockPos.MutableBlockPos();
        for (int x = globalBlockMiningBox.minX(); x <= globalBlockMiningBox.maxX(); ++x) {
            for (int z = globalBlockMiningBox.minZ(); z <= globalBlockMiningBox.maxZ(); ++z) {
                for (int y = globalBlockMiningBox.minY(); y <= globalBlockMiningBox.maxY(); ++y) {
                    globalBlockPos.set(x, y, z);
                    BlockState globalBlockState = level.getBlockState((BlockPos)globalBlockPos);
                    if (!canBreak.test((BlockPos)globalBlockPos, globalBlockState) || globalBlockPos.equals((Object)drillPos)) continue;
                    possiblyBreakableBlocks.add((Object)globalBlockPos.immutable());
                }
            }
        }
    }
}
