/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.commands.arguments.EntityAnchorArgument$Anchor
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.entity.entity_rotations_and_riding;

import com.mojang.authlib.GameProfile;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LocalPlayer.class})
public abstract class LocalPlayerMixin
extends Player {
    public LocalPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Redirect(method={"aiStep"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;", ordinal=0))
    private Vec3 sable$modifyFlightDir(Vec3 instance, double x, double y, double z) {
        Quaterniondc orientation = EntitySubLevelUtil.getCustomEntityOrientation((Entity)this, 1.0f);
        if (orientation == null) {
            return instance.add(x, y, z);
        }
        Vector3d dir = orientation.transform(new Vector3d(x, y, z));
        return instance.add(dir.x, dir.y, dir.z);
    }

    @Unique
    public final Vec3 sable$calculateViewVector2(float f, float g) {
        float h = f * ((float)Math.PI / 180);
        float i = -g * ((float)Math.PI / 180);
        float j = Mth.cos((float)i);
        float k = Mth.sin((float)i);
        float l = Mth.cos((float)h);
        float m = Mth.sin((float)h);
        return new Vec3((double)(k * l), (double)(-m), (double)(j * l));
    }

    @Inject(method={"startRiding(Lnet/minecraft/world/entity/Entity;Z)Z"}, at={@At(value="RETURN")})
    private void sable$onStartRiding(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (!((Boolean)cir.getReturnValue()).booleanValue() || !EntitySubLevelUtil.shouldKick((Entity)this)) {
            return;
        }
        Entity vehicle = this.getVehicle();
        if (vehicle == null) {
            return;
        }
        SubLevel subLevel = Sable.HELPER.getContaining(vehicle);
        if (subLevel != null && EntitySubLevelUtil.shouldKick((Entity)this)) {
            Vec3 lookDir = this.sable$calculateViewVector2(this.getXRot(), this.getYRot());
            Vec3 localLookDir = subLevel.logicalPose().transformNormalInverse(lookDir);
            vehicle.positionRider((Entity)this);
            EntitySubLevelUtil.setOldPosNoMovement((Entity)this);
            this.lookAt(EntityAnchorArgument.Anchor.FEET, this.position().add(localLookDir));
        }
    }

    @Inject(method={"removeVehicle"}, at={@At(value="HEAD")})
    private void sable$onStopRiding(CallbackInfo ci) {
        if (!EntitySubLevelUtil.shouldKick((Entity)this)) {
            return;
        }
        Entity vehicle = this.getVehicle();
        if (vehicle == null) {
            return;
        }
        SubLevel subLevel = Sable.HELPER.getContaining(vehicle);
        if (subLevel != null) {
            Vec3 lookDir = this.sable$calculateViewVector2(this.getXRot(), this.getYRot());
            Vec3 globalLookDir = subLevel.logicalPose().transformNormal(lookDir);
            this.lookAt(EntityAnchorArgument.Anchor.FEET, this.position().add(globalLookDir));
        }
    }

    @Unique
    private void sable$dismountVehicle(Entity entity) {
        Vector3d dismountPos;
        ActiveSableCompanion helper = Sable.HELPER;
        Level level = this.level();
        if (this.isRemoved()) {
            dismountPos = JOMLConversion.toJOML((Position)this.position());
        } else if (!entity.isRemoved() && !level.getBlockState(entity.blockPosition()).is(BlockTags.PORTALS)) {
            dismountPos = JOMLConversion.toJOML((Position)entity.getDismountLocationForPassenger((LivingEntity)this));
        } else {
            double d = Math.max(this.getY(), helper.projectOutOfSubLevel((Level)level, (Vec3)entity.position()).y);
            dismountPos = new Vector3d(this.getX(), d, this.getZ());
        }
        helper.projectOutOfSubLevel(level, dismountPos);
        this.setPos(dismountPos.x, dismountPos.y, dismountPos.z);
    }

    public void stopRiding() {
        Entity vehicle = this.getVehicle();
        super.stopRiding();
        if (this.level().isClientSide && vehicle != null && vehicle != this.getVehicle() && Sable.HELPER.getContaining(vehicle) != null) {
            this.sable$dismountVehicle(vehicle);
        }
    }
}
