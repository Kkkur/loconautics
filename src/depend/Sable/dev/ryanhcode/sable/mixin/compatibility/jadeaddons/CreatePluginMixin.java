/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  snownee.jade.addon.create.CreatePlugin
 */
package dev.ryanhcode.sable.mixin.compatibility.jadeaddons;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.addon.create.CreatePlugin;

@Mixin(value={CreatePlugin.class})
public class CreatePluginMixin {
    @WrapOperation(method={"lambda$registerClient$1"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;")})
    private static Vec3 sable$getEyePosition(Entity instance, float f, Operation<Vec3> original, @Local(argsOnly=true) Entity e) {
        ClientSubLevel subLevel = (ClientSubLevel)Sable.HELPER.getContaining(e);
        if (subLevel != null) {
            return subLevel.renderPose().transformPositionInverse((Vec3)original.call(new Object[]{instance, Float.valueOf(f)}));
        }
        return (Vec3)original.call(new Object[]{instance, Float.valueOf(f)});
    }

    @WrapOperation(method={"lambda$registerClient$1"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;getViewVector(F)Lnet/minecraft/world/phys/Vec3;")})
    private static Vec3 sable$getViewVector(Entity instance, float f, Operation<Vec3> original, @Local(argsOnly=true) Entity e) {
        ClientSubLevel subLevel = (ClientSubLevel)Sable.HELPER.getContaining(e);
        if (subLevel != null) {
            return subLevel.renderPose().transformNormalInverse((Vec3)original.call(new Object[]{instance, Float.valueOf(f)}));
        }
        return (Vec3)original.call(new Object[]{instance, Float.valueOf(f)});
    }
}
