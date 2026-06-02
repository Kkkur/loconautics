/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.sugar.Local
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.client.event.ViewportEvent$ComputeCameraAngles
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.camera_rotation;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinhelpers.camera.camera_rotation.EntitySubLevelRotationHelper;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Camera.class})
public abstract class CameraMixin {
    @Shadow
    @Final
    private static Vector3f FORWARDS;
    @Shadow
    @Final
    private static Vector3f UP;
    @Shadow
    @Final
    private static Vector3f LEFT;
    @Shadow
    @Final
    private Quaternionf rotation;
    @Shadow
    @Final
    private Vector3f left;
    @Shadow
    @Final
    private Vector3f up;
    @Shadow
    @Final
    private Vector3f forwards;
    @Shadow
    private float yRot;
    @Shadow
    private float xRot;
    @Shadow
    private Entity entity;

    @Shadow
    protected abstract void setRotation(float var1, float var2, float var3);

    @Redirect(method={"setup"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/Camera;setRotation(FFF)V", ordinal=1))
    private void sable$redirectSetRotation(Camera camera, float f, float g, float roll, @Local ViewportEvent.ComputeCameraAngles event) {
        this.setRotation(event.getYaw() + 180.0f, -event.getPitch(), roll);
    }

    @WrapMethod(method={"setPosition(Lnet/minecraft/world/phys/Vec3;)V"})
    private void sable$setPosition(Vec3 arg, Operation<Void> original) {
        Level level = this.entity.level();
        ClientSubLevel subLevel = (ClientSubLevel)Sable.HELPER.getContaining(level, (Position)arg);
        if (subLevel == null) {
            original.call(new Object[]{arg});
            return;
        }
        Pose3dc pose = subLevel.renderPose();
        Vec3 pos = pose.transformPosition(arg);
        original.call(new Object[]{pos});
    }

    @Inject(method={"setRotation(FFF)V"}, at={@At(value="INVOKE", target="Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;", shift=At.Shift.AFTER)})
    public void sable$rotateView(float f, float g, float roll, CallbackInfo ci) {
        float pt = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        Quaterniond ridingOrientation = EntitySubLevelRotationHelper.getEntityOrientation(this.entity, x -> ((ClientSubLevel)x).renderPose(), pt, EntitySubLevelRotationHelper.Type.CAMERA);
        if (ridingOrientation != null) {
            this.rotation.premul((Quaternionfc)new Quaternionf((Quaterniondc)ridingOrientation));
            FORWARDS.rotate((Quaternionfc)this.rotation, this.forwards);
            UP.rotate((Quaternionfc)this.rotation, this.up);
            LEFT.rotate((Quaternionfc)this.rotation, this.left);
            Vector3f euler = this.rotation.getEulerAnglesYXZ(new Vector3f());
            this.yRot = -180.0f - (float)Math.toDegrees(euler.y);
            this.xRot = (float)(-Math.toDegrees(euler.x));
        }
    }
}
