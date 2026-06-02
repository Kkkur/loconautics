/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.Camera
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.debug_render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class LevelRendererMixin {
    @Shadow
    private ClientLevel level;

    @Inject(method={"renderLevel"}, at={@At(value="TAIL")})
    private void renderLevel(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes() || Minecraft.getInstance().showOnlyReducedInfo()) {
            return;
        }
        ClientSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer((RenderType)RenderType.LINES);
        double cx = camera.getPosition().x;
        double cy = camera.getPosition().y;
        double cz = camera.getPosition().z;
        PoseStack ps = new PoseStack();
        ps.mulPose(matrix4f);
        for (SubLevel subLevel : ((SubLevelContainer)container).getAllSubLevels()) {
            BoundingBox3dc bounds = subLevel.boundingBox();
            LevelRenderer.renderLineBox((PoseStack)ps, (VertexConsumer)consumer, (double)(bounds.minX() - cx), (double)(bounds.minY() - cy), (double)(bounds.minZ() - cz), (double)(bounds.maxX() - cx), (double)(bounds.maxY() - cy), (double)(bounds.maxZ() - cz), (float)0.5f, (float)0.5f, (float)0.5f, (float)0.7f);
            ps.pushPose();
            Pose3dc renderPose = ((ClientSubLevel)subLevel).renderPose();
            BoundingBox3ic plotBounds = subLevel.getPlot().getBoundingBox();
            Vector3dc globalCenter = renderPose.position();
            Vector3dc localCenter = renderPose.rotationPoint();
            ps.translate(globalCenter.x() - cx, globalCenter.y() - cy, globalCenter.z() - cz);
            ps.mulPose(new Quaternionf(renderPose.orientation()));
            LevelRenderer.renderLineBox((PoseStack)ps, (VertexConsumer)consumer, (double)-0.125, (double)-0.125, (double)-0.125, (double)0.125, (double)0.125, (double)0.125, (float)0.7f, (float)0.7f, (float)0.5f, (float)1.0f);
            LevelRenderer.renderLineBox((PoseStack)ps, (VertexConsumer)consumer, (double)((double)plotBounds.minX() - localCenter.x()), (double)((double)plotBounds.minY() - localCenter.y()), (double)((double)plotBounds.minZ() - localCenter.z()), (double)((double)plotBounds.maxX() + 1.0 - localCenter.x()), (double)((double)plotBounds.maxY() + 1.0 - localCenter.y()), (double)((double)plotBounds.maxZ() + 1.0 - localCenter.z()), (float)0.9f, (float)0.5f, (float)0.5f, (float)1.0f);
            ps.popPose();
        }
        bufferSource.endLastBatch();
    }
}
