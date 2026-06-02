/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.projectile.AbstractArrow
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.arrows_hit_blocks;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.mixinhelpers.CanFallAtleastHelper;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={AbstractArrow.class})
public abstract class AbstractArrowMixin
extends Entity {
    @Shadow
    protected boolean inGround;

    public AbstractArrowMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(method={"onHitBlock"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/projectile/AbstractArrow;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void sable$setDeltaMovement(AbstractArrow arrow, Vec3 difference, @Local(argsOnly=true) BlockHitResult blockHitResult, @Share(value="difference") LocalRef<Vec3> differenceRef, @Share(value="subLevel") LocalRef<SubLevel> subLevelRef) {
        SubLevel subLevel = Sable.HELPER.getContaining(this.level(), (Position)blockHitResult.getLocation());
        if (subLevel == null) {
            arrow.setDeltaMovement(difference);
            return;
        }
        Vec3 localPosition = subLevel.logicalPose().transformPositionInverse(this.position());
        Vec3 diff = blockHitResult.getLocation().subtract(localPosition);
        if (!this.level().isClientSide && !this.inGround) {
            Vec3 localImpulse = subLevel.logicalPose().transformNormalInverse(this.getDeltaMovement());
            RigidBodyHandle.of((ServerSubLevel)subLevel).applyImpulseAtPoint(localPosition, localImpulse);
        }
        arrow.setDeltaMovement(diff.x, diff.y, diff.z);
        differenceRef.set((Object)diff);
        subLevelRef.set((Object)subLevel);
    }

    @Redirect(method={"onHitBlock"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/projectile/AbstractArrow;setPosRaw(DDD)V"))
    private void sable$setPosRaw(AbstractArrow instance, double x, double y, double z, @Share(value="subLevel") LocalRef<SubLevel> subLevelRef, @Share(value="difference") LocalRef<Vec3> differenceRef) {
        Vec3 difference = (Vec3)differenceRef.get();
        if (difference == null) {
            instance.setPosRaw(x, y, z);
            return;
        }
        Vec3 nudge = difference.normalize().scale((double)0.05f);
        SubLevel subLevel = (SubLevel)subLevelRef.get();
        Vec3 localPosition = subLevel.logicalPose().transformPositionInverse(this.position());
        instance.setPosRaw(localPosition.x - nudge.x, localPosition.y - nudge.y, localPosition.z - nudge.z);
        Vec3 vec3 = this.getDeltaMovement();
        double d = vec3.horizontalDistance();
        this.setXRot((float)(Mth.atan2((double)vec3.y, (double)d) * 57.2957763671875));
        this.setYRot((float)(Mth.atan2((double)vec3.x, (double)vec3.z) * 57.2957763671875));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Inject(method={"startFalling"}, at={@At(value="TAIL")})
    private void sable$startFalling(CallbackInfo ci) {
        SubLevel subLevel = Sable.HELPER.getContaining(this);
        if (subLevel != null) {
            EntitySubLevelUtil.kickEntity(subLevel, this);
        }
    }

    @Redirect(method={"shouldFall"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/phys/AABB;)Z"))
    private boolean sable$noCollision(Level level, AABB aabb) {
        boolean original = level.noCollision((Entity)this, aabb);
        if (!original) {
            return false;
        }
        return CanFallAtleastHelper.canFallAtleastWithSubLevels(level, aabb) == null;
    }
}
