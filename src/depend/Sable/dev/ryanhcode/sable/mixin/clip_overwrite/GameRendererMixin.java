/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.Function
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.mixin.clip_overwrite;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.LevelPoseProviderExtension;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={GameRenderer.class})
public class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Redirect(method={"filterHitResult"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;closerThan(Lnet/minecraft/core/Position;D)Z"))
    private static boolean sable$closerThan(Vec3 a, Position b, double d) {
        return Sable.HELPER.distanceSquaredWithSubLevels((Level)Minecraft.getInstance().level, (Position)a, (Position)new Vec3(b.x(), b.y(), b.z())) < d * d;
    }

    @Redirect(method={"pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"))
    private double sable$distanceToSqr(Vec3 instance, Vec3 other) {
        return Sable.HELPER.distanceSquaredWithSubLevels((Level)this.minecraft.level, (Position)instance, (Position)other);
    }

    @Redirect(method={"pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 sable$getEyePosition(Entity instance, float partialTicks) {
        return Sable.HELPER.getEyePositionInterpolated(instance, partialTicks);
    }

    @WrapOperation(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GameRenderer;pick(F)V")})
    private void sable$renderLevel(GameRenderer instance, float f, Operation<Void> original) {
        LevelPoseProviderExtension extension = (LevelPoseProviderExtension)this.minecraft.level;
        extension.sable$pushPoseSupplier((Function<SubLevel, Pose3dc>)((Function)subLevel -> ((ClientSubLevel)subLevel).renderPose(f)));
        original.call(new Object[]{instance, Float.valueOf(f)});
        extension.sable$popPoseSupplier();
    }
}
