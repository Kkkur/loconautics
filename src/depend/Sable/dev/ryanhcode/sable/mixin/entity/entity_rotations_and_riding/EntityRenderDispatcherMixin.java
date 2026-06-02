/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entity_rotations_and_riding;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.mixinhelpers.camera.camera_rotation.EntitySubLevelRotationHelper;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderDispatcher.class})
public class EntityRenderDispatcherMixin {
    @Shadow
    private Level level;
    @Unique
    private boolean sable$rotated = false;

    @Inject(method={"render"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V", shift=At.Shift.AFTER, ordinal=0)})
    private <E extends Entity> void sable$rotateEntity(E entity, double d, double e, double f, float g, float h, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (!EntitySubLevelUtil.shouldKick(entity)) {
            return;
        }
        Quaterniond orientation = EntitySubLevelRotationHelper.getEntityOrientation(entity, x -> ((ClientSubLevel)x).renderPose(), h, EntitySubLevelRotationHelper.Type.ENTITY);
        if (orientation == null) {
            return;
        }
        poseStack.pushPose();
        Vec3 eyeOffset = entity.getEyePosition().subtract(entity.position());
        Vec3 offset = Sable.HELPER.getEyePositionInterpolated(entity, h).subtract(entity.getEyePosition(h));
        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.translate(eyeOffset.x, eyeOffset.y, eyeOffset.z);
        poseStack.mulPose(new Quaternionf((Quaterniondc)orientation));
        poseStack.translate(-eyeOffset.x, -eyeOffset.y, -eyeOffset.z);
        this.sable$rotated = true;
    }

    @Inject(method={"render"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;isInvisible()Z")})
    private void sable$popPose1(Entity entity, double d, double e, double f, float g, float h, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (this.sable$rotated) {
            poseStack.popPose();
            this.sable$rotated = false;
        }
    }

    @Inject(method={"render"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", shift=At.Shift.BEFORE)})
    private void sable$popPose2(Entity entity, double d, double e, double f, float g, float h, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (this.sable$rotated) {
            poseStack.popPose();
            this.sable$rotated = false;
        }
    }
}
