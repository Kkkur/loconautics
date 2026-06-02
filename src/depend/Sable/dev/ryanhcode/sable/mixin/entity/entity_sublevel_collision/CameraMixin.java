/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.Camera
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.entity.entity_sublevel_collision;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={Camera.class})
public class CameraMixin {
    @Shadow
    private float eyeHeightOld;
    @Shadow
    private float eyeHeight;
    @Unique
    private final Vector3d sable$startPos = new Vector3d();
    @Unique
    private final Vector3d sable$endPos = new Vector3d();

    @WrapOperation(method={"setup"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Camera;setPosition(DDD)V")})
    private void sable$setPosition(Camera instance, double x, double y, double z, Operation<Void> original, @Local(argsOnly=true) Entity entity, @Local(argsOnly=true) float partialTicks) {
        SubLevel trackingSubLevel = Sable.HELPER.getTrackingOrVehicleSubLevel(entity);
        if (trackingSubLevel instanceof ClientSubLevel) {
            ClientSubLevel clientSubLevel = (ClientSubLevel)trackingSubLevel;
            double yOffset = Mth.lerp((float)partialTicks, (float)this.eyeHeightOld, (float)this.eyeHeight);
            this.sable$startPos.set(entity.xo, entity.yo + yOffset, entity.zo);
            this.sable$endPos.set(entity.getX(), entity.getY() + yOffset, entity.getZ());
            Pose3dc renderPose = clientSubLevel.renderPose(partialTicks);
            clientSubLevel.lastPose().transformPositionInverse(this.sable$startPos);
            clientSubLevel.logicalPose().transformPositionInverse(this.sable$endPos);
            this.sable$startPos.lerp((Vector3dc)this.sable$endPos, (double)partialTicks);
            renderPose.transformPosition(this.sable$startPos);
            original.call(new Object[]{instance, this.sable$startPos.x, this.sable$startPos.y, this.sable$startPos.z});
            return;
        }
        original.call(new Object[]{instance, x, y, z});
    }
}
