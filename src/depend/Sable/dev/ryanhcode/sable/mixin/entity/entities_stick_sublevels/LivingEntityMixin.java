/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.core.Position
 *  net.minecraft.network.protocol.game.ClientboundAddEntityPacket
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entities_stick_sublevels;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinterface.entity.entities_stick_sublevels.LivingEntityStickExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LivingEntity.class})
public abstract class LivingEntityMixin
extends Entity
implements LivingEntityStickExtension {
    @Shadow
    protected int lerpSteps;
    @Shadow
    protected double lerpYRot;
    @Shadow
    protected double lerpXRot;
    @Unique
    private Vec3 sable$lerpTarget = Vec3.ZERO;
    @Unique
    private int sable$sableLerpSteps;
    @Unique
    private int sable$sableRotLerpSteps;

    @Shadow
    protected abstract void updateWalkAnimation(float var1);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void sable$setupLerp() {
        if (this.sable$getPlotPosition() != null && this.lerpSteps > 0) {
            this.sable$sableRotLerpSteps = this.lerpSteps;
            this.lerpSteps = 0;
        }
    }

    @Override
    public void sable$applyLerp() {
        Vec3 plotPos = this.sable$getPlotPosition();
        if (plotPos == null) {
            this.sable$sableLerpSteps = 0;
            this.sable$sableRotLerpSteps = 0;
            return;
        }
        if (this.sable$sableLerpSteps > 0) {
            this.sable$setPlotPosition(plotPos.lerp(this.sable$lerpTarget, 1.0 / (double)this.sable$sableLerpSteps));
            --this.sable$sableLerpSteps;
        }
        if (this.sable$sableRotLerpSteps > 0) {
            double difference = Mth.wrapDegrees((double)(this.lerpYRot - (double)this.getYRot()));
            this.setYRot(this.getYRot() + (float)difference / (float)this.sable$sableRotLerpSteps);
            this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.sable$sableRotLerpSteps);
            --this.sable$sableRotLerpSteps;
            this.setRot(this.getYRot(), this.getXRot());
        }
    }

    @Override
    public Vec3 sable$getLerpTarget() {
        return this.sable$lerpTarget;
    }

    @Inject(method={"aiStep"}, at={@At(value="HEAD")})
    private void sable$updateRotLerp(CallbackInfo ci) {
        this.sable$setupLerp();
    }

    @Inject(method={"aiStep"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V", shift=At.Shift.BEFORE)})
    private void sable$updatePlotPosition(CallbackInfo ci) {
        this.sable$applyLerp();
    }

    @Override
    public void sable$plotLerpTo(Vec3 pos, int lerpSteps) {
        this.sable$lerpTarget = pos;
        this.sable$sableLerpSteps = lerpSteps;
    }

    @ModifyVariable(method={"tick"}, at=@At(value="STORE"), ordinal=0)
    private double sable$modifyXDifference(double x) {
        return this.sable$getDifference((boolean)true).x;
    }

    @ModifyVariable(method={"tick"}, at=@At(value="STORE"), ordinal=1)
    private double sable$modifyZDifference(double x) {
        return this.sable$getDifference((boolean)true).z;
    }

    @Redirect(method={"calculateEntityAnimation"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;updateWalkAnimation(F)V"))
    private void sable$walkAnimation(LivingEntity instance, float g, boolean pIncludeHeight) {
        Vec3 delta = this.sable$getDifference(false);
        float f = (float)Mth.length((double)delta.x, (double)(pIncludeHeight ? delta.y : 0.0), (double)delta.z);
        this.updateWalkAnimation(f);
    }

    @Unique
    private Vec3 sable$getDifference(boolean countLocalPlayer) {
        Quaterniondc orientation;
        Player player;
        LivingEntityMixin livingEntityMixin;
        Vec3 currentPos = this.position();
        Vec3 oldPos = new Vec3(this.xo, this.yo, this.zo);
        Vec3 delta = currentPos.subtract(oldPos);
        SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel(this);
        if (!(trackingSubLevel == null || !countLocalPlayer && (livingEntityMixin = this) instanceof Player && (player = (Player)livingEntityMixin).isLocalPlayer())) {
            Pose3d pose = trackingSubLevel.logicalPose();
            Pose3dc lastPose = trackingSubLevel.lastPose();
            currentPos = pose.transformPositionInverse(currentPos);
            oldPos = lastPose.transformPositionInverse(oldPos);
            delta = currentPos.subtract(oldPos);
            delta = pose.transformNormal(delta);
        }
        if ((orientation = EntitySubLevelUtil.getCustomEntityOrientation(this, 1.0f)) != null) {
            delta = JOMLConversion.toMojang((Vector3dc)orientation.transformInverse(JOMLConversion.toJOML((Position)delta)));
        }
        return delta;
    }

    @Inject(method={"recreateFromPacket"}, at={@At(value="TAIL")})
    public void sable$recreateFromPacket(ClientboundAddEntityPacket packet, CallbackInfo ci) {
        if (!EntitySubLevelUtil.shouldKick(this)) {
            return;
        }
        double packetX = packet.getX();
        double packetY = packet.getY();
        double packetZ = packet.getZ();
        SubLevel packetSubLevel = Sable.HELPER.getContaining(this.level(), packetX, packetZ);
        if (packetSubLevel != null) {
            Vector3d globalPacketPos = packetSubLevel.logicalPose().transformPosition(new Vector3d(packetX, packetY, packetZ));
            this.setPos(globalPacketPos.x, globalPacketPos.y, globalPacketPos.z);
        }
    }
}
