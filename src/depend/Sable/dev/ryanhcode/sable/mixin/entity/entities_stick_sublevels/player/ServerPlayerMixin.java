/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entities_stick_sublevels.player;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ServerPlayer.class})
public abstract class ServerPlayerMixin
extends Entity {
    @Unique
    private final Vector3d sable$trackedSubLevelPos = new Vector3d();

    public ServerPlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    public void tick(CallbackInfo ci) {
        SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel(this);
        if (trackingSubLevel != null && !trackingSubLevel.isRemoved()) {
            Vector3d entityCenter = JOMLConversion.getAABBCenter((AABB)this.getBoundingBox(), (Vector3d)this.sable$trackedSubLevelPos);
            double entityCenterX = entityCenter.x();
            double entityCenterY = entityCenter.y();
            double entityCenterZ = entityCenter.z();
            Pose3d pose = trackingSubLevel.logicalPose();
            Pose3dc lastPose = trackingSubLevel.lastPose();
            Vector3d inherited = pose.transformPosition(lastPose.transformPositionInverse((Vector3dc)entityCenter, entityCenter));
            Vec3 position = this.position();
            this.setPos(new Vec3(position.x + inherited.x - entityCenterX, position.y + inherited.y - entityCenterY, position.z + inherited.z - entityCenterZ));
        }
    }
}
