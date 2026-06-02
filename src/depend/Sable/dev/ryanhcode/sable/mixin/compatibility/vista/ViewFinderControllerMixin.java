/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.mehvahdjukaar.vista.client.ViewFinderController
 *  net.minecraft.client.Camera
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.compatibility.vista;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.mehvahdjukaar.vista.client.ViewFinderController;
import net.minecraft.client.Camera;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ViewFinderController.class})
public class ViewFinderControllerMixin {
    @Unique
    private static final Quaternionf sable$orientation = new Quaternionf();

    @Inject(method={"setupCamera"}, at={@At(value="TAIL")})
    private static void sable$setupCamera(Camera camera, BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfoReturnable<Boolean> cir, @Local(ordinal=0) Vec3 centerCannonPos) {
        ClientSubLevel subLevel = (ClientSubLevel)Sable.HELPER.getContaining(entity.level(), (Position)centerCannonPos);
        if (subLevel != null) {
            Quaternionf rotation = camera.rotation();
            sable$orientation.set(subLevel.renderPose().orientation());
            rotation.premul((Quaternionfc)sable$orientation, rotation);
        }
    }
}
