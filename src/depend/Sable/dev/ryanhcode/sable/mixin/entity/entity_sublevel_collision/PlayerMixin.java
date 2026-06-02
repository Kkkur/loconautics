/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.player.Abilities
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.entity.entity_sublevel_collision;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinhelpers.CanFallAtleastHelper;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Player.class})
public abstract class PlayerMixin
extends LivingEntity {
    @Shadow
    public float bob;
    @Shadow
    @Final
    private Abilities abilities;

    @Shadow
    protected abstract boolean isStayingOnGroundSurface();

    @Shadow
    protected abstract boolean isAboveGround(float var1);

    @Shadow
    protected abstract boolean canFallAtLeast(double var1, double var3, float var5);

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method={"maybeBackOffFromEdge"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$maybeBackOffFromEdge(Vec3 movement, MoverType moverType, CallbackInfoReturnable<Vec3> cir) {
        SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel((Entity)this);
        if (trackingSubLevel != null) {
            float maxUpStep = this.maxUpStep();
            if (!this.abilities.flying && !(movement.y > 0.0) && (moverType == MoverType.SELF || moverType == MoverType.PLAYER) && this.isStayingOnGroundSurface() && this.isAboveGround(maxUpStep)) {
                double xMovement;
                Pose3dc pose = trackingSubLevel.lastPose();
                double originalYaw = pose.orientation().getEulerAnglesYXZ((Vector3d)new Vector3d()).y;
                Quaterniond frameOrientation = new Quaterniond().rotateY(originalYaw);
                Vector3d localMovement = frameOrientation.transformInverse(new Vector3d(movement.x, 0.0, movement.z));
                double zMovement = localMovement.z();
                double step = 0.05;
                double signedStep = Math.signum(xMovement) * 0.05;
                double i = Math.signum(zMovement) * 0.05;
                for (xMovement = localMovement.x(); xMovement != 0.0 && this.sable$wouldSlideOff(xMovement, 0.0, maxUpStep, (Quaterniondc)frameOrientation); xMovement -= signedStep) {
                    if (!(Math.abs(xMovement) <= 0.05)) continue;
                    xMovement = 0.0;
                    break;
                }
                while (zMovement != 0.0 && this.sable$wouldSlideOff(0.0, zMovement, maxUpStep, (Quaterniondc)frameOrientation)) {
                    if (Math.abs(zMovement) <= 0.05) {
                        zMovement = 0.0;
                        break;
                    }
                    zMovement -= i;
                }
                while (xMovement != 0.0 && zMovement != 0.0 && this.sable$wouldSlideOff(xMovement, zMovement, maxUpStep, (Quaterniondc)frameOrientation)) {
                    xMovement = Math.abs(xMovement) <= 0.05 ? 0.0 : (xMovement -= signedStep);
                    if (Math.abs(zMovement) <= 0.05) {
                        zMovement = 0.0;
                        continue;
                    }
                    zMovement -= i;
                }
                Vector3d globalMovement = frameOrientation.transform(new Vector3d(xMovement, 0.0, zMovement));
                Vec3 finalMovement = new Vec3(globalMovement.x, movement.y, globalMovement.z);
                cir.setReturnValue((Object)finalMovement);
            }
        }
    }

    @Unique
    private boolean sable$wouldSlideOff(double localXMovement, double localZMovement, float fallDistance, Quaterniondc frameOrientation) {
        Vector3d movement = new Vector3d(localXMovement, 0.0, localZMovement);
        frameOrientation.transform(movement);
        double xMovement = movement.x;
        double zMovement = movement.z;
        AABB bounds = this.getBoundingBox();
        AABB boundsToCheck = new AABB(bounds.minX + xMovement, bounds.minY - (double)fallDistance - (double)1.0E-5f, bounds.minZ + zMovement, bounds.maxX + xMovement, bounds.minY, bounds.maxZ + zMovement);
        return CanFallAtleastHelper.canFallAtleastWithSubLevels(this.level(), boundsToCheck) == null;
    }

    @Redirect(method={"canFallAtLeast"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z"))
    private boolean sable$noCollision(Level level, Entity entity, AABB aabb) {
        boolean original = level.noCollision(entity, aabb);
        if (!original) {
            return false;
        }
        return CanFallAtleastHelper.canFallAtleastWithSubLevels(level, aabb) == null;
    }
}
