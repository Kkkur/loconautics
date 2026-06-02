/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.debug.DebugRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 */
package dev.ryanhcode.sable.mixin.debug_render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value={DebugRenderer.class})
public class DebugRendererMixin {
    @Overwrite
    public static void renderFilledBox(PoseStack poseStack, MultiBufferSource bufferSource, BlockPos blockPos, float f, float g, float h, float i, float j) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (camera.isInitialized()) {
            ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Vec3i)blockPos);
            if (subLevel != null) {
                poseStack.pushPose();
                Pose3dc renderPose = subLevel.renderPose();
                Vec3 pos = renderPose.transformPosition(blockPos.getCenter()).subtract(camera.getPosition());
                poseStack.translate(pos.x, pos.y, pos.z);
                poseStack.mulPose(new Quaternionf(renderPose.orientation()));
                DebugRenderer.renderFilledBox((PoseStack)poseStack, (MultiBufferSource)bufferSource, (AABB)new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0).inflate(0.5).inflate((double)f), (float)g, (float)h, (float)i, (float)j);
                poseStack.popPose();
                return;
            }
            Vec3 relativePos = camera.getPosition().reverse();
            AABB box = new AABB(blockPos).move(relativePos).inflate((double)f);
            DebugRenderer.renderFilledBox((PoseStack)poseStack, (MultiBufferSource)bufferSource, (AABB)box, (float)g, (float)h, (float)i, (float)j);
        }
    }
}
