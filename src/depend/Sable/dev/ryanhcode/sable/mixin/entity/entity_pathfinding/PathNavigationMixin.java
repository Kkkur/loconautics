/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.navigation.PathNavigation
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.PathNavigationRegion
 *  net.minecraft.world.level.pathfinder.Path
 *  net.minecraft.world.level.pathfinder.PathFinder
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.entity.entity_pathfinding;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.mixinterface.entity.pathfinding.PathExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PathNavigation.class})
public abstract class PathNavigationMixin {
    @Shadow
    @Final
    protected Mob mob;
    @Shadow
    @Nullable
    protected Path path;
    @Shadow
    @Final
    protected Level level;
    @Shadow
    @Nullable
    private BlockPos targetPos;
    @Shadow
    private int reachRange;
    @Shadow
    @Final
    private PathFinder pathFinder;
    @Shadow
    private float maxVisitedNodesMultiplier;

    @Shadow
    protected abstract boolean canUpdatePath();

    @Shadow
    protected abstract void resetStuckTimeout();

    @Inject(method={"createPath(Ljava/util/Set;IZIF)Lnet/minecraft/world/level/pathfinder/Path;"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$createPath(Set<BlockPos> globalSet, int i, boolean bl, int j, float f, CallbackInfoReturnable<Path> cir) {
        SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel((Entity)this.mob);
        Iterator<BlockPos> iter = globalSet.iterator();
        while (trackingSubLevel == null && iter.hasNext()) {
            BlockPos globalPos = iter.next();
            trackingSubLevel = Sable.HELPER.getContaining(this.level, (Vec3i)globalPos);
        }
        if (trackingSubLevel != null) {
            if (globalSet.isEmpty()) {
                cir.setReturnValue(null);
            } else if (!this.canUpdatePath()) {
                cir.setReturnValue(null);
            } else if (this.path != null && !this.path.isDone() && globalSet.contains(this.targetPos)) {
                cir.setReturnValue((Object)this.path);
            } else {
                Pose3d pose = trackingSubLevel.logicalPose();
                Vec3 localMobPosition = pose.transformPositionInverse(this.mob.position());
                BlockPos localMobBlockPosition = BlockPos.containing((Position)localMobPosition);
                this.level.getProfiler().push("pathfind_sub_level");
                ObjectOpenHashSet localSet = new ObjectOpenHashSet();
                for (BlockPos globalPos : globalSet) {
                    if (Sable.HELPER.getContaining(this.level, (Vec3i)globalPos) == trackingSubLevel) {
                        localSet.add(globalPos);
                        continue;
                    }
                    Vec3 globalPosVec = globalPos.getCenter();
                    Vec3 localPosVec = pose.transformPositionInverse(globalPosVec);
                    localSet.add(BlockPos.containing((Position)localPosVec));
                }
                BlockPos blockPos = bl ? localMobBlockPosition.above() : localMobBlockPosition;
                int k = (int)(f + (float)i);
                PathNavigationRegion pathNavigationRegion = new PathNavigationRegion(this.level, blockPos.offset(-k, -k, -k), blockPos.offset(k, k, k));
                Path path = this.pathFinder.findPath(pathNavigationRegion, this.mob, (Set)localSet, f, j, this.maxVisitedNodesMultiplier);
                this.level.getProfiler().pop();
                if (path != null && path.getTarget() != null) {
                    this.targetPos = path.getTarget();
                    this.reachRange = j;
                    this.resetStuckTimeout();
                    ((PathExtension)path).sable$setLocalPath(this.level, true);
                }
                cir.setReturnValue((Object)path);
            }
        }
    }
}
