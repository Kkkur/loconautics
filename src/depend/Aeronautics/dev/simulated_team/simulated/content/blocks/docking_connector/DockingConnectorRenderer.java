/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector2f
 *  org.joml.Vector2fc
 */
package dev.simulated_team.simulated.content.blocks.docking_connector;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class DockingConnectorRenderer
extends SafeBlockEntityRenderer<DockingConnectorBlockEntity> {
    public DockingConnectorRenderer(BlockEntityRendererProvider.Context context) {
    }

    protected void renderSafe(DockingConnectorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        VertexConsumer vb = bufferSource.getBuffer(RenderType.cutout());
        Direction direction = (Direction)be.getBlockState().getValue((Property)BlockStateProperties.FACING);
        BlockState blockState = be.getBlockState();
        float extension = be.getExtensionDistance(partialTicks);
        float rotation = be.getFeetRotation(partialTicks) * 90.0f;
        SuperByteBuffer piston1 = CachedBuffers.partial((PartialModel)SimPartialModels.DOCKING_CONNECTOR_MAIN_PISTON_BOTTOM, (BlockState)blockState);
        SuperByteBuffer piston2 = CachedBuffers.partial((PartialModel)SimPartialModels.DOCKING_CONNECTOR_MAIN_PISTON_TOP, (BlockState)blockState);
        SuperByteBuffer sidePiston1 = CachedBuffers.partial((PartialModel)SimPartialModels.DOCKING_CONNECTOR_SIDE_PISTON_BOTTOM, (BlockState)blockState);
        SuperByteBuffer sidePiston2 = CachedBuffers.partial((PartialModel)SimPartialModels.DOCKING_CONNECTOR_SIDE_PISTON_TOP, (BlockState)blockState);
        SuperByteBuffer foot = CachedBuffers.partial((PartialModel)SimPartialModels.DOCKING_CONNECTOR_FOOT, (BlockState)blockState);
        ms.pushPose();
        DockingConnectorRenderer.rotateToFaceCentered(ms, direction);
        piston1.translate(0.0, (double)extension * 0.5, 0.0);
        piston2.translate(0.0f, extension, 0.0f);
        piston1.light(light).renderInto(ms, vb);
        piston2.light(light).renderInto(ms, vb);
        Vector2f footAnchor = new Vector2f();
        Vector2f sidePistonTopAnchor = new Vector2f();
        Vector2f sidePistonBottomAnchor = new Vector2f();
        Vector2f relativeAnchor = new Vector2f();
        footAnchor.set(-7.5f, 15.5f).div(16.0f).add(0.0f, extension);
        this.rotateVector2f(sidePistonTopAnchor.set(1.5f, -2.5f).div(16.0f), rotation).add((Vector2fc)footAnchor);
        sidePistonBottomAnchor.set(-6.0f, 2.0f).div(16.0f).add(0.0f, extension / 2.0f);
        relativeAnchor.set((Vector2fc)sidePistonTopAnchor).sub((Vector2fc)sidePistonBottomAnchor);
        relativeAnchor.normalize();
        Matrix4f rotationMatrix = new Matrix4f(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, relativeAnchor.y, relativeAnchor.x, 0.0f, 0.0f, -relativeAnchor.x, relativeAnchor.y, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        for (int i = 0; i < 4; ++i) {
            ms.pushPose();
            ms.translate(0.5, 0.0, 0.5);
            TransformStack.of((PoseStack)ms).rotateYDegrees((float)(i * 90));
            sidePiston1.translate(0.0f, sidePistonBottomAnchor.y, sidePistonBottomAnchor.x);
            sidePiston2.translate(0.0f, sidePistonTopAnchor.y, sidePistonTopAnchor.x);
            foot.translate(0.0f, footAnchor.y, footAnchor.x);
            foot.rotateXDegrees(rotation);
            sidePiston1.mulPose((Matrix4fc)rotationMatrix);
            sidePiston2.mulPose((Matrix4fc)rotationMatrix);
            sidePiston1.light(light).renderInto(ms, vb);
            sidePiston2.light(light).renderInto(ms, vb);
            foot.light(light).renderInto(ms, vb);
            ms.popPose();
        }
        ms.popPose();
    }

    public int getViewDistance() {
        return 256;
    }

    public static void rotateToFaceCentered(PoseStack ms, Direction facing) {
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing))).rotateXDegrees(AngleHelper.verticalAngle((Direction)facing) + 90.0f)).uncenter();
    }

    private Vector2f rotateVector2f(Vector2f v, float angle) {
        angle = (float)Math.toRadians(angle);
        float s = Mth.sin((float)angle);
        float c = Mth.cos((float)angle);
        v.set(v.x * c + v.y * s, v.y * c - v.x * s);
        return v;
    }
}
