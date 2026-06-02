/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.mixin.entity.entity_leashing;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={EntityRenderer.class})
public class EntityRendererMixin {
    @Redirect(method={"renderLeash"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;getRopeHoldPosition(F)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 sable$getRopeHoldPosition(Entity instance, float f, @Local(argsOnly=true, ordinal=0) Entity leashedEntity) {
        ActiveSableCompanion helper = Sable.HELPER;
        SubLevel leashedSubLevel = helper.getContaining(leashedEntity);
        Vector3d ropeHoldPosition = JOMLConversion.toJOML((Position)instance.getRopeHoldPosition(f));
        SubLevel holdingSubLevel = helper.getContaining(leashedEntity.level(), (Vector3dc)ropeHoldPosition);
        if (holdingSubLevel != null) {
            holdingSubLevel.logicalPose().transformPosition(ropeHoldPosition);
        }
        if (leashedSubLevel != null) {
            leashedSubLevel.logicalPose().transformPositionInverse(ropeHoldPosition);
        }
        return JOMLConversion.toMojang((Vector3dc)ropeHoldPosition);
    }

    @Redirect(method={"renderLeash"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 sable$getEyePosition(Entity instance, float f, @Local(argsOnly=true, ordinal=0) Entity leashedEntity) {
        ActiveSableCompanion helper = Sable.HELPER;
        SubLevel leashedSubLevel = helper.getContaining(leashedEntity);
        Vector3d eyePosition = JOMLConversion.toJOML((Position)instance.getEyePosition(f));
        SubLevel holdingSubLevel = helper.getContaining(leashedEntity.level(), (Vector3dc)eyePosition);
        if (holdingSubLevel != null) {
            holdingSubLevel.logicalPose().transformPosition(eyePosition);
        }
        if (leashedSubLevel != null) {
            leashedSubLevel.logicalPose().transformPositionInverse(eyePosition);
        }
        return JOMLConversion.toMojang((Vector3dc)eyePosition);
    }
}
