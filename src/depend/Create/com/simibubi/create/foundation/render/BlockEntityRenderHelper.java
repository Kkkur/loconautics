/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  dev.engine_room.flywheel.lib.visualization.VisualizationHelper
 *  javax.annotation.Nullable
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 */
package com.simibubi.create.foundation.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import java.util.BitSet;
import java.util.List;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

public class BlockEntityRenderHelper {
    public static void renderBlockEntities(List<BlockEntity> blockEntities, BitSet shouldRenderBEs, BitSet erroredBEsOut, @javax.annotation.Nullable VirtualRenderWorld renderLevel, Level realLevel, PoseStack ms, @javax.annotation.Nullable Matrix4f lightTransform, MultiBufferSource buffer, float pt) {
        int i = shouldRenderBEs.nextSetBit(0);
        while (i >= 0 && i < blockEntities.size()) {
            BlockEntity blockEntity = blockEntities.get(i);
            if (!VisualizationManager.supportsVisualization((LevelAccessor)realLevel) || !VisualizationHelper.skipVanillaRender((BlockEntity)blockEntity)) {
                BlockEntityRenderer renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);
                if (renderer == null) {
                    erroredBEsOut.set(i);
                } else {
                    BlockPos pos = blockEntity.getBlockPos();
                    ms.pushPose();
                    TransformStack.of((PoseStack)ms).translate((Vec3i)pos);
                    try {
                        int light;
                        int realLevelLight = LevelRenderer.getLightColor((BlockAndTintGetter)realLevel, (BlockPos)BlockEntityRenderHelper.getLightPos(lightTransform, pos));
                        if (renderLevel != null) {
                            renderLevel.setExternalLight(realLevelLight);
                            light = LevelRenderer.getLightColor((BlockAndTintGetter)renderLevel, (BlockPos)pos);
                        } else {
                            light = realLevelLight;
                        }
                        renderer.render(blockEntity, pt, ms, buffer, light, OverlayTexture.NO_OVERLAY);
                    }
                    catch (Exception e) {
                        erroredBEsOut.set(i);
                        String message = "BlockEntity " + String.valueOf(RegisteredObjectsHelper.getKeyOrThrow((BlockEntityType)blockEntity.getType())) + " could not be rendered virtually.";
                        if (((Boolean)AllConfigs.client().explainRenderErrors.get()).booleanValue()) {
                            Create.LOGGER.error(message, (Throwable)e);
                        }
                        Create.LOGGER.error(message);
                    }
                    ms.popPose();
                }
            }
            i = shouldRenderBEs.nextSetBit(i + 1);
        }
        if (renderLevel != null) {
            renderLevel.resetExternalLight();
        }
    }

    private static BlockPos getLightPos(@Nullable Matrix4f lightTransform, BlockPos contraptionPos) {
        if (lightTransform != null) {
            Vector4f lightVec = new Vector4f((float)contraptionPos.getX() + 0.5f, (float)contraptionPos.getY() + 0.5f, (float)contraptionPos.getZ() + 0.5f, 1.0f);
            lightVec.mul((Matrix4fc)lightTransform);
            return BlockPos.containing((double)lightVec.x(), (double)lightVec.y(), (double)lightVec.z());
        }
        return contraptionPos;
    }
}
