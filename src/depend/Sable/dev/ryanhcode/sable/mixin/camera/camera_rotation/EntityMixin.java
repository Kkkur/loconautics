/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniond
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.camera.camera_rotation;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinhelpers.camera.camera_rotation.EntitySubLevelRotationHelper;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.LevelPoseProviderExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.function.Function;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Entity.class})
public abstract class EntityMixin {
    @Shadow
    private Level level;

    @Inject(method={"calculateViewVector"}, at={@At(value="RETURN")}, cancellable=true)
    public void sable$calculateViewVector(float f, float g, CallbackInfoReturnable<Vec3> cir) {
        Function<SubLevel, Pose3dc> provider;
        Level level = this.level;
        if (level instanceof LevelPoseProviderExtension) {
            LevelPoseProviderExtension levelPoseProvider = (LevelPoseProviderExtension)level;
            provider = levelPoseProvider::sable$getPose;
        } else {
            provider = SubLevel::logicalPose;
        }
        Quaterniond orientation = EntitySubLevelRotationHelper.getEntityOrientation((Entity)this, provider, 0.0f, EntitySubLevelRotationHelper.Type.CAMERA);
        if (orientation != null) {
            Vec3 viewVector = (Vec3)cir.getReturnValue();
            cir.setReturnValue((Object)JOMLConversion.toMojang((Vector3dc)orientation.transform(JOMLConversion.toJOML((Position)viewVector))));
        }
    }
}
