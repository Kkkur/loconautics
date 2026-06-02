/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entities_stick_sublevels;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.mixinterface.entity.entities_stick_sublevels.EntityStickExtension;
import dev.ryanhcode.sable.mixinterface.entity.entities_stick_sublevels.LivingEntityStickExtension;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.entity_collision.SubLevelEntityCollision;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderDispatcher.class})
public class EntityRenderDispatcherMixin {
    @Inject(method={"renderHitbox"}, at={@At(value="TAIL")})
    private static void renderHitbox(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, float partialTicks, float g, float h, float i, CallbackInfo ci) {
        ClientSubLevel subLevel;
        EntityStickExtension duck;
        Vec3 plotPosition;
        SubLevel tracking = Sable.HELPER.getTrackingSubLevel(entity);
        if (tracking instanceof ClientSubLevel) {
            ClientSubLevel clientSubLevel = (ClientSubLevel)tracking;
            Quaterniondc customOrientation = EntitySubLevelUtil.getCustomEntityOrientation(entity, partialTicks);
            if (customOrientation == null) {
                customOrientation = JOMLConversion.QUAT_IDENTITY;
            }
            double yaw = SubLevelEntityCollision.getHitBoxYaw(clientSubLevel.renderPose());
            poseStack.pushPose();
            AABB bounds = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());
            poseStack.translate(0.0, (double)entity.getEyeHeight(), 0.0);
            poseStack.mulPose(new Quaternionf(customOrientation).rotateY((float)yaw));
            poseStack.translate(0.0, (double)(-entity.getEyeHeight()), 0.0);
            LevelRenderer.renderLineBox((PoseStack)poseStack, (VertexConsumer)vertexConsumer, (AABB)bounds, (float)1.0f, (float)1.0f, (float)0.0f, (float)0.4f);
            poseStack.popPose();
        }
        if ((plotPosition = (duck = (EntityStickExtension)entity).sable$getPlotPosition()) != null && (subLevel = (ClientSubLevel)Sable.HELPER.getContaining(entity.level(), (Position)plotPosition)) != null) {
            Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            Vec3 projectedPos = subLevel.renderPose().transformPosition(plotPosition);
            poseStack.popPose();
            AABB aABB = entity.getType().getSpawnAABB(projectedPos.x - cam.x, projectedPos.y - cam.y, projectedPos.z - cam.z);
            LevelRenderer.renderLineBox((PoseStack)poseStack, (VertexConsumer)vertexConsumer, (AABB)aABB, (float)0.0f, (float)1.0f, (float)0.0f, (float)0.2f);
            if (entity instanceof LivingEntityStickExtension) {
                LivingEntityStickExtension livingDuck = (LivingEntityStickExtension)entity;
                Vec3 serverProjectedPos = subLevel.renderPose().transformPosition(livingDuck.sable$getLerpTarget());
                AABB aABB3 = entity.getType().getSpawnAABB(serverProjectedPos.x - cam.x, serverProjectedPos.y - cam.y, serverProjectedPos.z - cam.z);
                LevelRenderer.renderLineBox((PoseStack)poseStack, (VertexConsumer)vertexConsumer, (AABB)aABB3, (float)1.0f, (float)0.0f, (float)1.0f, (float)0.2f);
            }
            poseStack.pushPose();
        }
    }
}
