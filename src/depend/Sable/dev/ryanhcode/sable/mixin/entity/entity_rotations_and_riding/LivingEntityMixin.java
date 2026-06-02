/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entity_rotations_and_riding;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LivingEntity.class})
public abstract class LivingEntityMixin
extends Entity {
    @Shadow
    protected abstract float getJumpPower();

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method={"jumpFromGround"}, at={@At(value="HEAD")}, cancellable=true)
    public void sable$jumpFromGround(CallbackInfo ci) {
        Quaterniondc orientation = EntitySubLevelUtil.getCustomEntityOrientation(this, 1.0f);
        if (orientation == null) {
            return;
        }
        float power = this.getJumpPower();
        if (!(power <= 1.0E-5f)) {
            Vector3d deltaMovement = JOMLConversion.toJOML((Position)this.getDeltaMovement());
            Vector3d up = orientation.transform(OrientedBoundingBox3d.UP, new Vector3d());
            deltaMovement.fma(-up.dot((Vector3dc)deltaMovement), (Vector3dc)up).fma((double)power, (Vector3dc)up);
            this.setDeltaMovement(deltaMovement.x, deltaMovement.y, deltaMovement.z);
            if (this.isSprinting()) {
                float yRot = this.getYRot() * ((float)Math.PI / 180);
                Vec3 horizontalImpulse = new Vec3((double)(-Mth.sin((float)yRot)) * 0.2, 0.0, (double)Mth.cos((float)yRot) * 0.2);
                this.addDeltaMovement(JOMLConversion.toMojang((Vector3dc)orientation.transform(JOMLConversion.toJOML((Position)horizontalImpulse))));
            }
            this.hasImpulse = true;
        }
        ci.cancel();
    }

    @WrapOperation(method={"dismountVehicle"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;dismountTo(DDD)V")})
    public void sable$onDismountVehicle(LivingEntity instance, double x, double y, double z, Operation<Void> original) {
        Vec3 dismountPosition = new Vec3(x, y, z);
        SubLevel subLevel = Sable.HELPER.getContaining(instance.level(), (Position)dismountPosition);
        if (subLevel == null) {
            original.call(new Object[]{instance, x, y, z});
            return;
        }
        Vec3 pos = subLevel.logicalPose().transformPosition(dismountPosition);
        original.call(new Object[]{instance, pos.x, pos.y, pos.z});
    }

    @Redirect(method={"dismountVehicle"}, at=@At(value="INVOKE", target="Ljava/lang/Math;max(DD)D"))
    public double sable$maxAltitude(double a, double b, @Local(argsOnly=true) Entity vehicle) {
        Vec3 vehiclePos = vehicle.position();
        SubLevel subLevel = Sable.HELPER.getContaining(vehicle);
        if (subLevel != null) {
            return Math.max(this.getY(), subLevel.logicalPose().transformPosition((Vec3)vehiclePos).y);
        }
        return Math.max(a, b);
    }
}
