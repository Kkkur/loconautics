/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  net.mehvahdjukaar.moonlight.api.client.util.LOD
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.compatibility.vista;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.mehvahdjukaar.moonlight.api.client.util.LOD;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LOD.class})
public class LODMixin {
    @Unique
    private static final Vector3d sable$direction = new Vector3d();
    @Shadow
    @Final
    @Mutable
    private Vec3 objCenter;
    @Shadow
    @Final
    @Mutable
    private double distSq;
    @Shadow
    @Final
    private Vec3 cameraPosition;
    @Unique
    private Vec3 sable$localPos = null;

    @WrapMethod(method={"isPlaneCulled(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;FF)Z"})
    private boolean sable$isPlaneCulled(Vec3 planeNormal, Vec3 offset, float discRadius, float cosTolerance, Operation<Boolean> original) {
        ClientSubLevel clientSubLevel = Sable.HELPER.getContainingClient((Position)this.sable$localPos);
        if (clientSubLevel != null) {
            planeNormal = clientSubLevel.renderPose().transformNormal(planeNormal);
            if (offset != null) {
                offset = clientSubLevel.renderPose().transformNormal(offset);
            }
        }
        return (Boolean)original.call(new Object[]{planeNormal, offset, Float.valueOf(discRadius), Float.valueOf(cosTolerance)});
    }

    @Inject(method={"<init>(Lnet/minecraft/client/Camera;Lnet/minecraft/world/phys/Vec3;)V"}, at={@At(value="TAIL")})
    private void sable$init(Camera camera, Vec3 objCenter, CallbackInfo ci) {
        ClientLevel level = Minecraft.getInstance().level;
        this.sable$localPos = objCenter;
        this.objCenter = Sable.HELPER.projectOutOfSubLevel((Level)level, objCenter);
        this.distSq = LOD.isScoping() ? 1.0 : Sable.HELPER.distanceSquaredWithSubLevels((Level)level, (Position)this.cameraPosition, (Position)objCenter);
    }
}
