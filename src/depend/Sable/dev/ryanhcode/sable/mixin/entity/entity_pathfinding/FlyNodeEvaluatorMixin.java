/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.level.pathfinder.FlyNodeEvaluator
 *  net.minecraft.world.level.pathfinder.Node
 *  net.minecraft.world.level.pathfinder.NodeEvaluator
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.entity.entity_pathfinding;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={FlyNodeEvaluator.class})
public abstract class FlyNodeEvaluatorMixin
extends NodeEvaluator {
    @Inject(method={"getStart"}, at={@At(value="HEAD")})
    private void sable$init(CallbackInfoReturnable<Node> cir, @Share(value="mobPosition") LocalRef<Vec3> mobPosition) {
        SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel((Entity)this.mob);
        if (trackingSubLevel != null) {
            mobPosition.set((Object)trackingSubLevel.logicalPose().transformPositionInverse(this.mob.position()));
        } else {
            mobPosition.set((Object)this.mob.position());
        }
    }

    @Redirect(method={"getStart"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;getBlockY()I"))
    private int sable$redirectGetBlockY(Mob mob, @Share(value="mobPosition") LocalRef<Vec3> mobPosition) {
        return Mth.floor((double)((Vec3)mobPosition.get()).y);
    }

    @Redirect(method={"getStart"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;getX()D"))
    private double sable$redirectGetX(Mob mob, @Share(value="mobPosition") LocalRef<Vec3> mobPosition) {
        return ((Vec3)mobPosition.get()).x;
    }

    @Redirect(method={"getStart"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;getY()D"))
    private double sable$redirectGetY(Mob mob, @Share(value="mobPosition") LocalRef<Vec3> mobPosition) {
        return ((Vec3)mobPosition.get()).y;
    }

    @Redirect(method={"getStart"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;getZ()D"))
    private double sable$redirectGetZ(Mob mob, @Share(value="mobPosition") LocalRef<Vec3> mobPosition) {
        return ((Vec3)mobPosition.get()).z;
    }

    @Overwrite
    private Iterable<BlockPos> iteratePathfindingStartNodeCandidatePositions(Mob mob) {
        AABB mobBounds = mob.getBoundingBox();
        boolean small = mobBounds.getSize() < 1.0;
        SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel((Entity)this.mob);
        Vec3 localPosition = this.mob.position();
        if (trackingSubLevel != null) {
            localPosition = trackingSubLevel.logicalPose().transformPositionInverse(localPosition);
        }
        AABB localMobBounds = mob.getBoundingBox().move(localPosition.subtract(this.mob.position()));
        if (!small) {
            int blockY = Mth.floor((double)localPosition.y);
            return List.of(BlockPos.containing((double)localMobBounds.minX, (double)blockY, (double)localMobBounds.minZ), BlockPos.containing((double)localMobBounds.minX, (double)blockY, (double)localMobBounds.maxZ), BlockPos.containing((double)localMobBounds.maxX, (double)blockY, (double)localMobBounds.minZ), BlockPos.containing((double)localMobBounds.maxX, (double)blockY, (double)localMobBounds.maxZ));
        }
        double xSize = Math.max(0.0, (double)1.1f - mobBounds.getXsize());
        double ySize = Math.max(0.0, (double)1.1f - mobBounds.getYsize());
        double zSize = Math.max(0.0, (double)1.1f - mobBounds.getZsize());
        AABB localBounds = localMobBounds.inflate(xSize, ySize, zSize);
        return BlockPos.randomBetweenClosed((RandomSource)mob.getRandom(), (int)10, (int)Mth.floor((double)localBounds.minX), (int)Mth.floor((double)localBounds.minY), (int)Mth.floor((double)localBounds.minZ), (int)Mth.floor((double)localBounds.maxX), (int)Mth.floor((double)localBounds.maxY), (int)Mth.floor((double)localBounds.maxZ));
    }
}
