/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.entity.entities_stick_sublevels.effects;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Entity.class})
public abstract class EntityMixin {
    @Shadow
    @Final
    protected RandomSource random;
    @Shadow
    private Level level;
    @Shadow
    private BlockPos blockPosition;
    @Shadow
    public Optional<BlockPos> mainSupportingBlockPos;

    @Shadow
    public abstract Vec3 position();

    @Shadow
    @Deprecated
    public abstract BlockPos getOnPosLegacy();

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract BlockPos blockPosition();

    @Shadow
    public abstract Vec3 getEyePosition();

    @Inject(method={"spawnSprintParticle"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", shift=At.Shift.AFTER)})
    private void sable$spawnSprintParticle(CallbackInfo ci, @Local(ordinal=0) BlockPos blockPos, @Share(value="localPosition") LocalRef<Vec3> localPosition) {
        SubLevel subLevel = Sable.HELPER.getContaining(this.level, (Vec3i)blockPos);
        Vec3 feetPos = JOMLConversion.toMojang((Vector3dc)Sable.HELPER.getFeetPos((Entity)this, 0.0f));
        localPosition.set((Object)feetPos);
        if (subLevel != null) {
            localPosition.set((Object)subLevel.logicalPose().transformPositionInverse(feetPos));
        }
    }

    @Redirect(method={"spawnSprintParticle"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;getX()D"))
    private double sable$getX(Entity entity, @Share(value="localPosition") LocalRef<Vec3> localPosition) {
        return ((Vec3)localPosition.get()).x;
    }

    @Redirect(method={"spawnSprintParticle"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;getZ()D"))
    private double sable$getZ(Entity entity, @Share(value="localPosition") LocalRef<Vec3> localPosition) {
        return ((Vec3)localPosition.get()).z;
    }

    @Redirect(method={"spawnSprintParticle"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;getY()D"))
    private double sable$getY(Entity entity, @Share(value="localPosition") LocalRef<Vec3> localPosition) {
        return ((Vec3)localPosition.get()).y;
    }

    @Redirect(method={"spawnSprintParticle"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private void sable$addParticle(Level instance, ParticleOptions particleOptions, double d, double e, double f, double g, double h, double i, @Share(value="localPosition") LocalRef<Vec3> localPosition, @Local(ordinal=0) BlockPos pos) {
        SubLevel subLevel = Sable.HELPER.getContaining(this.level, (Vec3i)pos);
        if (subLevel == null) {
            instance.addParticle(particleOptions, d, e, f, g, h, i);
            return;
        }
        Vec3 upDir = new Vec3(0.0, 1.0, 0.0);
        Quaterniondc orientation = EntitySubLevelUtil.getCustomEntityOrientation((Entity)this, 1.0f);
        if (orientation != null) {
            Vector3d upDirJOML = orientation.transform(OrientedBoundingBox3d.UP, new Vector3d());
            upDir = JOMLConversion.toMojang((Vector3dc)upDirJOML);
        }
        Vec3 p = new Vec3(d, e - 0.1, f).add(subLevel.logicalPose().transformNormalInverse(upDir.scale(0.1)));
        Vec3 v = subLevel.logicalPose().transformNormalInverse(new Vec3(g, h, i));
        if (orientation != null) {
            v = this.getDeltaMovement().scale(-4.0);
            double dot = v.dot(upDir);
            v = v.subtract(upDir.x * dot, upDir.y * dot, upDir.z * dot).add(upDir.x * 1.5, upDir.y * 1.5, upDir.z * 1.5);
        }
        instance.addParticle(particleOptions, p.x, p.y, p.z, v.x, v.y, v.z);
    }

    @Inject(method={"getOnPos(F)Lnet/minecraft/core/BlockPos;"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$preGetOnPos(float distance, CallbackInfoReturnable<BlockPos> cir) {
        SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel((Entity)this);
        Vec3 feetPos = JOMLConversion.toMojang((Vector3dc)Sable.HELPER.getFeetPos((Entity)this, Math.max(0.1f, distance)));
        if (trackingSubLevel != null) {
            Vec3 localPos = trackingSubLevel.logicalPose().transformPositionInverse(feetPos);
            cir.setReturnValue((Object)BlockPos.containing((Position)localPos));
            return;
        }
        if (this.mainSupportingBlockPos.isEmpty()) {
            BoundingBox3d bounds = new BoundingBox3d(this.blockPosition);
            bounds.expand((double)distance, bounds);
            Iterable<SubLevel> intersectingSubLevels = Sable.HELPER.getAllIntersecting(this.level, (BoundingBox3dc)bounds);
            for (SubLevel subLevel : intersectingSubLevels) {
                Vec3 localPos = subLevel.logicalPose().transformPositionInverse(feetPos);
                BlockPos localBlockPos = BlockPos.containing((Position)localPos);
                if (this.level.getBlockState(localBlockPos).isAir()) continue;
                cir.setReturnValue((Object)localBlockPos);
                return;
            }
        }
    }
}
