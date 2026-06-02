/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entity_rotations_and_riding;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Player.class})
public abstract class PlayerMixin
extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method={"travel"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/player/Player;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;", ordinal=1)})
    private void sable$storeUpDeltaMovement(Vec3 vec3, CallbackInfo ci, @Share(value="upDir") LocalRef<Vector3d> upDir, @Share(value="upDeltaMovement") LocalRef<Vector3d> upDeltaMovement) {
        Quaterniondc orientation = EntitySubLevelUtil.getCustomEntityOrientation((Entity)this, 1.0f);
        if (orientation == null) {
            return;
        }
        Vector3d dir = orientation.transform(new Vector3d(OrientedBoundingBox3d.UP));
        upDir.set((Object)new Vector3d((Vector3dc)dir));
        Vec3 deltaMovement = this.getDeltaMovement();
        upDeltaMovement.set((Object)dir.mul(dir.dot(deltaMovement.x, deltaMovement.y, deltaMovement.z)));
    }

    @Redirect(method={"travel"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/player/Player;setDeltaMovement(DDD)V"))
    private void sable$modifyTravelSetDeltaMovement(Player instance, double x, double y, double z, @Share(value="upDir") LocalRef<Vector3d> upDir, @Share(value="upDeltaMovement") LocalRef<Vector3d> upDeltaMovement) {
        if (upDeltaMovement.get() == null) {
            instance.setDeltaMovement(x, y, z);
            return;
        }
        Vec3 deltaMovement = this.getDeltaMovement();
        double dot = ((Vector3d)upDir.get()).dot(deltaMovement.x, deltaMovement.y, deltaMovement.z);
        double scalar = 0.6;
        this.setDeltaMovement(deltaMovement.subtract(dot * ((Vector3d)upDir.get()).x, dot * ((Vector3d)upDir.get()).y, dot * ((Vector3d)upDir.get()).z).add(((Vector3d)upDeltaMovement.get()).x * 0.6, ((Vector3d)upDeltaMovement.get()).y * 0.6, ((Vector3d)upDeltaMovement.get()).z * 0.6));
    }

    @Redirect(method={"aiStep"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/AABB;minmax(Lnet/minecraft/world/phys/AABB;)Lnet/minecraft/world/phys/AABB;"))
    public AABB sable$fixRidingBoundingBox(AABB usBoundingBox, AABB vehicleBoundingBox) {
        Entity vehicle = this.getVehicle();
        SubLevel vehicleSubLevel = Sable.HELPER.getContaining(vehicle);
        if (vehicleSubLevel == null) {
            return usBoundingBox.minmax(vehicleBoundingBox);
        }
        BoundingBox3d bb = new BoundingBox3d(vehicleBoundingBox);
        vehicleBoundingBox = bb.transform((Pose3dc)vehicleSubLevel.logicalPose(), bb).toMojang();
        return usBoundingBox.minmax(vehicleBoundingBox);
    }
}
