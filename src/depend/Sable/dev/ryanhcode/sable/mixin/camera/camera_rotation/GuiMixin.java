/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.world.entity.Entity
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.camera.camera_rotation;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.ryanhcode.sable.mixinhelpers.camera.camera_rotation.EntitySubLevelRotationHelper;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Gui.class})
public class GuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method={"renderCrosshair"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/systems/RenderSystem;getModelViewStack()Lorg/joml/Matrix4fStack;")})
    private void sable$onRenderCrosshair(CallbackInfo ci, @Share(value="mountedOrientation") LocalRef<Quaterniond> mountedOrientation) {
        Camera camera = this.minecraft.gameRenderer.getMainCamera();
        Entity entity = camera.getEntity();
        float pt = this.minecraft.getTimer().getGameTimeDeltaPartialTick(true);
        Quaterniond ridingOrientation = EntitySubLevelRotationHelper.getEntityOrientation(entity, x -> ((ClientSubLevel)x).renderPose(), pt, EntitySubLevelRotationHelper.Type.CAMERA);
        mountedOrientation.set((Object)ridingOrientation);
    }

    @Redirect(method={"renderCrosshair"}, at=@At(value="INVOKE", target="Lorg/joml/Matrix4fStack;rotateX(F)Lorg/joml/Matrix4f;"))
    private Matrix4f sable$redirectRotateX(Matrix4fStack stack, float angle, @Share(value="mountedOrientation") LocalRef<Quaterniond> mountedOrientation) {
        if (mountedOrientation.get() != null) {
            float pt = this.minecraft.getTimer().getGameTimeDeltaPartialTick(true);
            Camera camera = this.minecraft.gameRenderer.getMainCamera();
            Entity entity = camera.getEntity();
            return stack.rotateX(-entity.getViewXRot(pt) * ((float)Math.PI / 180));
        }
        return stack.rotateX(angle);
    }

    @Redirect(method={"renderCrosshair"}, at=@At(value="INVOKE", target="Lorg/joml/Matrix4fStack;rotateY(F)Lorg/joml/Matrix4f;"))
    private Matrix4f sable$redirectRotateY(Matrix4fStack stack, float angle, @Share(value="mountedOrientation") LocalRef<Quaterniond> mountedOrientation) {
        if (mountedOrientation.get() != null) {
            float pt = this.minecraft.getTimer().getGameTimeDeltaPartialTick(true);
            Camera camera = this.minecraft.gameRenderer.getMainCamera();
            Entity entity = camera.getEntity();
            stack.rotateY(entity.getViewYRot(pt) * ((float)Math.PI / 180));
            return stack.rotate((Quaternionfc)new Quaternionf((Quaterniondc)mountedOrientation.get()).conjugate());
        }
        return stack.rotateY(angle);
    }
}
