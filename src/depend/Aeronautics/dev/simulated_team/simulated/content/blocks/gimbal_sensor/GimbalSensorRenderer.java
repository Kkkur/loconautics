/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer
 *  com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package dev.simulated_team.simulated.content.blocks.gimbal_sensor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.gimbal_sensor.GimbalSensorBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.util.SimColors;
import dev.simulated_team.simulated.util.SimDirectionUtil;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class GimbalSensorRenderer
extends SafeBlockEntityRenderer<GimbalSensorBlockEntity> {
    public GimbalSensorRenderer(BlockEntityRendererProvider.Context context) {
    }

    protected void renderSafe(GimbalSensorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        FilteringRenderer.renderOnBlockEntity((SmartBlockEntity)be, (float)partialTicks, (PoseStack)ms, (MultiBufferSource)buffer, (int)light, (int)overlay);
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        VertexConsumer vb = buffer.getBuffer(RenderType.cutout());
        Quaternionf Q = be.getBaseQuaternion();
        ms.pushPose();
        ms.translate(0.5, 0.0, 0.5);
        for (Direction direction : SimDirectionUtil.Y_AXIS_PLANE) {
            ms.pushPose();
            SuperByteBuffer indicator = CachedBuffers.partial((PartialModel)SimPartialModels.GIMBAL_SENSOR_INDICATOR, (BlockState)be.getBlockState());
            indicator.rotateToFace(direction);
            indicator.translate(0.0, 0.0, -0.5);
            float signalStrength = (float)Math.max(be.getPower(direction), 0) / 15.0f;
            int color = SimColors.redstone(signalStrength);
            indicator.light(light).color(color).renderInto(ms, buffer.getBuffer(RenderType.cutout()));
            ms.popPose();
        }
        ms.popPose();
        be.applyPrimaryQuaternion(Q, partialTicks);
        this.apply(SimPartialModels.GIMBAL_SENSOR_GIMBAL, be, Q, light, ms, vb);
        be.applySecondaryQuaternion(Q, partialTicks);
        this.apply(SimPartialModels.GIMBAL_SENSOR_COMPASS, be, Q, light, ms, vb);
        be.applyCompassQuaternion(Q, partialTicks);
        this.apply(SimPartialModels.GIMBAL_SENSOR_NEEDLE, be, Q, light, ms, vb);
    }

    private void apply(PartialModel model, GimbalSensorBlockEntity te, Quaternionf Q, int light, PoseStack ms, VertexConsumer vb) {
        SuperByteBuffer buf = CachedBuffers.partial((PartialModel)model, (BlockState)te.getBlockState());
        buf.rotateCentered((Quaternionfc)Q);
        buf.translate(0.5, 0.5, 0.5);
        buf.light(light).renderInto(ms, vb);
    }
}
