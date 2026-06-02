/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.utility.RaycastHelper
 *  com.simibubi.create.foundation.utility.RaycastHelper$PredicateTraceResult
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.raycasts;

import com.simibubi.create.foundation.utility.RaycastHelper;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.LevelPoseProviderExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SableRaycastHelper {
    public static RaycastHelper.PredicateTraceResult rayCastUntilWithSublevels(Level level, Vec3 start, Vec3 end, BiPredicate<@Nullable SubLevel, BlockPos> predicate) {
        return SableRaycastHelper.rayCastUntilWithSublevels(level, start, end, pos -> predicate.test((SubLevel)null, (BlockPos)pos), predicate);
    }

    public static RaycastHelper.PredicateTraceResult rayCastUntilWithSublevels(Level level, Vec3 start, Vec3 end, Predicate<BlockPos> predicate) {
        return SableRaycastHelper.rayCastUntilWithSublevels(level, start, end, predicate, (sublevel, pos) -> predicate.test((BlockPos)pos));
    }

    public static RaycastHelper.PredicateTraceResult rayCastUntilWithSublevels(Level level, Vec3 start, Vec3 end, Predicate<BlockPos> predicate, BiPredicate<SubLevel, BlockPos> subLevelPredicate) {
        RaycastHelper.PredicateTraceResult closestRay = RaycastHelper.rayTraceUntil((Vec3)start, (Vec3)end, predicate);
        double closestDistance = closestRay.getPos() != null ? Vec3.atCenterOf((Vec3i)closestRay.getPos()).distanceToSqr(start) : Double.MAX_VALUE;
        Iterable<SubLevel> sublevels = Sable.HELPER.getAllIntersecting(level, (BoundingBox3dc)new BoundingBox3d(start, end));
        for (SubLevel subLevel : sublevels) {
            Vec3 plotEnd;
            Vec3 plotStart;
            if (level instanceof LevelPoseProviderExtension) {
                LevelPoseProviderExtension poseProvider = (LevelPoseProviderExtension)level;
                plotStart = poseProvider.sable$getPose(subLevel).transformPositionInverse(start);
                plotEnd = poseProvider.sable$getPose(subLevel).transformPositionInverse(end);
            } else {
                plotStart = subLevel.logicalPose().transformPositionInverse(start);
                plotEnd = subLevel.logicalPose().transformPositionInverse(end);
            }
            RaycastHelper.PredicateTraceResult plotRay = RaycastHelper.rayTraceUntil((Vec3)plotStart, (Vec3)plotEnd, pos -> subLevelPredicate.test(subLevel, (BlockPos)pos));
            double d = plotRay.getPos() != null ? Vec3.atCenterOf((Vec3i)plotRay.getPos()).distanceToSqr(plotStart) : Double.MAX_VALUE;
            double plotDistance = d;
            if (!(plotDistance < closestDistance)) continue;
            closestRay = plotRay;
            closestDistance = plotDistance;
        }
        return closestRay;
    }
}
