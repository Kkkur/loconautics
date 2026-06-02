/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  foundry.veil.api.client.render.MatrixStack
 *  foundry.veil.api.client.render.VeilRenderBridge
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entity_rendering;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.VeilRenderBridge;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class LevelRendererMixin {
    @Inject(method={"renderEntity"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;getYRot()F")})
    private void renderEntityOnSubLevel(Entity entity, double cameraX, double cameraY, double cameraZ, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, CallbackInfo ci, @Local(ordinal=3) LocalDoubleRef entityX, @Local(ordinal=4) LocalDoubleRef entityY, @Local(ordinal=5) LocalDoubleRef entityZ, @Share(value="renderPose") LocalRef<Pose3dc> renderPoseShare) {
        ClientSubLevel subLevel = (ClientSubLevel)Sable.HELPER.getContaining(entity);
        if (subLevel == null) {
            SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel(entity);
            if (trackingSubLevel instanceof ClientSubLevel) {
                ClientSubLevel clientSubLevel = (ClientSubLevel)trackingSubLevel;
                if (!entity.isPassenger()) {
                    Vector3d oldTrackingPosLocal = trackingSubLevel.lastPose().transformPositionInverse(new Vector3d(entity.xOld, entity.yOld, entity.zOld));
                    Vector3d newTrackingPosLocal = trackingSubLevel.logicalPose().transformPositionInverse(JOMLConversion.toJOML((Position)entity.position()));
                    Vector3d interpolatedTrackingPosLocal = new Vector3d(Mth.lerp((double)partialTick, (double)oldTrackingPosLocal.x, (double)newTrackingPosLocal.x), Mth.lerp((double)partialTick, (double)oldTrackingPosLocal.y, (double)newTrackingPosLocal.y), Mth.lerp((double)partialTick, (double)oldTrackingPosLocal.z, (double)newTrackingPosLocal.z));
                    Pose3dc renderPose = clientSubLevel.renderPose(partialTick);
                    renderPose.transformPosition(interpolatedTrackingPosLocal);
                    entityX.set(interpolatedTrackingPosLocal.x);
                    entityY.set(interpolatedTrackingPosLocal.y);
                    entityZ.set(interpolatedTrackingPosLocal.z);
                }
            }
            return;
        }
        Pose3dc renderPose = subLevel.renderPose(partialTick);
        Vector3d transformedPosition = renderPose.transformPosition(new Vector3d(entityX.get(), entityY.get(), entityZ.get()));
        renderPoseShare.set((Object)renderPose);
        entityX.set(transformedPosition.x);
        entityY.set(transformedPosition.y);
        entityZ.set(transformedPosition.z);
    }

    @WrapOperation(method={"renderEntity"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")})
    private void renderEntity(EntityRenderDispatcher instance, Entity entity, double x, double y, double z, float g, float h, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, Operation<Void> original, @Share(value="renderPose") LocalRef<Pose3dc> renderPoseShare) {
        Pose3dc pose = (Pose3dc)renderPoseShare.get();
        if (pose != null) {
            MatrixStack matrixStack = VeilRenderBridge.create((PoseStack)poseStack);
            matrixStack.matrixPush();
            matrixStack.rotateAround(pose.orientation(), x, y, z);
            original.call(new Object[]{instance, entity, x, y, z, Float.valueOf(g), Float.valueOf(h), poseStack, multiBufferSource, i});
            matrixStack.matrixPop();
        } else {
            original.call(new Object[]{instance, entity, x, y, z, Float.valueOf(g), Float.valueOf(h), poseStack, multiBufferSource, i});
        }
    }
}
