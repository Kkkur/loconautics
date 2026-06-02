/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class CarriageContraptionEntityRenderer
extends ContraptionEntityRenderer<CarriageContraptionEntity> {
    public CarriageContraptionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(CarriageContraptionEntity entity, Frustum clippingHelper, double cameraX, double cameraY, double cameraZ) {
        Carriage carriage = entity.getCarriage();
        if (carriage != null) {
            for (CarriageBogey bogey : carriage.bogeys) {
                if (bogey == null) continue;
                bogey.couplingAnchors.replace(v -> null);
            }
        }
        return super.shouldRender(entity, clippingHelper, cameraX, cameraY, cameraZ);
    }

    @Override
    public void render(CarriageContraptionEntity entity, float yaw, float partialTicks, PoseStack ms, MultiBufferSource buffers, int overlay) {
        if (!entity.validForRender || entity.firstPositionUpdate) {
            return;
        }
        super.render(entity, yaw, partialTicks, ms, buffers, overlay);
        Carriage carriage = entity.getCarriage();
        if (carriage == null) {
            return;
        }
        Vec3 position = entity.getPosition(partialTicks);
        float viewYRot = entity.getViewYRot(partialTicks);
        float viewXRot = entity.getViewXRot(partialTicks);
        int bogeySpacing = carriage.bogeySpacing;
        carriage.bogeys.forEach(bogey -> {
            BlockPos bogeyPos;
            if (bogey == null) {
                return;
            }
            BlockPos blockPos = bogeyPos = bogey.isLeading ? BlockPos.ZERO : BlockPos.ZERO.relative(entity.getInitialOrientation().getCounterClockWise(), bogeySpacing);
            if (!VisualizationManager.supportsVisualization((LevelAccessor)entity.level()) && !entity.getContraption().isHiddenInPortal(bogeyPos)) {
                ms.pushPose();
                CarriageContraptionEntityRenderer.translateBogey(ms, bogey, bogeySpacing, viewYRot, viewXRot, partialTicks);
                int light = CarriageContraptionEntityRenderer.getBogeyLightCoords(entity, bogey, partialTicks);
                bogey.getStyle().render(bogey.getSize(), partialTicks, ms, buffers, light, overlay, bogey.wheelAngle.getValue(partialTicks), bogey.bogeyData, true);
                ms.popPose();
            }
            bogey.updateCouplingAnchor(position, viewXRot, viewYRot, bogeySpacing, partialTicks, bogey.isLeading);
            if (!carriage.isOnTwoBogeys()) {
                bogey.updateCouplingAnchor(position, viewXRot, viewYRot, bogeySpacing, partialTicks, !bogey.isLeading);
            }
        });
    }

    public static void translateBogey(PoseStack ms, CarriageBogey bogey, int bogeySpacing, float viewYRot, float viewXRot, float partialTicks) {
        boolean selfUpsideDown = bogey.isUpsideDown();
        boolean leadingUpsideDown = bogey.carriage.leadingBogey().isUpsideDown();
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(viewYRot + 90.0f)).rotateXDegrees(-viewXRot)).rotateYDegrees(180.0f)).translate(0.0f, 0.0f, bogey.isLeading ? 0.0f : (float)(-bogeySpacing)).rotateYDegrees(-180.0f)).rotateXDegrees(viewXRot)).rotateYDegrees(-viewYRot - 90.0f)).rotateYDegrees(bogey.yaw.getValue(partialTicks))).rotateXDegrees(bogey.pitch.getValue(partialTicks))).translate(0.0f, 0.5f, 0.0f).rotateZDegrees(selfUpsideDown ? 180.0f : 0.0f)).translateY(selfUpsideDown != leadingUpsideDown ? 2.0f : 0.0f);
    }

    public static int getBogeyLightCoords(CarriageContraptionEntity entity, CarriageBogey bogey, float partialTicks) {
        Vec3 anchorPosition = bogey.getAnchorPosition();
        BlockPos lightPos = BlockPos.containing((Position)(anchorPosition == null ? entity.getLightProbePosition(partialTicks) : anchorPosition));
        return LightTexture.pack((int)entity.level().getBrightness(LightLayer.BLOCK, lightPos), (int)entity.level().getBrightness(LightLayer.SKY, lightPos));
    }
}
