/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.level.pathfinder.Node
 *  net.minecraft.world.level.pathfinder.NodeEvaluator
 *  net.minecraft.world.level.pathfinder.WalkNodeEvaluator
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={WalkNodeEvaluator.class})
public abstract class WalkNodeEvaluatorMixin
extends NodeEvaluator {
    @Redirect(method={"getStart"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;getBlockY()I"))
    private int sable$redirectGetBlockY(Mob mob) {
        SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel((Entity)mob);
        if (trackingSubLevel != null) {
            return Mth.floor((double)trackingSubLevel.logicalPose().transformPositionInverse((Vec3)mob.position()).y);
        }
        return mob.getBlockY();
    }

    @Inject(method={"getStart"}, at={@At(value="HEAD")})
    private void sable$init(CallbackInfoReturnable<Node> cir, @Share(value="mobPosition") LocalRef<Vec3> mobPosition) {
        SubLevel trackingSubLevel = this.sable$getTrackingSubLevel();
        if (trackingSubLevel != null) {
            mobPosition.set((Object)trackingSubLevel.logicalPose().transformPositionInverse(this.mob.position()));
        } else {
            mobPosition.set((Object)this.mob.position());
        }
    }

    @Redirect(method={"getStart"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;getX()D"))
    private double sable$redirectGetX(Mob mob, @Share(value="mobPosition") LocalRef<Vec3> mobPosition) {
        return ((Vec3)mobPosition.get()).x;
    }

    @Redirect(method={"getStart"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;getZ()D"))
    private double sable$redirectGetZ(Mob mob, @Share(value="mobPosition") LocalRef<Vec3> mobPosition) {
        return ((Vec3)mobPosition.get()).z;
    }

    @Redirect(method={"getStart"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;getY()D"))
    private double sable$redirectGetY(Mob mob, @Share(value="mobPosition") LocalRef<Vec3> mobPosition) {
        return ((Vec3)mobPosition.get()).y;
    }

    @Redirect(method={"getStart"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;blockPosition()Lnet/minecraft/core/BlockPos;"))
    private BlockPos sable$redirectBlockPosition(Mob mob, @Share(value="mobPosition") LocalRef<Vec3> mobPosition) {
        return BlockPos.containing((Position)((Position)mobPosition.get()));
    }

    @Inject(method={"canReachWithoutCollision"}, at={@At(value="HEAD")})
    private void sable$canReachWithoutCollision(CallbackInfoReturnable<Boolean> cir, @Share(value="mobPosition") LocalRef<Vec3> mobPosition) {
        SubLevel trackingSubLevel = this.sable$getTrackingSubLevel();
        if (trackingSubLevel != null) {
            mobPosition.set((Object)trackingSubLevel.logicalPose().transformPositionInverse(this.mob.position()));
        } else {
            mobPosition.set((Object)this.mob.position());
        }
    }

    @Unique
    private SubLevel sable$getTrackingSubLevel() {
        return Sable.HELPER.getTrackingSubLevel((Entity)this.mob);
    }

    @Redirect(method={"canReachWithoutCollision"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;getX()D"))
    private double sable$redirectGetX2(Mob mob, @Share(value="mobPosition") LocalRef<Vec3> mobPosition) {
        return ((Vec3)mobPosition.get()).x;
    }

    @Redirect(method={"canReachWithoutCollision"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;getZ()D"))
    private double sable$redirectGetZ2(Mob mob, @Share(value="mobPosition") LocalRef<Vec3> mobPosition) {
        return ((Vec3)mobPosition.get()).z;
    }

    @Redirect(method={"canReachWithoutCollision"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;getY()D"))
    private double sable$redirectGetY2(Mob mob, @Share(value="mobPosition") LocalRef<Vec3> mobPosition) {
        return ((Vec3)mobPosition.get()).y;
    }

    @Redirect(method={"canReachWithoutCollision"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;getBoundingBox()Lnet/minecraft/world/phys/AABB;"))
    private AABB sable$canReachWithoutCollision(Mob instance) {
        SubLevel trackingSubLevel = this.sable$getTrackingSubLevel();
        if (trackingSubLevel != null) {
            return instance.getBoundingBox().move(trackingSubLevel.logicalPose().transformPositionInverse(this.mob.position()).subtract(this.mob.position()));
        }
        return instance.getBoundingBox();
    }
}
